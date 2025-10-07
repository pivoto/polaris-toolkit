package io.polaris.core.jdbc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 * @see java.beans.Transient
 */
@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	String value();

	String alias() default "";

	String schema() default "";

	String catalog() default "";

	String metaSuffix() default "Meta";

	boolean sqlGenerated() default true;

	String sqlSuffix() default "Sql";

}
