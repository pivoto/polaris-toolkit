package io.polaris.core.jdbc.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.base.DefaultParameterPreparer;
import io.polaris.core.jdbc.base.ParameterPreparer;

/**
 * @author Qt
 * @since 1.8,  Feb 06, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
@Inherited
public @interface Options {

	int fetchSize() default -1;

	int timeout() default -1;

	boolean useGeneratedKeys() default false;

	String[] keyProperty() default {};

	String[] keyColumn() default {};

	Class<? extends ParameterPreparer> parameterPreparer() default DefaultParameterPreparer.class;

}
