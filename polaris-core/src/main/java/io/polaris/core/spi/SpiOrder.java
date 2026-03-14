package io.polaris.core.spi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpiOrder {

	int value() default 0;

	int HIGHEST = Integer.MIN_VALUE;

	int LOWEST = Integer.MAX_VALUE;
}

