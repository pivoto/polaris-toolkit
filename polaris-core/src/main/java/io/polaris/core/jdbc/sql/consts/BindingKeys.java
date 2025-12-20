package io.polaris.core.jdbc.sql.consts;

/**
 * @author Qt
 * @since Jan 28, 2024
 */
public interface BindingKeys {

	/** 实体对象参数 */
	String ENTITY = "_e";
	/** 字段参数 */
	String FIELD = "_f";
	/** insert语句参数 */
	String INSERT = "_c";
	/** select语句参数 */
	String SELECT = "_r";
	/** update语句参数 */
	String UPDATE = "_u";
	/** delete语句参数 */
	String DELETE = "_d";
	/** merge语句参数 */
	String MERGE = "_m";
	/** where条件参数 */
	String WHERE = "_w";
	/** 排序列参数 */
	String ORDER_BY = "_o";
	/** 需要包含的空值列处理序列，包括NULL与空串 */
	String INCLUDE_EMPTY_COLUMNS = "_iec";
	/** 需要包含空值处理的标志 */
	String INCLUDE_EMPTY = "_ie";
	/** 需要包含的列处理序列 */
	String INCLUDE_COLUMNS = "_ic";
	/** 需要排除的列处理序列 */
	String EXCLUDE_COLUMNS = "_xc";
	/** sql 语句型参数 */
	String SQL = "_sql";

	/** where条件中需要包含的空值列处理序列，包括NULL与空串 */
	String WHERE_INCLUDE_EMPTY_COLUMNS = "_w_iec";
	/** where条件中是否包含空值处理的标志，包括NULL与空串 */
	String WHERE_INCLUDE_EMPTY = "_w_ie";
	/** where条件中需要包含的列处理序列 */
	String WHERE_INCLUDE_COLUMNS = "_w_ic";
	/** where条件中需要排除的列处理序列 */
	String WHERE_EXCLUDE_COLUMNS = "_w_xc";

	/** 是否支持逻辑删除处理的标志 */
	String WITH_LOGIC_DELETED = "_w_ld";
}
