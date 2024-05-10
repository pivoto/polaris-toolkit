package io.polaris.core.jdbc.sql.consts;

/**
 * @author Qt
 * @since  Jan 28, 2024
 */
public interface BindingKeys {

	String ENTITY = "_e";
	String FIELD = "_f";
	String INSERT = "_c";
	String SELECT = "_r";
	String UPDATE = "_u";
	String DELETE = "_d";
	String MERGE = "_m";
	String WHERE = "_w";
	String ORDER_BY = "_o";
	String INCLUDE_EMPTY_COLUMNS = "_iec";
	String INCLUDE_EMPTY = "_ie";
	String INCLUDE_COLUMNS = "_ic";
	String EXCLUDE_COLUMNS = "_xc";
	String SQL = "_sql";

	String WHERE_INCLUDE_EMPTY_COLUMNS = "_w_iec";
	String WHERE_INCLUDE_EMPTY = "_w_ie";
	String WHERE_INCLUDE_COLUMNS = "_w_ic";
	String WHERE_EXCLUDE_COLUMNS = "_w_xc";
}
