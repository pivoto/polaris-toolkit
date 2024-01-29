package io.polaris.core.jdbc.sql.annotation.segment;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * @author Qt
 * @since 1.8,  Jan 28, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
@Inherited
public @interface BindingKey {

	String value();

	PredicateType predicateType() default PredicateType.NOT_EMPTY;

	Class<? extends BiPredicate<Map<String,Object>,String>>[] predicateClass() default {};

	enum PredicateType {
		NOT_NULL,
		NOT_EMPTY,
		IS_NULL,
		IS_EMPTY,
		REGEX,
		JAVA_EVALUATOR,
		GROOVY_EVALUATOR,
		CUSTOM,
	}
}
