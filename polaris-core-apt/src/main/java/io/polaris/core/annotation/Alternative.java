package io.polaris.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识该类或方法的替代类或方法信息
 *
 * @author Qt
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE, ElementType.FIELD})
public @interface Alternative {

	/**
	 * 替代类或方法信息
	 */
	String[] value() default "";

	/**
	 * 替代类信息
	 */
	Class<?>[] type() default {};

}
