package io.polaris.mybatis.scripting;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

/**
 * @author Qt
 * @since Mar 17, 2026
 */
class TableRefResolvableSqlSource implements SqlSource {
	private final Configuration configuration;
	private final SqlSource sqlSource;

	public TableRefResolvableSqlSource(Configuration configuration, SqlSource sqlSource) {
		this.configuration = configuration;
		this.sqlSource = sqlSource;
	}

	@Override
	public BoundSql getBoundSql(Object parameterObject) {
		BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
		return new DelegateBoundSql(configuration, boundSql);
	}
}
