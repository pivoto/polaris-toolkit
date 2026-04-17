package io.polaris.mybatis.enhance.printer;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import io.polaris.core.lang.Types;
import org.apache.ibatis.cursor.Cursor;
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
		Class<?> returnType = method.getReturnType();
		if (Types.isPrimitive(returnType)) {
			// 基本类型，则返回默认值，防止自动拆包报NPE
			return Types.getDefaultValue(returnType);
		} else if (Cursor.class.equals(returnType)) {
			// 游标类型，则返回空游标
			return EmptyCursor.INSTANCE;
		} else if (List.class.isAssignableFrom(returnType)) {
			return Collections.emptyList();
		} else if (Collection.class.isAssignableFrom(returnType)) {
			return Collections.emptyList();
		}
		return null;
	}

	private static class EmptyCursor<T> implements Cursor<T> {
		private static final EmptyCursor<?> INSTANCE = new EmptyCursor<>();

		@Override
		public boolean isOpen() {
			return true;
		}

		@Override
		public boolean isConsumed() {
			return false;
		}

		@Override
		public int getCurrentIndex() {
			return 0;
		}

		@Override
		public void close() throws IOException {
		}

		@Override
		public Iterator<T> iterator() {
			return Collections.emptyIterator();
		}
	}

}
