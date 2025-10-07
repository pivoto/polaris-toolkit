package io.polaris.core.lang.annotation.data;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 * @since Oct 05, 2025
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.PACKAGE, ElementType.LOCAL_VARIABLE, ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Anno {

	String value() default "";

	int intValue() default 0;

	long longValue() default 0;

	double doubleValue() default 0;

	float floatValue() default 0;

	boolean booleanValue() default false;

	char charValue() default '\0';

	byte byteValue() default 0;

	short shortValue() default 0;

	Class<?> classValue() default Object.class;

	AnnoSub annoValue() default @AnnoSub();

	String[] stringArrayValue() default {};

	int[] intArrayValue() default {};

	long[] longArrayValue() default {};

	double[] doubleArrayValue() default {};

	float[] floatArrayValue() default {};

	boolean[] booleanArrayValue() default {};

	char[] charArrayValue() default {};

	byte[] byteArrayValue() default {};

	short[] shortArrayValue() default {};

	Class<?>[] classArrayValue() default {};

	AnnoSub[] annoArrayValue() default {};

}
