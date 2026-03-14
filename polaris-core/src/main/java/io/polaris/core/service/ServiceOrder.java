package io.polaris.core.service;

import java.lang.annotation.*;

import io.polaris.core.annotation.Alternative;
import io.polaris.core.spi.SpiOrder;

/**
 * @author Qt
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Deprecated
@Alternative(SpiOrder.class)
public @interface ServiceOrder {

	int value() default 0;

	int HIGHEST = Integer.MIN_VALUE;

	int LOWEST = Integer.MAX_VALUE;
}

