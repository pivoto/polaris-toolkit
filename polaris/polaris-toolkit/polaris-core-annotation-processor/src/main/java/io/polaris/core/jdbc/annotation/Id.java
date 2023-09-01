package io.polaris.core.jdbc.annotation;

import java.lang.annotation.*;

/**
 * @author Qt
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
	/**
	 * 是否自增主键
	 */
	boolean auto() default false;

	/**
	 * 自增主键产生的sequence name
	 */
	String seqName() default "";

}
