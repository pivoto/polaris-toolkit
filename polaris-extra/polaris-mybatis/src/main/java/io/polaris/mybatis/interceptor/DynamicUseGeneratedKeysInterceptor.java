package io.polaris.mybatis.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
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
import io.polaris.mybatis.annotation.DynamicUseGeneratedKeys;
import io.polaris.mybatis.annotation.MapperEntity;
import io.polaris.mybatis.mapper.EntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

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
	@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class DynamicUseGeneratedKeysInterceptor implements Interceptor {
	/**
	 * 存储与某个 SQL 映射语句相关的元数据信息。
	 */
	private static class Meta {
		private final List<ColumnMeta> autoColumns;
		private final String entityKey;
		private MappedStatement lastStatement;
		private MappedStatement newerStatement;

		public Meta(List<ColumnMeta> autoColumns, String entityKey) {
			this.autoColumns = autoColumns;
			this.entityKey = entityKey;
		}
	}

	/**
	 * 缓存已解析的元数据信息，键为 SQL 映射语句 ID。
	 */
	private final Map<String, Meta> cache = new ConcurrentHashMap<>();

	/**
	 * 获取 Mapper 接口或方法上声明的实体类。
	 *
	 * @param mapperMethod 当前调用的方法对象
	 * @param mapperType   Mapper 接口类型
	 * @return 实体类 Class 对象
	 * @throws IllegalStateException 如果无法确定实体类则抛出异常
	 */
	protected static Class<?> getEntityClass(Method mapperMethod, Class<?> mapperType) {
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
		// 只处理插入情况
		if (mappedStatement.getSqlCommandType() == SqlCommandType.INSERT) {
			String statementId = mappedStatement.getId();
			KeyGenerator keyGenerator = mappedStatement.getKeyGenerator();
			// 忽略已设置KeyGenerator的情况
			if (keyGenerator instanceof NoKeyGenerator) {
				Meta meta = cache.computeIfAbsent(statementId, k -> buildMeta(statementId));
				if (meta.autoColumns != null) {
					MappedStatement newer;
					if (meta.lastStatement == mappedStatement) {
						// 复用上次
						newer = meta.newerStatement;
					} else {
						// 生成一个修改配置后的新 MappedStatement
						newer = new StatementBuilder(mappedStatement).useGeneratedKeys(meta.autoColumns, Strings.trimToNull(meta.entityKey)).build();
						meta.lastStatement = mappedStatement;
						meta.newerStatement = newer;
					}
					invocation.getArgs()[0] = newer;
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
					DynamicUseGeneratedKeys dynamicUseGeneratedKeys = method.getAnnotation(DynamicUseGeneratedKeys.class);
					Class<?> entityClass = dynamicUseGeneratedKeys.entity();
					if (entityClass == void.class) {
						entityClass = getEntityClass(method, mapperClass);
					}
					String entityKey = dynamicUseGeneratedKeys.value();
					TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
					List<ColumnMeta> columns = tableMeta.getColumns().values().stream()
						.filter(columnMeta ->
							columnMeta.isPrimaryKey() &&
								(
									columnMeta.isAutoIncrement() || Strings.isNotBlank(columnMeta.getSeqName()) || Strings.isNotBlank(columnMeta.getIdSql())
								)
						)
						.collect(Collectors.toList());
					if (!columns.isEmpty()) {
						return new Meta(columns, entityKey);
					}
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return new Meta(null, null);
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
}
