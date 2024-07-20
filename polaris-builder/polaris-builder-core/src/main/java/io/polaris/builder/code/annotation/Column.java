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
public @interface Column {

	/** 列名称 */
	String name();

	/** 自定义的Java映射类型，覆盖默认配置 */
	String javaType();

	Property[] property() default {};

}
