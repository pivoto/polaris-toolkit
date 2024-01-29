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
public @interface SelectColumn {
	/**
	 * @return 原始sql语句
	 */
	String raw() default "";

	/**
	 * @return 查询字段
	 */
	String field() default "";

	/**
	 * @return 查询函数，如`MAX(${ref})`、`MIN(${ref})`等
	 */
	String function() default "";


	/**
	 * @return 别名，指定固定值时不能为空
	 */
	String alias() default "";

	/**
	 * @return 指定固定值的绑定`Key`
	 */
	String keyOfValue() default "";

	boolean aliasWithField() default false;

}
