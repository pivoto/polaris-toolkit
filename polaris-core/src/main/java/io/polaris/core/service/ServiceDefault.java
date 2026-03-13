package io.polaris.core.service;

import java.lang.annotation.*;

import io.polaris.core.annotation.Alternative;

/**
 * @author Qt
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Deprecated
@Alternative(SpiDefault.class)
public @interface ServiceDefault {

	int value() default 0;

}

