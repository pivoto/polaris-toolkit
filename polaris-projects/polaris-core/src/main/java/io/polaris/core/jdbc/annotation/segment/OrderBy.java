package io.polaris.core.jdbc.annotation.segment;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.sql.consts.Direction;

/**
 * @author Qt
 * @since  Jan 28, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
@Inherited
public @interface OrderBy {
	/**
	 * @return 原始sql语句
	 */
	String raw() default "";
	/**
	 * @return 实体字段
	 */
	String field() default "";


	/**
	 * @return 函数，如`coalesce(${ref},1)`等
	 */
	Function[] functions() default {};

	Direction direction() default Direction.ASC;

	Condition[] condition() default {};
}
