package io.polaris.core.jdbc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.annotation.segment.ColumnPredicate;
import io.polaris.core.jdbc.sql.consts.BindingKeys;

/**
 * @author Qt
 * @since 1.8,  Jan 27, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
@Inherited
public @interface EntityMerge {

	/**
	 * @return 标识目标实体类型
	 */
	Class<?> table();

	/**
	 * @return 表别名
	 */
	String alias() default "";

	/**
	 * @return 标识在参数容器中映射实体参数值的`key`
	 */
	String entityKey() default BindingKeys.ENTITY;

	/**
	 * @return 标识是否执行`UPDATE`操作
	 */
	boolean updateWhenMatched() default true;

	/**
	 * @return 标识是否执行`INSERT`操作
	 */
	boolean insertWhenNotMatched() default true;

	ColumnPredicate columnPredicate() default @ColumnPredicate(
		includeColumnsKey = BindingKeys.INCLUDE_COLUMNS,
		excludeColumnsKey = BindingKeys.EXCLUDE_COLUMNS,
		includeEmptyColumnsKey = BindingKeys.INCLUDE_EMPTY_COLUMNS,
		includeAllEmptyKey = BindingKeys.INCLUDE_EMPTY
	);


}
