package io.polaris.mybatis.support;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import io.polaris.core.annotation.Internal;
import io.polaris.core.tuple.Tuple2;
import org.apache.ibatis.mapping.BoundSql;

/**
 * @author Qt
 * @since Mar 15, 2026
 */
@Internal
public class MapperHelperContext {
	private static final AtomicBoolean enableEnhanceBoundSql = new AtomicBoolean(false);
	private static final ThreadLocal<Tuple2<Object, Map<String, Object>>> additionalParametersLocal = new ThreadLocal<>();

	public static void enableEnhanceBoundSql(boolean enable) {
		enableEnhanceBoundSql.set(enable);
	}

	public static boolean isEnabledEnhanceBoundSql() {
		return enableEnhanceBoundSql.get();
	}

	public static void enhanceBoundSql(BoundSql boundSql) {
		Object parameterObject = boundSql.getParameterObject();
		enhanceBoundSql(boundSql, parameterObject);
	}

	public static void enhanceBoundSql(BoundSql boundSql, Object parameterObject) {
		Tuple2<Object, Map<String, Object>> tuple = additionalParametersLocal.get();
		// 存在扩展参数则创建一个扩展的SqlSource以动态追加
		if (tuple != null && tuple.getFirst() == parameterObject) {
			try {
				Map<String, Object> params = tuple.getSecond();
				if (params != null && !params.isEmpty()) {
					// 追加额外参数
					tuple.getSecond().forEach(boundSql::setAdditionalParameter);
				}
			} finally {
				// 用完即清理
				additionalParametersLocal.remove();
			}
		}
	}

	public static Map<String, Object> getBoundAdditionalParameters(Object parameterObject) {
		Tuple2<Object, Map<String, Object>> tuple = additionalParametersLocal.get();
		if (tuple != null && tuple.getFirst() == parameterObject) {
			return tuple.getSecond();
		}
		return null;
	}


	public static Map<String, Object> bindAdditionalParameters(Object parameterObject) {
		Map<String, Object> additionalParameters = getBoundAdditionalParameters(parameterObject);
		if (additionalParameters == null) {
			additionalParameters = new HashMap<>();
			additionalParametersLocal.set(Tuple2.of(parameterObject, additionalParameters));
		}
		return additionalParameters;
	}

	public static void clearAdditionalParameters() {
		additionalParametersLocal.remove();
	}

}
