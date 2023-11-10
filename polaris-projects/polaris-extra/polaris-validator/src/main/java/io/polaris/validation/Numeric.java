package io.polaris.validation;

import io.polaris.validation.validator.NumericValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 校验必须是数字
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NumericValidator.class)
public @interface Numeric {

	String message() default "{io.polaris.validation.Numeric.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
