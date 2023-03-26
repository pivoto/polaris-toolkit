package io.polaris.dbv.annotation;

import io.polaris.dbv.ColumnValueGetter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnHandler {

	Class<? extends ColumnValueGetter> value();
}
