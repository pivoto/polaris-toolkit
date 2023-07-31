/*
 * Copyright (c) 2016-9-8 alex
 */

package io.polaris.validation;

import io.polaris.validation.validator.LengthRangeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


/**
 * 校验字符串长度必须在范围内
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LengthRangeValidator.class)
public @interface LengthRange {

	String message() default "{io.polaris.validation.LengthRange.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	int min();

	int max();
}
