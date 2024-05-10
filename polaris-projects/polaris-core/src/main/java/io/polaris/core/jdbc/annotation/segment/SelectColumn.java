package io.polaris.core.jdbc.annotation.segment;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 * @since  Jan 28, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
@Inherited
public @interface SelectColumn {
	/**
	 * @return 原始sql语句
	 */
	String raw() default "";

	/**
	 * @return 实体字段
	 */
	String field() default "";

	/**
	 * @return 函数，如`max(${ref})`、`coalesce(${ref},1)`等
	 */
	Function[] functions() default {};

	/**
	 * @return 别名，指定固定值时不能为空
	 */
	String alias() default "";

	/**
	 * @return 指定固定值的绑定`Key`
	 */
	String valueKey() default "";

	boolean aliasWithField() default false;

	String aliasPrefix() default "";

	String aliasSuffix() default "";

	Condition[] condition() default {};
}
