package io.polaris.core.jdbc.dialect;

/**
 * @author Qt
 * @since Mar 05, 2025
 */
public class SqlserverDialect extends BaseDialect {
	@Override
	public String getPageSql(String sql, int pageNum, int pageSize) {
		int startRow = getStart(pageNum, pageSize);
		int endRow = getEnd(pageNum, pageSize);

		StringBuilder sqlBuilder = new StringBuilder(sql.length() + 120);
		sqlBuilder.append(" SELECT * FROM (  SELECT B.*,ROWNUMBER() OVER() AS ROWNUM_ FROM (");
		sqlBuilder.append(sql);
		sqlBuilder.append(" ) AS B ) AS A WHERE A.ROWNUM_ BETWEEN ").append(startRow + 1).append(" AND ").append(endRow);
		return sqlBuilder.toString();
	}
}
