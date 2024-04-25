package io.polaris.core.jdbc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.annotation.segment.ColumnPredicate;
import io.polaris.core.jdbc.annotation.segment.GroupBy;
import io.polaris.core.jdbc.annotation.segment.Having;
import io.polaris.core.jdbc.annotation.segment.Join;
import io.polaris.core.jdbc.annotation.segment.OrderBy;
import io.polaris.core.jdbc.annotation.segment.SelectColumn;
import io.polaris.core.jdbc.annotation.segment.Where;

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

	/**
	 * @return 标识别名是否添加引号
	 */
	boolean quotaSelectAlias() default false;

	/**
	 * @return 表连接配置
	 */
	Join[] join() default {};

	/**
	 * @return Where条件，默认无条件
	 */
	Where where() default @Where();

	/**
	 * @return 标识分组字段列表，默认无分组
	 */
	GroupBy[] groupBy() default {};

	/**
	 * @return Having条件，默认无条件
	 */
	Having having() default @Having();


	/**
	 * @return 标识在参数容器中映射`order by`条件参数值的`key`，优先级高于{@link #orderBy()}
	 */
	String orderByKey() default "";
	/**
	 * @return 标识排序字段列表，默认无排序
	 */
	OrderBy[] orderBy() default {};

	ColumnPredicate columnPredicate() default @ColumnPredicate();

}
