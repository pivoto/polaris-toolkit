package io.polaris.core.jdbc.dbv.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.dbv.DbvColumnGetter;

/**
 * @author Qt
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbvColumn {

	String value();

	Class<? extends DbvColumnGetter> getter() default DbvColumnGetter.class;
}
