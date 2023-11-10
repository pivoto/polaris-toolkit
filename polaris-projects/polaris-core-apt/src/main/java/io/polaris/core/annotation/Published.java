package io.polaris.core.annotation;

import java.lang.annotation.*;

/**
 * 标记为已公开应用而不可轻易变更的类或方法，发生变更时须慎重
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface Published {

	/** 备注说明 */
	String[] value() default "";

}
