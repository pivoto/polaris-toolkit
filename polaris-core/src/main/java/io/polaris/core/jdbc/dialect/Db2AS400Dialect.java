package io.polaris.core.jdbc.dialect;

/**
 * @author Qt
 * @since Mar 05, 2025
 */
public class Db2AS400Dialect extends BaseDialect {
	@Override
	public String getPageSql(String sql, int pageNum, int pageSize) {
		return sql + " OFFSET " +
			getStart(pageNum, pageSize) + " ROWS FETCH FIRST " +
			pageSize + " ROWS ONLY";
	}
}
