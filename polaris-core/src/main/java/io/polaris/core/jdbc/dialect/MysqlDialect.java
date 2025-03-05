package io.polaris.core.jdbc.dialect;

/**
 * @author Qt
 * @since Mar 05, 2025
 */
public class MysqlDialect extends BaseDialect {
	@Override
	public String getPageSql(String sql, int pageNum, int pageSize) {

		StringBuilder sqlBuilder = new StringBuilder(sql.length() + 14);
		sqlBuilder.append(sql);
		if (pageNum == 1) {
			sqlBuilder.append("\n LIMIT ").append(pageSize);
		} else {
			int startRow = getStart(pageNum, pageSize);
			sqlBuilder.append("\n LIMIT ").append(startRow).append(", ").append(pageSize);
		}
		return sqlBuilder.toString();
	}
}
