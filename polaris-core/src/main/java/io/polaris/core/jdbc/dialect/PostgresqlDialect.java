package io.polaris.core.jdbc.dialect;

/**
 * @author Qt
 * @since Mar 05, 2025
 */
public class PostgresqlDialect extends BaseDialect {
	@Override
	public String getPageSql(String sql, int pageNum, int pageSize) {

		StringBuilder sqlStr = new StringBuilder(sql.length() + 17);
		sqlStr.append(sql);
		if (pageNum == 1) {
			sqlStr.append(" LIMIT ").append(pageSize);
		} else {
			sqlStr.append(" LIMIT ").append(pageSize).append(" OFFSET ").append(getStart(pageNum, pageSize));
		}
		return sqlStr.toString();
	}
}
