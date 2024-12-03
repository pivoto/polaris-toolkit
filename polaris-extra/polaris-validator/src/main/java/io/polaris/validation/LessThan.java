package io.polaris.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Max;

import io.polaris.validation.validator.LessThanArrayValidator;
import io.polaris.validation.validator.LessThanCollectionValidator;
import io.polaris.validation.validator.LessThanValidator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

/**
 * 校验Integer必须小于某值
 *
 * @author Qt
 * @see Max
 * @since 1.8
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {LessThanValidator.class, LessThanArrayValidator.class, LessThanCollectionValidator.class})
public @interface LessThan {

	String message() default "{io.polaris.validation.LessThan.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	long value();
}
