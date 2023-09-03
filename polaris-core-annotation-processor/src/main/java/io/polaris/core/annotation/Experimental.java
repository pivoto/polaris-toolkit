package io.polaris.core.annotation;

import java.lang.annotation.*;

/**
 * 实验性质标记，表示当前版本下引入而未充分测试和应用的新特性
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE, ElementType.FIELD})
public @interface Experimental {

	/** 备注说明 */
	String[] value() default "";

}
