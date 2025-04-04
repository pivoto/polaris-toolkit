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
 * @since  Jan 27, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
@Inherited
public @interface EntitySelect {

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
	 * @return 标识是否只使用主键作为`where`条件
	 */
	boolean byId() default true;

	/**
	 * @return 标识是否排除逻辑删除状态的数据，即添加非逻辑删除`where`条件子句
	 */
	boolean exceptLogicDeleted() default false;

	/**
	 * @return 标识在参数容器中映射实体参数值的`key`
	 */
	String entityKey() default BindingKeys.ENTITY;

	/**
	 * @return 标识在参数容器中映射`where`条件参数值的`key`
	 */
	String whereKey() default BindingKeys.WHERE;

	/**
	 * @return 标识在参数容器中映射`order by`条件参数值的`key`
	 */
	String orderByKey() default BindingKeys.ORDER_BY;

	ColumnPredicate columnPredicate() default @ColumnPredicate(
		includeColumnsKey = BindingKeys.INCLUDE_COLUMNS,
		excludeColumnsKey = BindingKeys.EXCLUDE_COLUMNS,
		includeEmptyColumnsKey = BindingKeys.INCLUDE_EMPTY_COLUMNS,
		includeAllEmptyKey = BindingKeys.INCLUDE_EMPTY
	);

}
