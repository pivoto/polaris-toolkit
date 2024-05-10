package io.polaris.mybatis.interceptor;

import io.polaris.core.function.Executable;
import net.sf.jsqlparser.schema.Table;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 数据权限控制上下文
 *
 * @author Qt
 * @since  Aug 28, 2023
 */
public class DataAuthSqlCtx {


	private static final ThreadLocal<Function<Collection<Table>, String>> ConditionAppender = new ThreadLocal<>();
	private static final ThreadLocal<BiFunction<Table, String, Boolean>> ColumnFilter = new ThreadLocal<>();

	public static boolean hasDataAuthority() {
		return ConditionAppender.get() != null || ColumnFilter.get() != null;
	}

	public static void bind(Function<Collection<Table>, String> conditionAppender, BiFunction<Table, String, Boolean> columnFilter) {
		ConditionAppender.set(conditionAppender);
		ColumnFilter.set(columnFilter);
	}

	public static void clear() {
		ConditionAppender.remove();
		ColumnFilter.remove();
	}

	public static BiFunction<Table, String, Boolean> getColumnFilter() {
		return ColumnFilter.get();
	}

	public static Function<Collection<Table>, String> getConditionAppender() {
		return ConditionAppender.get();
	}

	public static void execute(Executable executable, Function<Collection<Table>, String> conditionAppender, BiFunction<Table, String, Boolean> columnFilter) throws Throwable {
		try {
			bind(conditionAppender, columnFilter);
			executable.execute();
		} finally {
			clear();
		}
	}


	public static <V> V execute(Callable<V> executable, Function<Collection<Table>, String> conditionAppender, BiFunction<Table, String, Boolean> columnFilter) throws Throwable {
		try {
			bind(conditionAppender, columnFilter);
			return executable.call();
		} finally {
			clear();
		}
	}


}
