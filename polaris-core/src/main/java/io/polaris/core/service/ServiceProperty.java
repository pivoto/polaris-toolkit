package io.polaris.core.service;

import java.lang.annotation.*;

import io.polaris.core.annotation.Alternative;

/**
 * @author Qt
 */
@Repeatable(ServiceProperties.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
@Alternative(SpiProperty.class)
public @interface ServiceProperty {

	String name();

	String value();

}
