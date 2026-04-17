package io.polaris.mybatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.sql.consts.BindingKeys;

/**
 * 参数中的特定值的绑定键配置
 *
 * @author Qt
 * @since Mar 31, 2026
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MapperParam {

	/** 实体对象参数 */
	String entity() default BindingKeys.ENTITY;

	/** where条件参数 */
	String where() default BindingKeys.WHERE;

	/** 排序列参数 */
	String orderBy() default BindingKeys.ORDER_BY;

	/** insert或update中需要包含的空值列列集合或数组，包括NULL与空串 */
	String includeEmptyColumns() default BindingKeys.INCLUDE_EMPTY_COLUMNS;

	/** insert或update中需要包含空值处理的标志 */
	String includeAllEmpty() default BindingKeys.INCLUDE_ALL_EMPTY;

	/** insert或update中需要包含的列列集合或数组 */
	String includeColumns() default BindingKeys.INCLUDE_COLUMNS;

	/** insert或update中需要排除的列列集合或数组 */
	String excludeColumns() default BindingKeys.EXCLUDE_COLUMNS;

	/** sql 语句型参数 */
	String sql() default BindingKeys.SQL;

	/** where条件中需要包含的空值列列集合或数组，包括NULL与空串 */
	String whereIncludeEmptyColumns() default BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS;

	/** where条件中是否包含空值处理的标志，包括NULL与空串 */
	String whereIncludeAllEmpty() default BindingKeys.WHERE_INCLUDE_ALL_EMPTY;

	/** where条件中需要包含的列列集合或数组 */
	String whereIncludeColumns() default BindingKeys.WHERE_INCLUDE_COLUMNS;

	/** where条件中需要排除的列列集合或数组 */
	String whereExcludeColumns() default BindingKeys.WHERE_EXCLUDE_COLUMNS;

	/** 是否支持逻辑删除处理的标志 */
	String withLogicDeleted() default BindingKeys.WITH_LOGIC_DELETED;

	/** 是否使用Count语句查询存在性 */
	String existsByCount() default BindingKeys.EXISTS_BY_COUNT;

	/** 标识为实体对象参数 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@interface Entity {
	}

	/** 标识为where条件参数 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@interface Where {}

	/** 标识为排序列参数 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@interface OrderBy {}

	/** 标识为insert或update中需要包含的空值列集合或数组，包括NULL与空串 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@interface IncludeEmptyColumns {}

	/** 标识为insert或update中需要包含空值处理的标志 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@interface IncludeAllEmpty {}

	/** 标识为insert或update中需要包含的列列集合或数组 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@interface IncludeColumns {}

	/** 标识为insert或update中需要排除的列列集合或数组 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@interface ExcludeColumns {}

	/** 标识为sql 语句型参数 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@interface Sql {}

	/** 标识为where条件中需要包含的空值列列集合或数组，包括NULL与空串 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@interface WhereIncludeEmptyColumns {}

	/** 标识为where条件中是否包含空值处理的标志，包括NULL与空串 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@interface WhereIncludeAllEmpty {}

	/** 标识为where条件中需要包含的列列集合或数组 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@interface WhereIncludeColumns {}

	/** 标识为where条件中需要排除的列列集合或数组 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@interface WhereExcludeColumns {}

	/** 标识为是否支持逻辑删除处理的标志 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@interface WithLogicDeleted {}
	/** 是否使用Count语句查询存在性 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	@interface ExistsByCount {}
}
