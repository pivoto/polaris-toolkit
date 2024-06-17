package io.polaris.core.jdbc.annotation.segment;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.sql.consts.JoinType;

/**
 * @author Qt
 * @since  Jan 28, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
@Inherited
public @interface Join {

	/**
	 * @return 标识目标实体类型
	 */
	Class<?> table();

	/**
	 * @return 表别名
	 */
	String alias();

	JoinType type() default JoinType.JOIN;

	SelectColumn[] columns() default {};

	Criteria[] on();

	Where where() default @Where();

	GroupBy[] groupBy() default {};

	Having having() default @Having();

	OrderBy[] orderBy() default {};

	Condition[] condition() default {};

	ColumnPredicate columnPredicate() default @ColumnPredicate();
}
