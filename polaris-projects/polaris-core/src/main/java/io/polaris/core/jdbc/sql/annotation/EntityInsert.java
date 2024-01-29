package io.polaris.core.jdbc.sql.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 * @since 1.8,  Jan 27, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Inherited
public @interface SqlInsert {

	/**
	 * 指定目标实体类型
	 *
	 * @return 实体类型
	 */
	Class<?> value();

	/**
	 * 指定需要包含的字段，当字段在包含列表且非空值时，才会执行其SQL构建。
	 * <p>
	 * 默认包含所有字段，
	 *
	 * @return 字段列表
	 */
	String[] includeColumns() default {};

	/**
	 * 指定排除的字段，当字段在排除列表中时，即使值非空也不执行其SQL构建。
	 * <p>
	 * 默认不排除任何字段
	 *
	 * @return 字段列表
	 */
	String[] excludeColumns() default {};

	/**
	 * 指定即使字段值为空时仍要包含的字段。这些字段无论是否空值，都会执行其SQL构建。
	 * <p>
	 * 默认不包含空值字段
	 *
	 * @return 字段列表
	 */
	String[] includeEmptyColumns() default {};

	/**
	 * 是否启用`REPLACE`关键词
	 * <p>
	 * 默认为否
	 *
	 * @return 是否启用
	 */
	boolean enableReplace() default false;

	/**
	 * 是否启用`ON DUPLICATE KEY UPDATE`关键词
	 * <p>
	 * 默认为否
	 *
	 * @return 是否启用
	 */
	boolean enableUpdateByDuplicateKey() default false;

}
