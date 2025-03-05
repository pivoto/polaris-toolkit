package io.polaris.core.jdbc.dialect;

/**
 * @author Qt
 * @since Mar 05, 2025
 */
public class HsqldbDialect extends BaseDialect {
	@Override
	public String getPageSql(String sql, int pageNum, int pageSize) {
		StringBuilder sqlBuilder = new StringBuilder(sql.length() + 20);
		sqlBuilder.append(sql);
		sqlBuilder.append("\n LIMIT ").append(pageSize);
		if (pageNum > 1) {
			sqlBuilder.append("\n OFFSET ").append(getStart(pageNum, pageSize));
		}
		return sqlBuilder.toString();
	}
}
