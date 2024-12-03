package io.polaris.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import io.polaris.validation.validator.LengthMaxArrayValidator;
import io.polaris.validation.validator.LengthMaxCollectionValidator;
import io.polaris.validation.validator.LengthMaxValidator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

/**
 * 校验字符串长度的在最大值限制内
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {LengthMaxValidator.class, LengthMaxArrayValidator.class, LengthMaxCollectionValidator.class})
public @interface LengthMax {

	String message() default "{io.polaris.validation.LengthMax.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	int value();
}
