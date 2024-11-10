package io.polaris.validation;

import io.polaris.validation.validator.RuntimeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

/**
 * @author Qt
 * @since 1.8
 */
@Documented
@Target({ElementType.TYPE, METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RuntimeValidator.class)
public @interface RuntimeValidated {

	String message() default "{io.polaris.validation.RuntimeValidated.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
