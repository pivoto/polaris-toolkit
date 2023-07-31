package io.polaris.core.annotation;

import java.lang.annotation.*;

/**
 * 尚未完成但因构建依赖或部分投产等原因而提交的功能
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE, ElementType.FIELD})
public @interface Uncompleted {

	String[] value() default "";

}
