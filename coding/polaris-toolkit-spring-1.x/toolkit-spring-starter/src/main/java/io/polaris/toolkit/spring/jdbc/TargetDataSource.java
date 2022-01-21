package io.polaris.toolkit.spring.jdbc;

import io.polaris.toolkit.spring.constants.ToolkitConstants;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 * @version Dec 30, 2021
 * @since 1.8
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {

	String value() default ToolkitConstants.DYNAMIC_DATASOURCE_DEFAULT_KEY;

}
