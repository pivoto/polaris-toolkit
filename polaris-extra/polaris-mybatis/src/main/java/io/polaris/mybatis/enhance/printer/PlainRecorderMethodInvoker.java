package io.polaris.mybatis.enhance.printer;

import java.lang.reflect.Method;
import java.util.function.Consumer;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.ParamNameResolver;

/**
 * @author Qt
 * @since Apr 03, 2026
 */
class PlainRecorderMethodInvoker implements RecorderMapperMethodInvoker {
	private final SqlSource sqlSource;
	private final ParamNameResolver paramNameResolver;
	private final Consumer<BoundSql> consumer;

	public PlainRecorderMethodInvoker(SqlSource sqlSource, ParamNameResolver paramNameResolver, Consumer<BoundSql> consumer) {
		this.sqlSource = sqlSource;
		this.paramNameResolver = paramNameResolver;
		this.consumer = consumer;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object param = paramNameResolver.getNamedParams(args);
		BoundSql boundSql = sqlSource.getBoundSql(param);
		consumer.accept(boundSql);
		return null;
	}
}
