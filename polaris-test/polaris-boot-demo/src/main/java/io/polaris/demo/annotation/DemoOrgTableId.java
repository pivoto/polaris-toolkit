package io.polaris.demo.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.annotation.ColumnProperties;
import io.polaris.core.jdbc.annotation.ColumnProperty;

/**
 * @author Qt
 * @since Oct 04, 2025
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@ColumnProperty(key = "k3", stringValue = "v3")
@ColumnProperties({
	@ColumnProperty(key = "k4", stringValue = "v4"),
	@ColumnProperty(key = "k5", stringValue = "v5")
})
public @interface DemoOrgTableId {
}
