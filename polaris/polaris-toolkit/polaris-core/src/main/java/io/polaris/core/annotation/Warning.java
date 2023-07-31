package io.polaris.core.annotation;

import java.lang.annotation.*;

/**
 * 警告标记，表示类或方法即将弃用
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE, ElementType.FIELD})
public @interface Warning {

	/** 警告说明 */
	String[] value() default "";

}
