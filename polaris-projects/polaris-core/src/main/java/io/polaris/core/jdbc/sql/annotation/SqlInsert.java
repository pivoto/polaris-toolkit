package io.polaris.core.jdbc.sql.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.sql.annotation.segment.InsertColumn;

/**
 * @author Qt
 * @since 1.8,  Jan 27, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
@Inherited
public @interface SqlInsert {

	/**
	 * @return 标识目标实体类型
	 */
	Class<?> table();

	InsertColumn[] columns();

	/**
	 * @return 是否启用`REPLACE`关键词
	 * <p>
	 * 默认为否
	 */
	boolean enableReplace() default false;

	/**
	 * @return 是否启用`ON DUPLICATE KEY UPDATE`关键词
	 * <p>
	 * 默认为否
	 */
	boolean enableUpdateByDuplicateKey() default false;

}