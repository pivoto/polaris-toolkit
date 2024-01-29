package io.polaris.core.jdbc.sql.annotation.segment;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 * @since 1.8,  Jan 28, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
@Inherited
public @interface Expression {

	/**
	 * @return 函数，如`coalesce(${ref},1)`等
	 */
	String function() default "";

	JoinColumn[] joinColumns() default {};

	BindingKey[] bindingKeys() default {};

}
