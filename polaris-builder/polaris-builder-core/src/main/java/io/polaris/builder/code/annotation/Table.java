package io.polaris.builder.code.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 * @since 1.8
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

	/**
	 * 数据库目录名。注意特定数据库的大小写敏感性
	 */
	String catalog() default "";

	/**
	 * 数据库模式名。注意特定数据库的大小写敏感性
	 */
	String schema() default "";

	/**
	 * 表名称。注意特定数据库的大小写敏感性
	 */
	String name();

	/**
	 * java包名
	 */
	String javaPackage();

	/**
	 * 列映射配置
	 */
	Column[] columns() default {};

	Property[] property() default {};

	/** 需忽略的列名，支持正则表达式 */
	String[] ignoredColumns() default {};

}
