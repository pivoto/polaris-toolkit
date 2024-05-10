package io.polaris.mybatis.provider;

import java.util.Map;

import io.polaris.core.tuple.Tuple2;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

/**
 * @author Qt
 * @since  Feb 07, 2024
 */
public class ProviderSqlSourceDriver extends XMLLanguageDriver {

	@Override
	public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
		SqlSource sqlSource = super.createSqlSource(configuration, script, parameterType);
		// 创建一个扩展的SqlSource以动态追加
		sqlSource = new SqlSourceWithAdditionalParameters(sqlSource);
		return sqlSource;
	}


	static class SqlSourceWithAdditionalParameters implements SqlSource {
		private final SqlSource sqlSource;

		public SqlSourceWithAdditionalParameters(SqlSource sqlSource) {
			this.sqlSource = sqlSource;
		}

		@Override
		public BoundSql getBoundSql(Object parameterObject) {
			try {
				BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
				Tuple2<Object, Map<String, Object>> tuple = BaseProviderMethodResolver.ADDITIONAL_PARAMETERS.get();
				// 存在扩展参数则创建一个扩展的SqlSource以动态追加
				if (tuple != null && tuple.getFirst() == parameterObject) {
					Map<String, Object> params = tuple.getSecond();
					if (params != null && !params.isEmpty()) {
						// 追加额外参数
						tuple.getSecond().forEach(boundSql::setAdditionalParameter);
					}
				}
				return boundSql;
			} finally {
				// 用完即清理
				BaseProviderMethodResolver.ADDITIONAL_PARAMETERS.remove();
			}
		}
	}

}
