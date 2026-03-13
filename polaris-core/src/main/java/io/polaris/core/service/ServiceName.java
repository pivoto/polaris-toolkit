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
@Alternative(SpiName.class)
public @interface ServiceName {

	String value();

}

