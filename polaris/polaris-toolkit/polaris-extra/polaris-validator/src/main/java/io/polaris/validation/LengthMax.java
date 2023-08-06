package io.polaris.validation;

import io.polaris.validation.validator.LengthMaxValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 校验字符串长度的在最大值限制内
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LengthMaxValidator.class)
public @interface LengthMax {

	String message() default "{io.polaris.validation.LengthMax.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	int value();
}
