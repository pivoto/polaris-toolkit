package io.polaris.core.jdbc;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

import io.polaris.core.collection.ObjectArrays;
import io.polaris.core.env.Config;
import io.polaris.core.env.GlobalStdEnvObservable;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qt
 * @since Dec 19, 2025
 */
public class SqlParamPredicate {
	public static enum Result {
		NULL,
		EMPTY,
		NONEMPTY,
	}

	private static volatile SqlParamPredicate instance;
	private static final ThreadLocal<SqlParamPredicate> LOCAL = new ThreadLocal<>();

	/** 全局控制是否空字符串参数在SQL语句中作为NULL值处理，而不是直接使用，可遵循具体数据库的处理方式 */
	@Setter
	@Getter
	private volatile boolean emptyAsNull;
	/** 全局控制是否空字符串作为有效参数，而不是尽可能忽略 */
	@Setter
	@Getter
	private volatile boolean includeEmpty;

	private SqlParamPredicate(boolean listen) {
		updateEnv();
		if (listen) {
			GlobalStdEnvObservable.addListener(this::updateEnv);
		}
	}

	public SqlParamPredicate() {
		this(false);
	}

	public void updateEnv() {
		this.emptyAsNull = Config.INSTANCE.jdbc().sql().param().emptyAsNull();
		this.includeEmpty = Config.INSTANCE.jdbc().sql().param().includeEmpty();
	}

	public static SqlParamPredicate instance() {
		if (instance == null) {
			synchronized (SqlParamPredicate.class) {
				if (instance == null) {
					instance = new SqlParamPredicate(true);
				}
			}
		}
		return instance;
	}

	public static boolean emptyAsNull() {
		SqlParamPredicate predicate = LOCAL.get();
		if (predicate != null) {
			return predicate.emptyAsNull;
		}
		return instance().emptyAsNull;
	}

	public static boolean includeEmpty() {
		SqlParamPredicate predicate = LOCAL.get();
		if (predicate != null) {
			return predicate.includeEmpty;
		}
		return instance().includeEmpty;
	}

	public static Result check(Object obj) {
		if (obj == null) {
			return Result.NULL;
		}
		if (obj instanceof CharSequence) {
			if (((CharSequence) obj).length() == 0) {
				return Result.EMPTY;
			}
			return Result.NONEMPTY;
		}
		if (obj instanceof Map) {
			if (((Map<?, ?>) obj).isEmpty()) {
				return Result.EMPTY;
			}
			return Result.NONEMPTY;
		} else if (obj instanceof Iterable) {
			if (!((Iterable<?>) obj).iterator().hasNext()) {
				return Result.EMPTY;
			}
			return Result.NONEMPTY;
		} else if (obj instanceof Iterator) {
			if (!((Iterator<?>) obj).hasNext()) {
				return Result.EMPTY;
			}
			return Result.NONEMPTY;
		} else if (ObjectArrays.isArray(obj)) {
			if (ObjectArrays.isEmpty(obj)) {
				return Result.EMPTY;
			}
			return Result.NONEMPTY;
		}
		return Result.NONEMPTY;
	}


	public static void with(SqlParamPredicate predicate, Runnable runnable) {
		LOCAL.set(predicate);
		try {
			runnable.run();
		} finally {
			LOCAL.remove();
		}
	}

	public static <V> V with(SqlParamPredicate predicate, Supplier<V> supplier) {
		LOCAL.set(predicate);
		try {
			return supplier.get();
		} finally {
			LOCAL.remove();
		}
	}


}
