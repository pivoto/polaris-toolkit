package io.polaris.core.jdbc.sql;

/**
 * @author Qt
 * @since 1.8,  Jan 28, 2024
 */
public interface BindingKeys {

	String ENTITY = "e";
	String FIELD = "f";
	String INSERT = "c";
	String SELECT = "r";
	String UPDATE = "u";
	String DELETE = "d";
	String MERGE = "m";
	String WHERE = "w";
	String ORDER_BY = "o";
	String INCLUDE_EMPTY_COLUMNS = "iec";
	String INCLUDE_EMPTY = "ie";
	String INCLUDE_COLUMNS = "ic";
	String EXCLUDE_COLUMNS = "xc";
	String SQL = "sql";

	String WHERE_INCLUDE_EMPTY_COLUMNS = "w_iec";
	String WHERE_INCLUDE_EMPTY = "w_ie";
	String WHERE_INCLUDE_COLUMNS = "w_ic";
	String WHERE_EXCLUDE_COLUMNS = "w_xc";
}
