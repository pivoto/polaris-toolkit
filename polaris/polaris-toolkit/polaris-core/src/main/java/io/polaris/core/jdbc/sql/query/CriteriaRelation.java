package io.polaris.core.jdbc.sql.query;

/**
 * @author Qt
 * @since 1.8,  Aug 11, 2023
 */
@SuppressWarnings("all" )
public enum CriteriaRelation {

	AND, OR,
	;


	public String getSqlText() {
		return name();
	}

}
