package io.polaris.core.service;

import java.lang.annotation.*;

/**
 * @author Qt
 */
@Repeatable(ServiceProperties.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceProperty {

	String name();

	String value();

}
