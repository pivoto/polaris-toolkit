/*
 * Copyright (c) 2016-9-7 alex
 */

package io.polaris.validation;

import io.polaris.validation.validator.RegexpValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 校验字符串必须满足正则范式
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegexpValidator.class)
public @interface Regexp {

	String message() default "{io.polaris.validation.Regexp.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String value();

	int flags() default 0;
}
