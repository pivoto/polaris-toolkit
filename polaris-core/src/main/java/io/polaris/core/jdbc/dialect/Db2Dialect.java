package io.polaris.core.jdbc.dialect;

/**
 * @author Qt
 * @since Mar 05, 2025
 */
public class Db2Dialect extends BaseDialect {
	@Override
	public String getPageSql(String sql, int pageNum, int pageSize) {

		int startRow = getStart(pageNum, pageSize);
		int endRow = getEnd(pageNum, pageSize);

		StringBuilder sqlBuilder = new StringBuilder(sql.length() + 140);
		sqlBuilder.append("SELECT * FROM (SELECT TMP_PAGE.*,ROWNUMBER() OVER() AS ROWNUM_ FROM ( \n");
		sqlBuilder.append(sql);
		sqlBuilder.append("\n ) AS TMP_PAGE) TMP_PAGE WHERE ROWNUM_ BETWEEN ").append(startRow + 1).append(" AND ").append(endRow);
		return sqlBuilder.toString();
	}
}
