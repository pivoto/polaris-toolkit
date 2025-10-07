package io.polaris.core.jdbc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ColumnProperties.class)
public @interface ColumnProperty {
	String key();

	Type type() default Type.STRING;

	String stringValue() default "";

	Class<?> classValue() default void.class;

	int intValue() default 0;

	boolean booleanValue() default false;

	long longValue() default 0;

	double doubleValue() default 0;

	enum Type{
		STRING,
		CLASS,
		INT,
		BOOLEAN,
		LONG,
		DOUBLE,
	}
}
