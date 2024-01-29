package io.polaris.core.jdbc.sql.annotation.segment;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 * @since 1.8,  Jan 28, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
@Inherited
public @interface Criterion {
	/**
	 * @return 原始sql语句
	 */
	String raw() default "";

	/**
	 * @return 查询字段
	 */
	String field() default "";

	/**
	 * @return 查询函数，如`coalesce(${ref},1)`等
	 */
	String function() default "";


	BindingKey eq() default @BindingKey;

	BindingKey ne() default @BindingKey;

	BindingKey gt() default @BindingKey;

	BindingKey ge() default @BindingKey;

	BindingKey lt() default @BindingKey;

	BindingKey le() default @BindingKey;


	BindingKey isNull() default @BindingKey;

	BindingKey notNull() default @BindingKey;

	BindingKey contains() default @BindingKey;

	BindingKey notContains() default @BindingKey;

	BindingKey like() default @BindingKey;

	BindingKey notLike() default @BindingKey;

	BindingKey between() default @BindingKey;

	BindingKey notBetween() default @BindingKey;

	BindingKey in() default @BindingKey;

	BindingKey notIn() default @BindingKey;

}
