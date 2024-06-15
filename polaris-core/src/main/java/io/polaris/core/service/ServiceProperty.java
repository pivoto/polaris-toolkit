package io.polaris.core.service;

import java.lang.annotation.*;

/**
 * @author Qt
 * @since 1.8
 */
@Repeatable(ServiceProperties.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceProperty {

	String name();

	String value();

}
