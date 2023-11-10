package io.polaris.core.service;

import java.lang.annotation.*;

/**
 * @author Qt
 * @since 1.8
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceDefault {

	int value() default 0;

}

