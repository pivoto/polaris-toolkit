package io.polaris.core.jdbc.dialect;

/**
 * @author Qt
 * @since Mar 05, 2025
 */
public class OracleDialect extends BaseDialect {
	@Override
	public String getPageSql(String sql, int pageNum, int pageSize) {

		int startRow = getStart(pageNum, pageSize);
		int endRow = getEnd(pageNum, pageSize);

		StringBuilder sqlBuilder = new StringBuilder(sql.length() + 120);
		sqlBuilder.append("SELECT * FROM ( ");
		sqlBuilder.append(" SELECT TMP_PAGE.*, ROWNUM ROWNUM_ FROM ( \n");
		sqlBuilder.append(sql);
		sqlBuilder.append("\n ) TMP_PAGE)");
		sqlBuilder.append(" WHERE ROWNUM_ <= ").append(endRow)
			.append(" AND ROWNUM_ > ").append(startRow);
		return sqlBuilder.toString();
	}
}
