package io.polaris.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import io.polaris.validation.validator.NotNoneArrayValidator;
import io.polaris.validation.validator.NotNoneCollectionValidator;
import io.polaris.validation.validator.NotNoneValidator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

/**
 * 校验字符串不能为空
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {NotNoneValidator.class, NotNoneArrayValidator.class, NotNoneCollectionValidator.class})
public @interface NotNone {

	String message() default "{io.polaris.validation.NotNone.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
