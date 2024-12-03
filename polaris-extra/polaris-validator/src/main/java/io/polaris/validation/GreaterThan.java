package io.polaris.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Min;

import io.polaris.validation.validator.GreaterThanArrayValidator;
import io.polaris.validation.validator.GreaterThanCollectionValidator;
import io.polaris.validation.validator.GreaterThanValidator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

/**
 * 校验Integer值大于某值
 *
 * @author Qt
 * @see Min
 * @since 1.8
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {GreaterThanValidator.class, GreaterThanCollectionValidator.class, GreaterThanArrayValidator.class})
public @interface GreaterThan {

	String message() default "{io.polaris.validation.GreaterThan.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	long value();
}
