/*
 * Copyright (c) 2016-8-31
 * created by alex
 */

package io.polaris.validation;


import io.polaris.validation.validator.LengthEqualsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 字符串长度值校验
 *
 * @author Qt
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LengthEqualsValidator.class)
public @interface LengthEquals {

	String message() default "{io.polaris.validation.LengthEquals.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	int value();
}
