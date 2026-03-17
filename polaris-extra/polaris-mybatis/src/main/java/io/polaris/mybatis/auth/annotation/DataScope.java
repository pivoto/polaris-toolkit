package io.polaris.mybatis.auth.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.sql.consts.Relation;

/**
 * 数据权限注解
 *
 * @author Qt
 * @since Jan 25, 2026
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataScope {

	/** 需要开启控制的数据权限 */
	String[] include() default {};

	/** 需要关闭控制的数据权限 */
	String[] exclude() default {};

	/** 多个数据权限间的作用关系，默认为并集 */
	Relation relation() default Relation.OR;

}
