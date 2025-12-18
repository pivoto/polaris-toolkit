/*
 * Copyright (c) 2016-9-7 alex
 */

package io.polaris.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import io.polaris.validation.validator.RegexpArrayValidator;
import io.polaris.validation.validator.RegexpCollectionValidator;
import io.polaris.validation.validator.RegexpValidator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

/**
 * 校验字符串必须满足正则范式
 *
 * @author Qt
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {RegexpValidator.class, RegexpArrayValidator.class, RegexpCollectionValidator.class})
public @interface Regexp {

	String message() default "{io.polaris.validation.Regexp.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String value();

	int flags() default 0;
}
