package io.polaris.core.lang.annotation.data;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
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
@Repeatable(RepeatableAnno1Array.class)
public @interface RepeatableAnno1 {

	@Alias(value = "name")
	String value() default "RepeatableAnno1";

	@Alias(value = "value")
	String name() default "RepeatableAnno1";

	String desc() default "RepeatableAnno1.desc";
}
