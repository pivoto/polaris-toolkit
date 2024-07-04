//package io.polaris.config;
//
//import java.lang.reflect.Method;
//import java.sql.Connection;
//import java.sql.Statement;
//
//import io.polaris.core.tuple.Tuple3;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.ibatis.executor.statement.StatementHandler;
//import org.apache.ibatis.mapping.BoundSql;
//import org.apache.ibatis.plugin.Interceptor;
//import org.apache.ibatis.plugin.Intercepts;
//import org.apache.ibatis.plugin.Invocation;
//import org.apache.ibatis.plugin.Plugin;
//import org.apache.ibatis.plugin.Signature;
//import org.apache.ibatis.session.ResultHandler;
//import org.springframework.stereotype.Component;
//
///**
// * @author Qt
// * @since  Jan 31, 2024
// */
//@Intercepts({
//	@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
//	@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
//	@Signature(type = StatementHandler.class, method = "queryCursor", args = {Statement.class}),
//	@Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
//	@Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})})
//@Slf4j
//public class MybatisLogInterceptor implements Interceptor {
//
//	private final ThreadLocal<Tuple3<StatementHandler, Method, Long>> local = new ThreadLocal<>();
//
//	@Override
//	public Object intercept(Invocation invocation) throws Throwable {
//		Object target = invocation.getTarget();
//		Method invocationMethod = invocation.getMethod();
//		StatementHandler statementHandler = (StatementHandler) target;
//		long startTime = System.currentTimeMillis();
//
//		if ("prepare".equals(invocationMethod.getName())) {
//			try {
//				Object rs = invocation.proceed();
//				local.set(Tuple3.of(statementHandler, invocationMethod, startTime));
//				return rs;
//			} catch (Throwable e) {
//				local.remove();
//				throw e;
//			}
//		}else{
//			try {
//				return invocation.proceed();
//			}finally {
//				Tuple3<StatementHandler, Method, Long> tuple = local.get();
//				local.remove();
//				if (tuple.getFirst() == statementHandler && tuple.getSecond() == invocationMethod) {
//					startTime = tuple.getThird();
//				}
//				long time = System.currentTimeMillis() - startTime;
//				BoundSql boundSql = statementHandler.getBoundSql();
//				log.info("Sql: {}",boundSql.getSql());
//				log.info("{}",boundSql.getParameterMappings());
//				log.info("{}",boundSql.getParameterObject());
//				log.info("Time: {}ms", time);
//
//			}
//		}
//	}
//
//	@Override
//	public Object plugin(Object target) {
//		if (target instanceof StatementHandler) {
//			return Plugin.wrap(target, this);
//		} else {
//			return target;
//		}
//	}
//}
