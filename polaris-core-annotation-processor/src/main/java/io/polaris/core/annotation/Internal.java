package io.polaris.core.annotation;

import java.lang.annotation.*;

/**
 * 内部使用
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE, ElementType.FIELD})
public @interface Internal {

	/** 备注说明 */
	String[] value() default "";

}
