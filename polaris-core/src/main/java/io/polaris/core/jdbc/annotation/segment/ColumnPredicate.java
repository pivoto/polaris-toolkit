package io.polaris.core.jdbc.annotation.segment;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.sql.consts.BindingKeys;

/**
 * @author Qt
 * @since  Jan 29, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
@Inherited
public @interface ColumnPredicate {

	/**
	 * @return 标识需要包含的字段，当字段在包含列表时，才会执行其SQL构建。
	 * <p>
	 * 默认包含所有字段，
	 */
	String[] includeColumns() default {};

	/**
	 * @return 标识在参数容器中用于指定包含字段列表的`key`
	 */
	String includeColumnsKey() default "";

	/**
	 * @return 标识排除的字段，当字段在排除列表中时，即使值非空也不执行其SQL构建。
	 * <p>
	 * 默认不排除任何字段
	 */
	String[] excludeColumns() default {};

	/**
	 * @return 标识在参数容器中用于指定包含字段排除列表的`key`
	 */
	String excludeColumnsKey() default "";

	/**
	 * @return 标识即使字段值为空时仍要包含的字段。这些字段无论是否空值，都会执行其SQL构建。
	 * <p>
	 * 默认不包含空值字段
	 */
	String[] includeEmptyColumns() default {};


	/**
	 * @return 标识在参数容器中用于指定包含空值字段列表的`key`
	 */
	String includeEmptyColumnsKey() default "";

	/**
	 * @return 标识是否包含空值字段，如果包含则对于空值字段也执行其SQL构建。
	 * <p>
	 * 默认不包含。
	 */
	boolean includeAllEmpty() default false;

	/**
	 * @return 标识在参数容器中用于指定是否包含空值字段开关的`key`
	 */
	String includeAllEmptyKey() default "";
}
