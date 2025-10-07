package io.polaris.core.jdbc.entity.merge;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.annotation.Table;
import io.polaris.core.lang.annotation.Alias;

/**
 * @author Qt
 * @since Oct 08, 2025
 */
@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableX2 {

	@Alias(value = "value", annotation = Table.class)
	String table();


}
