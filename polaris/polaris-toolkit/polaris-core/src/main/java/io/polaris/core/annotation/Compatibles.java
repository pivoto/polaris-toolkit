package io.polaris.core.annotation;

import java.lang.annotation.*;

/**
 * 兼容处理标记，表示因考虑目标的兼容性而存在的类或方法
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE, ElementType.FIELD})
public @interface Compatibles {
	Compatible[] value() ;
}
