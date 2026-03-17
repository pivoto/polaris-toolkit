package io.polaris.mybatis.scripting;

import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

/**
 * 通过`@Lang(TableRefResolvableDriver.class)`配置添加对解析实体表与字段的引用表达式的支持
 *
 * @author Qt
 * @since Feb 21, 2024
 */
public class TableRefResolvableDriver extends XMLLanguageDriver implements EnhanceBoundSqlLanguageDriver {

	@Override
	public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {
		SqlSource sqlSource = super.createSqlSource(configuration, script, parameterType);
		sqlSource = new SqlSourceWithAdditionalParameters(sqlSource);
		sqlSource = new TableRefResolvableSqlSource(configuration, sqlSource);
		return sqlSource;
	}

	@Override
	public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
		SqlSource sqlSource = super.createSqlSource(configuration, script, parameterType);
		sqlSource = new SqlSourceWithAdditionalParameters(sqlSource);
		sqlSource = new TableRefResolvableSqlSource(configuration, sqlSource);
		return sqlSource;
	}

}
