package io.polaris.core.jdbc.annotation;

import java.lang.annotation.*;

/**
 * @author Qt
 * @see java.beans.Transient
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	String name() default "";

	String schema() default "";

	String catalog() default "";

	String metaSuffix() default "Meta";
}
