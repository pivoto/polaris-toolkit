package io.polaris.core.jdbc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import lombok.Getter;

/**
 * @author Qt
 * @since  Feb 05, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
@Inherited
public @interface Mapping {

	boolean caseInsensitive() default true;

	boolean caseCamel() default true;

	Class<?> entityType() default void.class;

	Column[] columns() default {};

	Composite[] composites() default {};


	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
	@Documented
	@Inherited
	@interface Column {
		String property();

		String column();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
	@Documented
	@Inherited
	@interface Composite {
		String property();

		Class<?> entityType();

		Column[] columns() default {};

		Composite1[] composites() default {};
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
	@Documented
	@Inherited
	@interface Composite1 {
		String property();

		Class<?> entityType();

		Column[] columns() default {};

		Composite2[] composites() default {};
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
	@Documented
	@Inherited
	@interface Composite2 {
		String property();

		Class<?> entityType();

		Column[] columns() default {};

		Composite3[] composites() default {};
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
	@Documented
	@Inherited
	@interface Composite3 {
		String property();

		Class<?> entityType();

		Column[] columns() default {};

		Composite4[] composites() default {};
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
	@Documented
	@Inherited
	@interface Composite4 {
		String property();

		Class<?> entityType();

		Column[] columns() default {};

		Composite5[] composites() default {};
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
	@Documented
	@Inherited
	@interface Composite5 {
		String property();

		Class<?> entityType();

		Column[] columns() default {};
	}


}
