package io.polaris.core.jdbc.annotation.segment;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 * @since 1.8,  Feb 07, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
@Inherited
public @interface SqlRawItem3 {

	String[] value() default {};

	SqlRawItem4[] subset() default {};

	Condition[] condition() default {};

	String forEachKey() default "";

	String itemKey() default "";

	String indexKey() default "";

	String[] open() default {};

	String[] close() default {};

	String[] separator() default {};

}
