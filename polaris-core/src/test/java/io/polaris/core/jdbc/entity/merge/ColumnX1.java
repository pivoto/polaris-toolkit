package io.polaris.core.jdbc.entity.merge;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.annotation.Column;
import io.polaris.core.lang.annotation.Alias;

/**
 * @author Qt
 * @since Oct 08, 2025
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Column(ignored = true)
public @interface ColumnX1 {
	@Alias(value = "value", annotation = Column.class)
	String name() default "";

	@Alias(value = "ignored", annotation = Column.class)
	boolean ignored() default false;

}
