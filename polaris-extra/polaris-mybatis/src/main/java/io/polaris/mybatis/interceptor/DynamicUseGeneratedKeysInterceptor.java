package io.polaris.mybatis.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

/**
 * @author Qt
 * @since Jul 04, 2024
 */
@Slf4j
@Intercepts({
	@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class DynamicUseGeneratedKeysInterceptor implements Interceptor {

	private final Map<String, Meta> cache = new ConcurrentHashMap<>();

	protected static Class<?> getEntityClass(Method mapperMethod, Class<?> mapperType) {
		MapperEntity declared = mapperMethod.getAnnotation(MapperEntity.class);
		if (declared != null) {
			return declared.entity();
		}
		Class<?> entityClass = null;
		if (EntityMapper.class.isAssignableFrom(mapperType)) {
			Type actualType = JavaType.of(mapperType).getActualType(EntityMapper.class, 0);
			entityClass = Types.getClass(actualType);
		}
		if (entityClass == null || entityClass == Object.class) {
			throw new IllegalStateException("未知实体类型！");
		}
		return entityClass;
	}

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

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		// 只处理插入情况
		if (mappedStatement.getSqlCommandType() == SqlCommandType.INSERT) {
			String statementId = mappedStatement.getId();

			Meta meta = cache.computeIfAbsent(statementId, k -> {
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
								.filter(ColumnMeta::isAutoIncrement)
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
			});
			if (meta.autoColumns != null) {
				MappedStatement newer;
				if (meta.lastStatement == mappedStatement) {
					// 复用上次
					newer = meta.newerStatement;
				} else {
					// 生成一个修改配置后的新 MappedStatement
					newer = new StatementBuilder(mappedStatement).useGeneratedKeys(meta.autoColumns, Strings.trimToNull(meta.entityKey));
					meta.lastStatement = mappedStatement;
					meta.newerStatement = newer;
				}
				invocation.getArgs()[0] = newer;
			}
		}
		return invocation.proceed();
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
