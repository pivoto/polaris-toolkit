package io.polaris.core.jdbc.annotation;

import java.lang.annotation.*;

/**
 * @author Qt
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

	/** 数据库字段名称。 默认采用驼峰命名转换为下划线命名 */
	String value() default "";

	/** 标识忽略此字段 */
	boolean ignored() default false;

	/** java.sql.Types 的 SQL 类型 */
	String jdbcType() default "";

	/** 是否可为空 */
	boolean nullable() default true;

	/** 是否可新增列 */
	boolean insertable() default true;

	/** 是否可修改列 */
	boolean updatable() default true;

	/** 字段 update set 默认值 */
	String updateDefault() default "";

	/** insert的时候默认值 */
	String insertDefault() default "";

	/** 字段 update set 默认SQL */
	String updateDefaultSql() default "";

	/** insert的时候默认值SQL */
	String insertDefaultSql() default "";

	/** 版本锁字段标识 */
	boolean version() default false;

	/** 标识逻辑删除字段 */
	boolean logicDeleted() default false;


	/** 标识创建时间字段 */
	boolean createTime() default false;

	/** 标识修改时间字段 */
	boolean updateTime() default false;

}
