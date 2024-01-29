package io.polaris.core.jdbc.sql.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.sql.EntityStatements;

/**
 * @author Qt
 * @since 1.8,  Jan 27, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Inherited
public @interface EntityUpdate {

	/**
	 * @return 标识目标实体类型
	 */
	Class<?> value();

	String tableAlias() default "";

	/**
	 * @return 标识是否只使用主键作为`where`条件
	 */
	boolean byId() default true;

	/**
	 * @return 标识在参数容器中映射实体参数值的`key`
	 */
	String keyEntity() default EntityStatements.Keys.ENTITY;

	/**
	 * @return 标识在参数容器中映射`where`条件参数值的`key`
	 */
	String keyWhere() default EntityStatements.Keys.WHERE;

	/**
	 * @return 标识需要包含的字段，当字段在包含列表时，才会执行其SQL构建。
	 * <p>
	 * 默认包含所有字段，
	 */
	String[] includeColumns() default {};

	/**
	 * @return 标识在参数容器中用于指定包含字段列表的`key`
	 */
	String keyIncludeColumns() default EntityStatements.Keys.INCLUDE_COLUMNS;

	/**
	 * @return 标识排除的字段，当字段在排除列表中时，即使值非空也不执行其SQL构建。
	 * <p>
	 * 默认不排除任何字段
	 */
	String[] excludeColumns() default {};

	/**
	 * @return 标识在参数容器中用于指定包含字段排除列表的`key`
	 */
	String keyExcludeColumns() default EntityStatements.Keys.EXCLUDE_COLUMNS;

	/**
	 * @return 标识即使字段值为空时仍要包含的字段。这些字段无论是否空值，都会执行其SQL构建。
	 * <p>
	 * 默认不包含空值字段
	 */
	String[] includeEmptyColumns() default {};


	/**
	 * @return 标识在参数容器中用于指定包含空值字段列表的`key`
	 */
	String keyIncludeEmptyColumns() default EntityStatements.Keys.INCLUDE_EMPTY_COLUMNS;

	/**
	 * @return 标识是否包含空值字段，如果包含则对于空值字段也执行其SQL构建。
	 * <p>
	 * 默认不包含。
	 */
	boolean includeAllEmpty() default false;

	/**
	 * @return 标识在参数容器中用于指定是否包含空值字段开关的`key`
	 */
	String keyIncludeEmpty() default EntityStatements.Keys.INCLUDE_EMPTY;


	/**
	 * @return 标识需要包含的字段，当字段在包含列表时，才会执行其SQL构建。
	 * <p>
	 * 默认包含所有字段，
	 */
	String[] whereIncludeColumns() default {};

	/**
	 * @return 标识在参数容器中用于指定包含字段列表的`key`
	 */
	String keyWhereIncludeColumns() default EntityStatements.Keys.WHERE_INCLUDE_COLUMNS;

	/**
	 * @return 标识排除的字段，当字段在排除列表中时，即使值非空也不执行其SQL构建。
	 * <p>
	 * 默认不排除任何字段
	 */
	String[] whereExcludeColumns() default {};

	/**
	 * @return 标识在参数容器中用于指定包含字段排除列表的`key`
	 */
	String keyWhereExcludeColumns() default EntityStatements.Keys.WHERE_EXCLUDE_COLUMNS;

	/**
	 * @return 标识即使字段值为空时仍要包含的字段。这些字段无论是否空值，都会执行其SQL构建。
	 * <p>
	 * 默认不包含空值字段
	 */
	String[] whereIncludeEmptyColumns() default {};


	/**
	 * @return 标识在参数容器中用于指定包含空值字段列表的`key`
	 */
	String keyWhereIncludeEmptyColumns() default EntityStatements.Keys.WHERE_INCLUDE_EMPTY_COLUMNS;

	/**
	 * @return 标识是否包含空值字段，如果包含则对于空值字段也执行其SQL构建。
	 * <p>
	 * 默认不包含。
	 */
	boolean whereIncludeEmpty() default false;

	/**
	 * @return 标识在参数容器中用于指定是否包含空值字段开关的`key`
	 */
	String keyWhereIncludeEmpty() default EntityStatements.Keys.WHERE_INCLUDE_EMPTY;


}
