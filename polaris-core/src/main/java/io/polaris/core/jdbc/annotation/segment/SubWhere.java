package io.polaris.core.jdbc.annotation.segment;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.sql.consts.Relation;

/**
 * @author Qt
 * @since  Jan 28, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
@Inherited
public @interface SubWhere {

	/**
	 * @return 实体主键条件的绑定key，优先于{@link #byEntityKey}
	 */
	String byEntityIdKey() default "";

	/**
	 * @return 实体全字段条件的绑定key，未配置{@link #byEntityIdKey}时生效
	 */
	String byEntityKey() default "";

	/**
	 * @return 自定义条件
	 */
	SubCriteria[] criteria() default {};

	/**
	 * @return 自定义条件的关系连接符
	 */
	Relation relation() default Relation.AND;

}
