package io.polaris.core.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.annotation.Alternative;
import io.polaris.core.spi.SpiProperties;

/**
 * @author Qt
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
@Alternative(SpiProperties.class)
public @interface ServiceProperties {
	ServiceProperty[] value();
}
