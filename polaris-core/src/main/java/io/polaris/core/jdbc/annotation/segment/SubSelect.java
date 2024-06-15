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
public @interface SubSelect {

	/**
	 * @return 标识目标实体类型
	 */
	Class<?> table();

	/**
	 * @return 表别名
	 */
	String alias();

	/**
	 * @return 标识查询的字段列表，默认查询全部
	 */
	SelectColumn[] columns();

	boolean quotaSelectAlias() default false;

	SubWhere where() default @SubWhere();

	GroupBy[] groupBy() default {};

	SubHaving having() default @SubHaving();

	ColumnPredicate columnPredicate() default @ColumnPredicate();

}
