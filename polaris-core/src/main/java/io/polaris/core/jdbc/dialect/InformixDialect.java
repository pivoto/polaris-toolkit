package io.polaris.core.jdbc.dialect;

/**
 * @author Qt
 * @since Mar 05, 2025
 */
public class InformixDialect extends BaseDialect {
	@Override
	public String getPageSql(String sql, int pageNum, int pageSize) {

		StringBuilder sqlBuilder = new StringBuilder(sql.length() + 40);
		sqlBuilder.append("SELECT ");
		if (pageNum > 1) {
			sqlBuilder.append(" SKIP ").append(getStart(pageNum, pageSize));
		}
		sqlBuilder.append(" FIRST ").append(pageSize);
		sqlBuilder.append(" * FROM ( \n");
		sqlBuilder.append(sql);
		sqlBuilder.append("\n ) TEMP_T ");
		return sqlBuilder.toString();
	}
}
