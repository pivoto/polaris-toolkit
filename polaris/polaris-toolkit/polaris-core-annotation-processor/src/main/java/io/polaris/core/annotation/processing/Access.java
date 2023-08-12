package io.polaris.core.annotation.processing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 * @since 1.8
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Access {

	boolean fields() default true;

	String fieldsSuffix() default "Fields";

	boolean fluent() default true;

	String fluentSuffix() default "Fluent";

	boolean map() default false;

	String mapSuffix() default "Map";

	String[] excludeFields() default {};

	String[] excludeSetters() default {};

	String[] excludeGetters() default {};


	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.SOURCE)
	@interface ExcludeField {
	}

	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.SOURCE)
	@interface ExcludeGetter {
	}

	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.SOURCE)
	@interface ExcludeSetter {
	}
}
