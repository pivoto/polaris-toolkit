package io.polaris.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import io.polaris.validation.validator.NumericArrayValidator;
import io.polaris.validation.validator.NumericCollectionValidator;
import io.polaris.validation.validator.NumericValidator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

/**
 * 校验必须是数字
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {NumericValidator.class, NumericArrayValidator.class, NumericCollectionValidator.class})
public @interface Numeric {

	String message() default "{io.polaris.validation.Numeric.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
