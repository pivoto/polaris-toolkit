package io.polaris.core.spi.condition.case01;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.spi.condition.SpiCondition;
import io.polaris.core.spi.condition.SpiConditional;

/**
 * @author Qt
 * @since Mar 24, 2026
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SpiConditional(SpiCondition01.class)
public @interface SpiConditional01 {

	String attribute() default "";
}
