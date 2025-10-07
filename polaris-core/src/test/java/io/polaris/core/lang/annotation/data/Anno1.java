package io.polaris.core.lang.annotation.data;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.lang.annotation.Alias;

/**
 * @author Qt
 * @since Oct 05, 2025
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.PACKAGE, ElementType.LOCAL_VARIABLE, ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Anno1 {

	@Alias(annotation = Anno1.class, value = "name")
	String value() default "Anno1";

	@Alias(annotation = Anno1.class, value = "value")
	String name() default "Anno1";

	String desc() default "Anno1.desc";
}
