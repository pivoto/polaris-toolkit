package io.polaris.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import io.polaris.validation.validator.LengthMinArrayValidator;
import io.polaris.validation.validator.LengthMinCollectionValidator;
import io.polaris.validation.validator.LengthMinValidator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

/**
 * 校验字符串长度的在最小值限制内
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {LengthMinValidator.class, LengthMinArrayValidator.class, LengthMinCollectionValidator.class})
public @interface LengthMin {

	String message() default "{io.polaris.validation.LengthMin.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	int value();
}
