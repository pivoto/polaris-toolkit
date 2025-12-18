package io.polaris.core.service;

import java.lang.annotation.*;

/**
 * @author Qt
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceDefault {

	int value() default 0;

}

