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
public @interface JoinCriterion {
	/**
	 * @return 原始sql语句
	 */
	String raw() default "";

	/**
	 * @return 实体字段
	 */
	String field() default "";


	JoinColumn eq() default @JoinColumn(tableAlias = "",tableField = "");

	JoinColumn ne() default @JoinColumn(tableAlias = "",tableField = "");

	JoinColumn gt() default @JoinColumn(tableAlias = "",tableField = "");

	JoinColumn ge() default @JoinColumn(tableAlias = "",tableField = "");

	JoinColumn lt() default @JoinColumn(tableAlias = "",tableField = "");

	JoinColumn le() default @JoinColumn(tableAlias = "",tableField = "");

	JoinColumn[] between() default {};

	JoinColumn[] notBetween() default {};


	/**
	 * @return 函数，如`coalesce(${ref},1)`等
	 */
	Function[] functions() default {};

	boolean count() default false;

	boolean sum() default false;

	boolean max() default false;

	boolean min() default false;

	boolean avg() default false;
}
