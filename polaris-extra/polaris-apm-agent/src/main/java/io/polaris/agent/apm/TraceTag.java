package io.polaris.agent.apm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(TraceTags.class)
public @interface TraceTag {

	/**
	 * 标签key
	 *
	 * @return
	 */
	String key();

	/**
	 * 标签值，groovy表达式
	 *
	 * @return
	 */
	String value();

	/**
	 * 是否在方法体执行前标记
	 *
	 * @return
	 */
	boolean before() default true;

}
