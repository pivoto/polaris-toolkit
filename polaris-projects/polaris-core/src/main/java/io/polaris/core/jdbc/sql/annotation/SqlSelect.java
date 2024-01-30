package io.polaris.core.jdbc.sql.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.sql.annotation.segment.ColumnPredicate;
import io.polaris.core.jdbc.sql.annotation.segment.GroupBy;
import io.polaris.core.jdbc.sql.annotation.segment.Having;
import io.polaris.core.jdbc.sql.annotation.segment.Join;
import io.polaris.core.jdbc.sql.annotation.segment.OrderBy;
import io.polaris.core.jdbc.sql.annotation.segment.SelectColumn;
import io.polaris.core.jdbc.sql.annotation.segment.Where;

/**
 * @author Qt
 * @since 1.8,  Jan 27, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
@Inherited
public @interface SqlSelect {

	/**
	 * @return 标识目标实体类型
	 */
	Class<?> table();

	/**
	 * @return 表别名
	 */
	String alias() default "";

	/**
	 * @return 标识是否转为`count`语句
	 */
	boolean count() default false;

	/**
	 * @return 标识查询的字段列表，默认查询全部
	 */
	SelectColumn[] columns() default {};

	boolean quotaSelectAlias() default false;

	Join[] join() default {};

	Where where() default @Where();

	GroupBy[] groupBy() default {};

	Having having() default @Having();


	OrderBy[] orderBy() default {};
	/**
	 * @return 标识在参数容器中映射`order by`条件参数值的`key`
	 */
	String orderByKey() default "";

	ColumnPredicate columnPredicate() default @ColumnPredicate();

}
