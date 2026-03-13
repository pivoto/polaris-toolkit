package io.polaris.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.lang.annotation.Alias;

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
	 * 替代类信息
	 */
	Class<?>[] value() default {};


	/**
	 * 替代类或方法信息等描述
	 */
	@Alias(annotation = Remarks.class, value = "value")
	String[] remarks() default "";
}
