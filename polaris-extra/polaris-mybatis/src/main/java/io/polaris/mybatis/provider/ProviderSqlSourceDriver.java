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
			return BaseProviderMethodResolver.getBoundSql(sqlSource,parameterObject);
		}
	}

}
