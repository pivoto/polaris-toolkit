package io.polaris.core.jdbc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.annotation.segment.Condition;
import io.polaris.core.jdbc.sql.consts.SelectSetOps;

/**
 * @author Qt
 * @since  Jan 28, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
@Inherited
public @interface SqlSelectSet {

	/**
	 * @return 集合子句
	 */
	Item[] value();

	/**
	 * @return 标识是否转为`count`语句
	 */
	boolean count() default false;

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
	@Documented
	@Inherited
	@interface Item {

		SqlSelect value();

		SelectSetOps ops() default SelectSetOps.UNION_ALL;

		Condition[] condition() default {};
	}
}
