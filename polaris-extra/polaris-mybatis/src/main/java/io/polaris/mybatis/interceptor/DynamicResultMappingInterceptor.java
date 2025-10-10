package io.polaris.mybatis.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.TableMetaKit;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.Strings;
import io.polaris.mybatis.annotation.ColumnJdbcType;
import io.polaris.mybatis.annotation.ColumnTypeHandler;
import io.polaris.mybatis.annotation.DynamicResultMapping;
import io.polaris.mybatis.annotation.DynamicUseGeneratedKeys;
import io.polaris.mybatis.annotation.MapperEntity;
import io.polaris.mybatis.consts.MappingKeys;
import io.polaris.mybatis.mapper.EntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * 动态启用 MyBatis 的 useGeneratedKeys 特性的拦截器。
 * <p>
 * 该拦截器用于在执行 INSERT 操作时动态判断是否需要开启 useGeneratedKeys，
 * 并根据实体类中的自增列信息构建新的 MappedStatement 来替换原始语句。
 * </p>
 *
 * @author Qt
 * @since Jul 04, 2024
 */
@Slf4j
@Intercepts({
	@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
	@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
	@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class DynamicResultMappingInterceptor implements Interceptor {


	/**
	 * 缓存已解析的元数据信息，键为 SQL 映射语句 ID。
	 */
	private final Map<String, Meta> cache = new ConcurrentHashMap<>();

	/**
	 * 拦截 MyBatis 执行过程，在 INSERT 操作中动态启用 useGeneratedKeys。
	 *
	 * @param invocation 调用上下文对象
	 * @return 原始调用结果
	 * @throws Throwable 若发生错误则向上抛出
	 */
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		// 处理查询情况，注入结果集映射, 通过 ms.resource名标识是否已成功注入（`原名称|标识`)，加入缓存
		if (mappedStatement.getSqlCommandType() == SqlCommandType.SELECT) {
			String statementId = mappedStatement.getId();
			String resource = mappedStatement.getResource();
			if (resource == null || !resource.contains(StatementBuilder.RESOURCE_MARK)) {
				Meta meta = cache.computeIfAbsent(statementId, k -> buildMeta(statementId));
				List<ResultMappingMeta> resultMappingMetas = meta.resultMappings;
				if (resultMappingMetas != null) {
					MappedStatement newer;
					if (meta.lastStatement == mappedStatement) {
						// 复用上次
						newer = meta.newerStatement;
					} else {
						// 生成一个修改配置后的新 MappedStatement
						StatementBuilder statementBuilder = new StatementBuilder(mappedStatement);
						List<ResultMapping> resultMappings = new ArrayList<>();
						for (ResultMappingMeta resultMappingMeta : resultMappingMetas) {
							ResultMapping.Builder builder = new ResultMapping.Builder(statementBuilder.getConfiguration(), resultMappingMeta.property);
							if (resultMappingMeta.typeHandler != null) {
								builder.typeHandler(resultMappingMeta.typeHandler);
							} else if (resultMappingMeta.typeHandlerClass != null) {
								TypeHandler<?> typeHandler = statementBuilder.resolveTypeHandler(resultMappingMeta.javaType, resultMappingMeta.typeHandlerClass);
								resultMappingMeta.typeHandler = typeHandler;
								builder.typeHandler(typeHandler);
							}
							if (resultMappingMeta.column != null) {
								builder.column(resultMappingMeta.column);
							}
							if (resultMappingMeta.columnPrefix != null) {
								builder.columnPrefix(resultMappingMeta.columnPrefix);
							}
							resultMappings.add(builder.build());
						}
						statementBuilder.useResultMappings(meta.resultJavaType, resultMappings);
						newer = statementBuilder.build();
						meta.lastStatement = mappedStatement;
						meta.newerStatement = newer;
					}
					invocation.getArgs()[0] = newer;
				}
			}
		}
		// 处理插入情况
		else if (mappedStatement.getSqlCommandType() == SqlCommandType.INSERT) {
			String statementId = mappedStatement.getId();
			KeyGenerator keyGenerator = mappedStatement.getKeyGenerator();
			// 忽略已设置KeyGenerator的情况
			if (keyGenerator instanceof NoKeyGenerator) {
				String resource = mappedStatement.getResource();
				if (resource == null || !resource.contains(StatementBuilder.RESOURCE_MARK)) {
					Meta meta = cache.computeIfAbsent(statementId, k -> buildMeta(statementId));
					if (meta.autoColumns != null) {
						MappedStatement newer;
						if (meta.lastStatement == mappedStatement) {
							// 复用上次
							newer = meta.newerStatement;
						} else {
							// 生成一个修改配置后的新 MappedStatement
							StatementBuilder statementBuilder = new StatementBuilder(mappedStatement);
							statementBuilder.useGeneratedKeys(meta.autoColumns, Strings.trimToNull(meta.autoColumnPrefix));
							newer = statementBuilder.build();
							meta.lastStatement = mappedStatement;
							meta.newerStatement = newer;
						}
						invocation.getArgs()[0] = newer;
					}
				}
			}
		}
		return invocation.proceed();
	}

	@Nonnull
	private Meta buildMeta(String statementId) {
		try {
			int idx = statementId.lastIndexOf(".");
			String mapperClassName = statementId.substring(0, idx);
			String mapperMethodName = statementId.substring(idx + 1);
			Class<?> mapperClass = Class.forName(mapperClassName);
			if (mapperClass.isInterface()) {
				List<Method> methods = Reflects.getPublicMethods(mapperClass, m ->
					!m.isDefault() && !Modifier.isStatic(m.getModifiers())
						&& mapperMethodName.equals(m.getName())
						&& m.isAnnotationPresent(DynamicUseGeneratedKeys.class));
				if (methods.size() == 1) {
					Method method = methods.get(0);
					Class<?> declaredEntityClass = null;

					DynamicUseGeneratedKeys dynamicUseGeneratedKeys = method.getAnnotation(DynamicUseGeneratedKeys.class);
					String autoColumnPrefix = dynamicUseGeneratedKeys.value();
					List<ColumnMeta> autoColumns = null;

					{
						Class<?> entityClass = dynamicUseGeneratedKeys.entity();
						if (entityClass == void.class) {
							entityClass = declaredEntityClass = findDeclaredEntityClass(method, mapperClass);
						}
						TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
						autoColumns = tableMeta.getColumns().values().stream()
							.filter(columnMeta ->
								columnMeta.isPrimaryKey() &&
									(
										columnMeta.isAutoIncrement() || Strings.isNotBlank(columnMeta.getSeqName()) || Strings.isNotBlank(columnMeta.getIdSql())
									)
							)
							.collect(Collectors.toList());
						if (autoColumns.isEmpty()) {
							autoColumns = null;
						}
					}

					DynamicResultMapping dynamicResultMapping = method.getAnnotation(DynamicResultMapping.class);
					List<ResultMappingMeta> resultMappings = null;
					Class<?> resultJavaType = null;
					{
						resultJavaType = dynamicResultMapping.entity();
						if (resultJavaType == void.class) {
							if (declaredEntityClass != null) {
								resultJavaType = declaredEntityClass;
							} else {
								resultJavaType = findDeclaredEntityClass(method, mapperClass);
							}
						}
						boolean useColumnName = dynamicResultMapping.useColumnName();
						String columnPrefix = dynamicResultMapping.columnPrefix();
						TableMeta tableMeta = TableMetaKit.instance().get(resultJavaType);
						resultMappings = new ArrayList<>();
						for (ColumnMeta col : tableMeta.getColumns().values()) {
							Map<String, String> properties = col.getProperties();
							boolean match = properties.keySet().stream().anyMatch(MappingKeys.PARAMETER_MAPPING_KEYS::contains);
							// 是否存在
							if (match) {
								try {
									ResultMappingMeta resultMappingMeta = new ResultMappingMeta();
									String typeHandler = properties.get(ColumnTypeHandler.KEY);
									if (Strings.isNotBlank(typeHandler)) {
										Class<?> clazz = Class.forName(typeHandler);
										if (TypeHandler.class.isAssignableFrom(clazz)) {
											//noinspection unchecked
											resultMappingMeta.typeHandlerClass = (Class<? extends TypeHandler<?>>) clazz;
										}
									}
									String jdbcType = properties.get(ColumnJdbcType.KEY);
									if (Strings.isNotBlank(jdbcType)) {
										resultMappingMeta.jdbcType = JdbcType.valueOf(jdbcType);
									}
									resultMappingMeta.property = col.getFieldName();
									resultMappingMeta.column = useColumnName ? col.getColumnName() : col.getFieldName();
									resultMappingMeta.javaType = col.getFieldType();
									resultMappingMeta.columnPrefix = Strings.trimToNull(columnPrefix);
									resultMappings.add(resultMappingMeta);
								} catch (Exception ignored) {
									// 忽略异常
								}
							}
						}
						if (resultMappings.isEmpty()) {
							resultMappings = null;
						}
					}

					return new Meta(autoColumns, autoColumnPrefix, resultMappings, resultJavaType);
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return new Meta(null, null, null, null);
	}

	/**
	 * 获取 Mapper 接口或方法上声明的实体类。
	 *
	 * @param mapperMethod 当前调用的方法对象
	 * @param mapperType   Mapper 接口类型
	 * @return 实体类 Class 对象
	 * @throws IllegalStateException 如果无法确定实体类则抛出异常
	 */
	protected Class<?> findDeclaredEntityClass(Method mapperMethod, Class<?> mapperType) {
		Class<?> entityClass = null;
		MapperEntity declared = mapperMethod.getAnnotation(MapperEntity.class);
		if (declared != null) {
			entityClass = declared.entity();
			if (entityClass != null && entityClass != Object.class) {
				return entityClass;
			}
		}
		// 从Mapper接口类上获取
		declared = mapperType.getAnnotation(MapperEntity.class);
		if (declared != null) {
			entityClass = declared.entity();
			if (entityClass != null && entityClass != Object.class) {
				return entityClass;
			}
		}
		// 从方法所在接口类上获取
		Class<?> declaringClass = mapperMethod.getDeclaringClass();
		if (declaringClass != mapperType) {
			declared = declaringClass.getAnnotation(MapperEntity.class);
			if (declared != null) {
				entityClass = declared.entity();
				if (entityClass != null && entityClass != Object.class) {
					return entityClass;
				}
			}
		}

		if (EntityMapper.class.isAssignableFrom(mapperType)) {
			Type actualType = JavaType.of(mapperType).getActualType(EntityMapper.class, 0);
			entityClass = Types.getClass(actualType);
		}
		if (entityClass == null || entityClass == Object.class) {
			throw new IllegalStateException("未知实体类型！");
		}
		return entityClass;
	}

	// 实现 cloneMappedStatement 方法
	private MappedStatement cloneMappedStatement(MappedStatement ms, boolean useGeneratedKeys) {
		// 克隆逻辑，设置 useGeneratedKeys 配置
		return null;
	}

	@Override
	public Object plugin(Object target) {
		return Interceptor.super.plugin(target);
	}

	@Override
	public void setProperties(Properties properties) {
	}

	/**
	 * 存储与某个 SQL 映射语句相关的元数据信息。
	 */
	private static class Meta {
		private final List<ColumnMeta> autoColumns;
		private final String autoColumnPrefix;
		private final List<ResultMappingMeta> resultMappings;
		private final Class<?> resultJavaType;

		private MappedStatement lastStatement;
		private MappedStatement newerStatement;

		public Meta(List<ColumnMeta> autoColumns, String autoColumnPrefix, List<ResultMappingMeta> resultMappings, Class<?> resultJavaType) {
			this.autoColumns = autoColumns;
			this.autoColumnPrefix = autoColumnPrefix;
			this.resultMappings = resultMappings;
			this.resultJavaType = resultJavaType;
		}

	}


	private static class ResultMappingMeta {
		private String property;
		private String column;
		private Class<?> javaType;
		private JdbcType jdbcType;
		private Class<? extends TypeHandler<?>> typeHandlerClass;
		private TypeHandler<?> typeHandler;
		private String columnPrefix;
	}

}
