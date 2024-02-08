package io.polaris.mybatis.scripting;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import io.polaris.core.lang.bean.BeanMap;
import io.polaris.core.lang.bean.Beans;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

/**
 * @author Qt
 * @since 1.8,  Feb 07, 2024
 */
public class ProviderSqlSourceDriver extends XMLLanguageDriver {

	private static final ThreadLocal<Map<String, Object>> ADDITIONAL_PARAMETERS = new ThreadLocal<>();

	public static boolean hasProviderSqlSourceDriver(Method mapperMethod) {
		Lang lang = mapperMethod.getAnnotation(Lang.class);
		if (lang == null) {
			return false;
		}
		return ProviderSqlSourceDriver.class == lang.value();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> toParameterBindings(Method mapperMethod, Object parameterObject) {
		if (parameterObject instanceof Map) {
			return (Map<String, Object>) parameterObject;
		} else {
			if (!ProviderSqlSourceDriver.hasProviderSqlSourceDriver(mapperMethod)) {
				throw new IllegalArgumentException("请使用Map类型或明确声明参数名");
			}
			return ProviderSqlSourceDriver.toParameterBindings(parameterObject);
		}
	}

	/**
	 * 将原Mybatis参数转换为Map类型以便在自定义SqlProvider中可进行额外的动态参数绑定。
	 * 对于原参数是Map类型，则直接返回。
	 * 负作用是如果此Map是只读的，则在添加额外参数键值时会报错。
	 * 对于原则数是非Map类型，则创建一个Map并添加此参数的所有可用属性。
	 *
	 * @param parameterObject
	 * @return
	 */
	@SuppressWarnings({"all"})
	public static Map<String, Object> toParameterBindings(Object parameterObject) {
		if (parameterObject instanceof Map) {
			return (Map) parameterObject;
		}
		Map<String, Object> additionalParameters = new HashMap<>();
		ADDITIONAL_PARAMETERS.set(additionalParameters);
		if (parameterObject != null) {
			BeanMap<Object> beanMap = Beans.newBeanMap(parameterObject);
			additionalParameters.putAll(beanMap);
		}
		return additionalParameters;
	}


	@Override
	public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
		try {
			SqlSource sqlSource = super.createSqlSource(configuration, script, parameterType);
			Map<String, Object> params = ADDITIONAL_PARAMETERS.get();
			// 存在扩展参数则创建一个扩展的SqlSource以动态追加
			if (params != null && !params.isEmpty()) {
				return new SqlSourceWithAdditionalParameters(sqlSource, params);
			}
			return sqlSource;
		} finally {
			// 用完即清理
			ADDITIONAL_PARAMETERS.remove();
		}
	}

	static class SqlSourceWithAdditionalParameters implements SqlSource {
		private final SqlSource sqlSource;
		private final Map<String, Object> params;

		public SqlSourceWithAdditionalParameters(SqlSource sqlSource, Map<String, Object> params) {
			this.sqlSource = sqlSource;
			this.params = params;
		}

		@Override
		public BoundSql getBoundSql(Object parameterObject) {
			BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
			// 追加额外参数
			params.forEach(boundSql::setAdditionalParameter);
			return boundSql;
		}
	}

}
