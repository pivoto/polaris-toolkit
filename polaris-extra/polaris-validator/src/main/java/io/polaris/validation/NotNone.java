package io.polaris.validation;

import io.polaris.validation.validator.NotNoneValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 校验字符串不能为空
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotNoneValidator.class)
public @interface NotNone {

	String message() default "{io.polaris.validation.NotNone.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
