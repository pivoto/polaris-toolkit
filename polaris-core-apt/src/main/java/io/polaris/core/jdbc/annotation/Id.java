package io.polaris.core.jdbc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
	/** 是否自增主键 */
	boolean auto() default false;

	/**
	 * 产生主键值的数据库序列名，数据新增时使用。
	 * 默认赋值表达式为`seq_name.nextval`
	 */
	String seqName() default "";

	/**
	 * 产生主键值的数据库SQL表达式，数据新增时使用。
	 * 区别于常规自增或序列值的使用，完全使用自定义语句，
	 * 如`seq_name.nextval`、`uuid()`、`sys_guid()`等
	 */
	String sql() default "";
}
