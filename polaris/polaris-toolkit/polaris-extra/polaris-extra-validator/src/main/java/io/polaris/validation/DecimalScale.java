/*
 * Copyright (c) 2016-8-31
 * created by alex
 */

package io.polaris.validation;

import io.polaris.validation.validator.DecimalScaleValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * BigDecimal精度校验
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DecimalScaleValidator.class)
public @interface DecimalScale {

	String message() default "{io.polaris.validation.DecimalScale.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	int min();

	int max();
}
