package io.polaris.validation.path;

import java.lang.annotation.*;

/**
 * @author Qt
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Paths {

	Path[] value() default {};

}
