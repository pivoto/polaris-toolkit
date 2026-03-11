package io.polaris.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识与该类或方法关联的其他资源信息，如相关类、方法、字段等
 *
 * @author Qt
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE, ElementType.FIELD})
public @interface See {

	/**
	 * 关联的其他资源信息，如相关类、方法、字段等
	 */
	String[] value() default "";

}
