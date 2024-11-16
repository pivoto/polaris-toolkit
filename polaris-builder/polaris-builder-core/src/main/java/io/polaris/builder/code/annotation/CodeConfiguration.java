package io.polaris.builder.code.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.polaris.builder.code.annotation.Template.*;

/**
 * @author Qt
 * @since 1.8
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CodeConfiguration {

	/**
	 * 代码生成器的输出日志输出到标准输出流
	 */
	boolean logWithStd() default true;

	/**
	 * 生成文件的输出目录
	 */
	String outDir() default ".";

	/**
	 * 模板全局属性配置，优先高于默认配置
	 */
	Property[] property() default {};

	/**
	 * Jdbc类型与Java类型的自定义映射
	 */
	Mapping[] mapping() default {};

	/**
	 * 需要截断的表名前缀
	 */
	String tablePrefix() default "_,t_,tbl_";

	/** 需要截断的表名后缀 */
	String tableSuffix() default "_,_bak,_tmp";

	/**
	 * 需要截断的列名前缀
	 */
	String columnPrefix() default "_";

	/**
	 * 需要截断的列名后缀
	 */
	String columnSuffix() default "_";

	/**
	 * 自定义模板配置，未配置时使用默认
	 */
	Template[] templates() default {};

	/**
	 * Jdbc连接驱动
	 */
	String jdbcDriver() default "";

	/**
	 * Jdbc连接URL
	 */
	String jdbcUrl();

	/**
	 * Jdbc连接User
	 */
	String jdbcUsername();

	/**
	 * Jdbc连接Password
	 */
	String jdbcPassword();


	/**
	 * 需要生成代码的表名及其配置
	 */
	Table[] tables() default {};

	/** 需忽略的列名，支持正则表达式 */
	String[] ignoredColumns() default {};
}
