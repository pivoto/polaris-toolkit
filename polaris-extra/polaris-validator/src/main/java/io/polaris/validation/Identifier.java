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

import io.polaris.validation.validator.IdentifierArrayValidator;
import io.polaris.validation.validator.IdentifierCollectionValidator;
import io.polaris.validation.validator.IdentifierValidator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

/**
 * 校验字符串必须满足常规标识符书写格式
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {IdentifierValidator.class, IdentifierCollectionValidator.class, IdentifierArrayValidator.class})
public @interface Identifier {

	String message() default "{io.polaris.validation.Identifier.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	/**
	 * 正则表达式,默认支持 \w . $ + -
	 */
	String regexp() default "^[\\w.$+-]*$";

	int flags() default 0;
}
