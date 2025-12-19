package io.polaris.core.jdbc;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

import io.polaris.core.collection.ObjectArrays;
import io.polaris.core.consts.StdKeys;
import io.polaris.core.env.GlobalStdEnv;
import io.polaris.core.env.GlobalStdEnvObservable;
import io.polaris.core.string.Strings;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qt
 * @since Dec 19, 2025
 */
public class SqlParamPredicate {

	private static volatile SqlParamPredicate instance;
	private static final ThreadLocal<Boolean> includeEmptyStringLocal = new ThreadLocal<>();
	private static final ThreadLocal<Boolean> includeEmptyCollectionLocal = new ThreadLocal<>();

	/** 全局控制允许空字符串作为有效参数，而不是视为NULL值处理 */
	@Setter
	@Getter
	private volatile boolean includeEmptyString;
	/** 全局控制允许空集合或数组作为有效参数，而不是视为NULL值处理 */
	@Setter
	@Getter
	private volatile boolean includeEmptyCollection;

	private SqlParamPredicate() {
		this.includeEmptyString = GlobalStdEnv.getBoolean(StdKeys.JDBC_SQL_PARAM_INCLUDE_EMPTY_STRING, false);
		this.includeEmptyCollection = GlobalStdEnv.getBoolean(StdKeys.JDBC_SQL_PARAM_INCLUDE_EMPTY_COLLECTION, false);
		GlobalStdEnvObservable.addListener(this::updateEnvironment);
	}

	public static SqlParamPredicate instance() {
		if (instance == null) {
			synchronized (SqlParamPredicate.class) {
				if (instance == null) {
					instance = new SqlParamPredicate();
				}
			}
		}
		return instance;
	}

	public static void includeEmptyString(boolean includeEmptyString, Runnable runnable) {
		includeEmptyStringLocal.set(includeEmptyString);
		try {
			runnable.run();
		} finally {
			includeEmptyStringLocal.remove();
		}
	}

	public static <V> V includeEmptyString(boolean includeEmptyString, Supplier<V> supplier) {
		includeEmptyStringLocal.set(includeEmptyString);
		try {
			return supplier.get();
		} finally {
			includeEmptyStringLocal.remove();
		}
	}


	public static void includeEmptyCollection(boolean includeEmptyCollection, Runnable runnable) {
		includeEmptyCollectionLocal.set(includeEmptyCollection);
		try {
			runnable.run();
		} finally {
			includeEmptyCollectionLocal.remove();
		}
	}

	public static <V> V includeEmptyCollection(boolean includeEmptyCollection, Supplier<V> supplier) {
		includeEmptyCollectionLocal.set(includeEmptyCollection);
		try {
			return supplier.get();
		} finally {
			includeEmptyCollectionLocal.remove();
		}
	}

	public void updateEnvironment() {
		this.includeEmptyString = GlobalStdEnv.getBoolean(StdKeys.JDBC_SQL_PARAM_INCLUDE_EMPTY_STRING, false);
		this.includeEmptyCollection = GlobalStdEnv.getBoolean(StdKeys.JDBC_SQL_PARAM_INCLUDE_EMPTY_COLLECTION, false);
	}


	public boolean notExists(Object obj) {
		if (obj == null) {
			return true;
		}
		if (obj instanceof CharSequence) {
			Boolean localVal = includeEmptyStringLocal.get();
			boolean include = localVal != null ? localVal : this.includeEmptyString;
			if (include) {
				return false;
			}
			return Strings.isEmpty((CharSequence) obj);
		}
		if (obj instanceof Map) {
			Boolean localVal = includeEmptyCollectionLocal.get();
			boolean include = localVal != null ? localVal : this.includeEmptyCollection;
			if (include) {
				return false;
			}
			return ((Map<?, ?>) obj).isEmpty();
		} else if (obj instanceof Iterable) {
			Boolean localVal = includeEmptyCollectionLocal.get();
			boolean include = localVal != null ? localVal : this.includeEmptyCollection;
			if (include) {
				return false;
			}
			return !((Iterable<?>) obj).iterator().hasNext();
		} else if (obj instanceof Iterator) {
			Boolean localVal = includeEmptyCollectionLocal.get();
			boolean include = localVal != null ? localVal : this.includeEmptyCollection;
			if (include) {
				return false;
			}
			return !((Iterator<?>) obj).hasNext();
		} else if (ObjectArrays.isArray(obj)) {
			Boolean localVal = includeEmptyCollectionLocal.get();
			boolean include = localVal != null ? localVal : this.includeEmptyCollection;
			if (include) {
				return false;
			}
			return ObjectArrays.isEmpty(obj);
		}
		return false;
	}

	public boolean exists(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof CharSequence) {
			Boolean localVal = includeEmptyStringLocal.get();
			boolean include = localVal != null ? localVal : this.includeEmptyString;
			if (include) {
				return true;
			}
			return !Strings.isEmpty((CharSequence) obj);
		}
		if (obj instanceof Map) {
			Boolean localVal = includeEmptyCollectionLocal.get();
			boolean include = localVal != null ? localVal : this.includeEmptyCollection;
			if (include) {
				return true;
			}
			return !((Map<?, ?>) obj).isEmpty();
		} else if (obj instanceof Iterable) {
			Boolean localVal = includeEmptyCollectionLocal.get();
			boolean include = localVal != null ? localVal : this.includeEmptyCollection;
			if (include) {
				return true;
			}
			return ((Iterable<?>) obj).iterator().hasNext();
		} else if (obj instanceof Iterator) {
			Boolean localVal = includeEmptyCollectionLocal.get();
			boolean include = localVal != null ? localVal : this.includeEmptyCollection;
			if (include) {
				return true;
			}
			return ((Iterator<?>) obj).hasNext();
		} else if (ObjectArrays.isArray(obj)) {
			Boolean localVal = includeEmptyCollectionLocal.get();
			boolean include = localVal != null ? localVal : this.includeEmptyCollection;
			if (include) {
				return true;
			}
			return !ObjectArrays.isEmpty(obj);
		}
		return true;
	}
}
