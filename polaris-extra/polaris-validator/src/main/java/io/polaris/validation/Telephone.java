package io.polaris.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import io.polaris.validation.validator.InEnumArrayValidator;
import io.polaris.validation.validator.InEnumCollectionValidator;
import io.polaris.validation.validator.MobileValidator;
import io.polaris.validation.validator.TelephoneArrayValidator;
import io.polaris.validation.validator.TelephoneCollectionValidator;
import io.polaris.validation.validator.TelephoneValidator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

/**
 * 电话号码校验
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {TelephoneValidator.class, TelephoneCollectionValidator.class, TelephoneArrayValidator.class})
public @interface Telephone {

	String message() default "{io.polaris.validation.Telephone.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
