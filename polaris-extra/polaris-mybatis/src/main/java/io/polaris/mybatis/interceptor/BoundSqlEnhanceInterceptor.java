package io.polaris.mybatis.interceptor;

import java.sql.Connection;
import java.sql.Statement;

import io.polaris.mybatis.support.MapperHelperContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * @author Qt
 * @since Mar 15, 2026
 */
@Slf4j
@Intercepts({
	@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
	@Signature(type = Executor.class, method = "createCacheKey", args = {MappedStatement.class, Object.class, RowBounds.class, BoundSql.class}),

	@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
	@Signature(type = StatementHandler.class, method = "parameterize", args = {Statement.class}),
	@Signature(type = StatementHandler.class, method = "batch", args = {Statement.class}),
	@Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
	@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
	@Signature(type = StatementHandler.class, method = "queryCursor", args = {Statement.class}),
	@Signature(type = StatementHandler.class, method = "getBoundSql", args = {})
})
public class BoundSqlEnhanceInterceptor implements Interceptor {

	public static void enableEnhanceBoundSql(boolean enable) {
		MapperHelperContext.enableEnhanceBoundSql(enable);
	}

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		// 在执行真正的逻辑之前，对BoundSql参数进行增强，添加额外的变量参数值
		if (MapperHelperContext.isEnabledEnhanceBoundSql()) {
			Object target = invocation.getTarget();
			if (target instanceof Executor) {
				Object[] args = invocation.getArgs();
				if (args.length > 0) {
					Object arg = args[args.length - 1];
					if (arg instanceof BoundSql) {
						MapperHelperContext.enhanceBoundSql((BoundSql) arg);
					}
				}
			} else if (target instanceof StatementHandler) {
				MapperHelperContext.enhanceBoundSql(((StatementHandler) target).getBoundSql());
			}
		}
		return invocation.proceed();
	}

}
