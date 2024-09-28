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
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Expression {

	/** 数据库字段表达式 */
	String value();

	/** 是否可查询 */
	boolean selectable() default true;

	/** 表别名占位符，带`.`分隔符 */
	String tableAliasPlaceholder() default "$T.";

	/** java.sql.Types 的 SQL 类型 */
	String jdbcType() default "";

}
