package io.polaris.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import io.polaris.validation.validator.InNumberArrayValidator;
import io.polaris.validation.validator.InNumberCollectionValidator;
import io.polaris.validation.validator.InNumberValidator;
import io.polaris.validation.validator.InStringArrayValidator;
import io.polaris.validation.validator.InStringCollectionValidator;
import io.polaris.validation.validator.InStringValidator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

/**
 * 枚举名称校验
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {InNumberValidator.class, InNumberCollectionValidator.class, InNumberArrayValidator.class})
public @interface InNumber {

	String message() default "{io.polaris.validation.InNumber.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	long[] value();
}
