package io.polaris.mybatis.scripting;

import io.polaris.mybatis.support.MapperHelperContext;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

/**
 * @author Qt
 * @since Feb 07, 2024
 */
public class ProviderSqlSourceDriver extends XMLLanguageDriver implements EnhanceBoundSqlLanguageDriver {

	@Override
	public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {
		SqlSource sqlSource = super.createSqlSource(configuration, script, parameterType);
		// 创建一个扩展的SqlSource以动态追加
		sqlSource = new SqlSourceWithAdditionalParameters(sqlSource);
		return sqlSource;
	}

	@Override
	public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
		SqlSource sqlSource = super.createSqlSource(configuration, script, parameterType);
		// 创建一个扩展的SqlSource以动态追加
		sqlSource = new SqlSourceWithAdditionalParameters(sqlSource);
		return sqlSource;
	}

}
