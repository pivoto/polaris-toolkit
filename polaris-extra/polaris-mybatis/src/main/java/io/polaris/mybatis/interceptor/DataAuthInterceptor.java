package io.polaris.mybatis.interceptor;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;

import io.polaris.core.reflect.Reflects;
import io.polaris.core.tuple.Tuple2;
import io.polaris.mybatis.auth.DataAuthKit;
import io.polaris.mybatis.auth.TableColumnFilter;
import io.polaris.mybatis.auth.TableWhereGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

/**
 * @author Qt
 * @since Aug 28, 2023
 */
@Slf4j
@Intercepts({
	@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class DataAuthInterceptor implements Interceptor {
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		if (!DataAuthKit.instance().isMybatisInterceptorEnabled()) {
			return invocation.proceed();
		}
		Tuple2<TableWhereGenerator, TableColumnFilter> tuple = DataAuthKit.instance().getCurrentResolver();
		if (tuple.getFirst() == null && tuple.getSecond() == null) {
			return invocation.proceed();
		}
		try {
			StatementHandler target = null;
			if (invocation.getTarget() instanceof RoutingStatementHandler) {
				RoutingStatementHandler statementHandler = (RoutingStatementHandler) invocation.getTarget();
				target = InterceptorHelper.instance().getDelegate(statementHandler);
			} else if (invocation.getTarget() instanceof BaseStatementHandler) {
				target = (BaseStatementHandler) invocation.getTarget();
			} else if (invocation.getTarget() instanceof StatementHandler) {
				target = (StatementHandler) invocation.getTarget();
			}

			if (target != null && target instanceof BaseStatementHandler) {
				MappedStatement mappedStatement = InterceptorHelper.instance().getMappedStatement((BaseStatementHandler) target);
				if (mappedStatement != null && mappedStatement.getSqlCommandType() == SqlCommandType.SELECT) {
					BoundSql boundSql = target.getBoundSql();
					// 解析sql
					String sql = boundSql.getSql();
					// 查询权限拦截
					String resolvedSql = DataAuthKit.instance().resolveSelect(sql,tuple.getFirst(), tuple.getSecond());
					if (!sql.equals(resolvedSql)) {
						// 拦截后的SQL更新至BoundSql
						Field field = Reflects.getField(BoundSql.class, "sql");
						Reflects.setAccessible(field);
						field.set(boundSql, resolvedSql);
					}
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

}
