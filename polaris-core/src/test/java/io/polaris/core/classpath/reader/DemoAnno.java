package io.polaris.core.classpath.reader;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 * @since Jan 28, 2026
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PACKAGE, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
@Documented
@DemoAnno1()
public @interface DemoAnno {


	DemoAnno1 anno1() default @DemoAnno1();

	DemoAnno2 anno2();

	Class<?> classVal() default String.class;

	String[] strArrayVal() default {"s1", "s2"};

	String strVal() default "test";

	int intVal() default 1;

	long longVal() default 1;

	float floatVal() default 1;

	double doubleVal() default 1;

	boolean boolVal() default true;

	char charVal() default 'a';

	byte byteVal() default 1;

	short shortVal() default 1;
}
