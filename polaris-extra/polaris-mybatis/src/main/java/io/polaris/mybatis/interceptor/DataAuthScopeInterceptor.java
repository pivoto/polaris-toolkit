package io.polaris.mybatis.interceptor;

import java.lang.reflect.Method;

import io.polaris.mybatis.auth.DataAuthCtx;
import io.polaris.mybatis.auth.annotation.DataScope;
import io.polaris.mybatis.support.InterceptorHelper;
import io.polaris.mybatis.support.MapperHelperContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
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
	@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
	@Signature(type = Executor.class, method = "queryCursor", args = {MappedStatement.class, Object.class, RowBounds.class}),
})
public class DataAuthScopeInterceptor implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		if (mappedStatement.getSqlCommandType() == SqlCommandType.SELECT) {
			String statementId = mappedStatement.getId();
			Method statementMethod = InterceptorHelper.instance().getStatementMethod(statementId);
			if (statementMethod != null) {
				DataScope dataScope = statementMethod.getAnnotation(DataScope.class);
				if (dataScope != null) {
					return DataAuthCtx.executeThrowably(invocation::proceed, dataScope);
				}
			}
		}
		return invocation.proceed();
	}

}
