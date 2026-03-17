package io.polaris.mybatis.scripting;

import io.polaris.mybatis.support.MapperHelperContext;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

/**
 * @author Qt
 * @since Mar 17, 2026
 */
class SqlSourceWithAdditionalParameters implements SqlSource {
	private final SqlSource sqlSource;

	public SqlSourceWithAdditionalParameters(SqlSource sqlSource) {
		this.sqlSource = sqlSource;
	}

	@Override
	public BoundSql getBoundSql(Object parameterObject) {
		BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
		MapperHelperContext.enhanceBoundSql(boundSql, parameterObject);
		return boundSql;
	}
}
