package io.polaris.validation.path;

import java.lang.annotation.*;

/**
 * @author Qt
 * @since 1.8
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Paths {

	Path[] value() default {};

}
