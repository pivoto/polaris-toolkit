package io.polaris.core.annotation;

import java.lang.annotation.*;

/**
 * 标记为注解处理器依赖，变更时需同步变更注解处理器实现
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface AnnotationProcessing {

	/** 备注说明 */
	String[] value() default "";

}
