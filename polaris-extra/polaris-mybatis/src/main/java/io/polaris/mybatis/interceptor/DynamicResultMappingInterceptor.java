package io.polaris.mybatis.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.Strings;
import io.polaris.mybatis.annotation.DynamicResultMapping;
import io.polaris.mybatis.annotation.DynamicUseGeneratedKeys;
import io.polaris.mybatis.enhance.EnhancedAutoMapperHelper;
import io.polaris.mybatis.enhance.EnhancedAutoMapperMeta;
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
		SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
		// 处理查询情况，注入结果集映射
		// 处理插入情况，注入主键生成器
		if (sqlCommandType == SqlCommandType.SELECT || sqlCommandType == SqlCommandType.INSERT) {
			String statementId = mappedStatement.getId();
			String resource = mappedStatement.getResource();
			// 通过 ms.resource名标识是否已成功注入（`原名称|标识`)，加入缓存
			if (resource == null || !resource.contains(StatementBuilder.RESOURCE_MARK)) {
				Meta meta = cache.computeIfAbsent(statementId, k -> buildMeta(mappedStatement));
				EnhancedAutoMapperMeta autoMapperMeta = meta.autoMapperMeta;
				if (autoMapperMeta != null) {
					MappedStatement newer;
					if (meta.lastStatement == mappedStatement) {
						// 复用上次
						newer = meta.newerStatement;
					} else {
						// 生成一个修改配置后的新 MappedStatement
						StatementBuilder statementBuilder = new StatementBuilder(mappedStatement);
						boolean modified = autoMapperMeta.applyTo(statementBuilder, sqlCommandType);
						if (modified) {
							newer = statementBuilder.build();
						} else {
							newer = mappedStatement;
						}
						meta.modified = modified;
						meta.lastStatement = mappedStatement;
						meta.newerStatement = newer;
					}
					if (meta.modified) {
						invocation.getArgs()[0] = newer;
					}
				}
			}
		}
		return invocation.proceed();
	}

	@Nonnull
	private Meta buildMeta(MappedStatement mappedStatement) {
		try {
			String statementId = mappedStatement.getId();
			int idx = statementId.lastIndexOf(".");
			String mapperClassName = statementId.substring(0, idx);
			String mapperMethodName = statementId.substring(idx + 1);
			Class<?> mapperClass = Class.forName(mapperClassName);
			if (mapperClass.isInterface()) {
				List<Method> methods = Reflects.getPublicMethods(mapperClass, m ->
					!m.isDefault() && !Modifier.isStatic(m.getModifiers())
						&& mapperMethodName.equals(m.getName())
						&& (m.isAnnotationPresent(DynamicUseGeneratedKeys.class) || m.isAnnotationPresent(DynamicResultMapping.class)));
				if (methods.size() == 1) {
					Method method = methods.get(0);
					String databaseId = mappedStatement.getDatabaseId();
					Class<?> declaredEntityClass = EnhancedAutoMapperHelper.getEntityClassOrNull(method, mapperClass, databaseId);
					EnhancedAutoMapperMeta autoMapperMeta = EnhancedAutoMapperHelper.parseEnhancedAutoMapperMeta(method, mapperClass, declaredEntityClass);
					return new Meta(autoMapperMeta);
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return new Meta(null);
	}


	@Override
	public Object plugin(Object target) {
		return Interceptor.super.plugin(target);
	}

	/**
	 * 存储与某个 SQL 映射语句相关的元数据信息。
	 */
	private static class Meta {
		private final EnhancedAutoMapperMeta autoMapperMeta;

		private boolean modified;
		private MappedStatement lastStatement;
		private MappedStatement newerStatement;

		private Meta(EnhancedAutoMapperMeta autoMapperMeta) {
			this.autoMapperMeta = autoMapperMeta;
		}
	}


}
