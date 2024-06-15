package io.polaris.validation;

import io.polaris.validation.validator.RuntimeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author Qt
 * @since 1.8
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RuntimeValidator.class)
public @interface RuntimeValidated {

	String message() default "{io.polaris.validation.RuntimeValidated.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
