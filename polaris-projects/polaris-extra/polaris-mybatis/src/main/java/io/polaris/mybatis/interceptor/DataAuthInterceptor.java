package io.polaris.mybatis.interceptor;

import io.polaris.core.reflect.Reflects;
import io.polaris.mybatis.util.SqlParsers;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;

/**
 * @author Qt
 * @since 1.8,  Aug 28, 2023
 */
@Intercepts({
	@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
@Slf4j
public class DataAuthInterceptor implements Interceptor {
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		if (!DataAuthSqlCtx.hasDataAuthority()) {
			return invocation.proceed();
		}
		try {
			if (invocation.getTarget() instanceof RoutingStatementHandler) {
				RoutingStatementHandler statementHandler = (RoutingStatementHandler) invocation.getTarget();
				StatementHandler delegate = getDelegate(statementHandler);
				MappedStatement mappedStatement = getMappedStatement(delegate);

				if (mappedStatement.getSqlCommandType() == SqlCommandType.SELECT) {
					BoundSql boundSql = delegate.getBoundSql();
					// 解析sql
					String sql = boundSql.getSql();
					// 查询权限拦截
					String resolvedSql = SqlParsers.visitSelect(sql, DataAuthSqlCtx.getConditionAppender(), DataAuthSqlCtx.getColumnFilter());
					// 拦截后的SQL更新至BoundSql
					Field field = Reflects.getField(BoundSql.class, "sql" );
					Reflects.setAccessible(field);
					field.set(boundSql, resolvedSql);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		if (target instanceof StatementHandler) {
			return Plugin.wrap(target, this);
		} else {
			return target;
		}
	}

	@Override
	public void setProperties(Properties properties) {
	}


	private StatementHandler getDelegate(RoutingStatementHandler statementHandler) throws IllegalAccessException {
		Field field = Reflects.getField(RoutingStatementHandler.class, "delegate" );
		Reflects.setAccessible(field);
		return (StatementHandler) field.get(statementHandler);
	}

	private MappedStatement getMappedStatement(StatementHandler delegate) throws IllegalAccessException {
		Field field = Reflects.getField(BaseStatementHandler.class, "mappedStatement" );
		Reflects.setAccessible(field);
		return (MappedStatement) field.get(delegate);
	}


}
