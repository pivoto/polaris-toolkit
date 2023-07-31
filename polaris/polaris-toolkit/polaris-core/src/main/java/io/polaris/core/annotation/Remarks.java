package io.polaris.core.annotation;

import java.lang.annotation.*;

/**
 * 备注信息
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE, ElementType.FIELD})
public @interface Remarks {

	/** 备注说明 */
	String[] value() default "";

}
