package io.polaris.core.jdbc.annotation;

import java.lang.annotation.*;

/**
 * @author Qt
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableColumn {

	/**
	 * 数据库字段名称
	 * 默认采用驼峰命名转换为下划线命名
	 */
	String value() default "";

	/**
	 * 字段 update set 默认值
	 */
	String update() default "";

	/**
	 * insert的时候默认值
	 */
	String insert() default "";

	/**
	 * 版本锁字段标识
	 */
	boolean version() default false;

	/**
	 * 标识逻辑删除字段
	 */
	boolean logicDeleted() default false;

	/**
	 * 标识忽略此字段
	 */
	boolean ignored() default false;

	/**
	 * 标识创建时间字段
	 */
	boolean createTime() default false;

	/**
	 * 标识修改时间字段
	 */
	boolean updateTime() default false;
}
