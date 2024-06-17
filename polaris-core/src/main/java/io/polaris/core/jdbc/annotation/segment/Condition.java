package io.polaris.core.jdbc.annotation.segment;

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
 * @since  Jan 28, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
@Inherited
public @interface Condition {

	String bindingKey() default "";

	PredicateType predicateType() default PredicateType.NOT_EMPTY;

	String predicateExpression() default "";

	/** groovy/javascript/java */
	String predicateScriptEngine() default "";

	String predicateCustomKey() default "";

	Class<? extends BiPredicate<Map<String, Object>, Object>>[] predicateCustomClass() default {};


	enum PredicateType {
		NOT_NULL,
		NOT_EMPTY,
		IS_NULL,
		IS_EMPTY,
		REGEX,
		SCRIPT,
		CUSTOM,
		DEFAULT,
	}
}
