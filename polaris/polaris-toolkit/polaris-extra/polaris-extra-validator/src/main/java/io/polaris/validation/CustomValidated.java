package io.polaris.validation;

import io.polaris.validation.validator.CustomValidation;
import io.polaris.validation.validator.CustomValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义校验器
 * <p>
 * 尽量使用已存在的校验注解:
 * <ul>
 * <li>{@link javax.validation.constraints.AssertFalse }</li>
 * <li>{@link javax.validation.constraints.AssertTrue }</li>
 * <li>{@link javax.validation.constraints.DecimalMax }</li>
 * <li>{@link javax.validation.constraints.DecimalMin }</li>
 * <li>{@link javax.validation.constraints.Digits }</li>
 * <li>{@link javax.validation.constraints.Email }</li>
 * <li>{@link javax.validation.constraints.Future }</li>
 * <li>{@link javax.validation.constraints.FutureOrPresent }</li>
 * <li>{@link javax.validation.constraints.Max }</li>
 * <li>{@link javax.validation.constraints.Min }</li>
 * <li>{@link javax.validation.constraints.Negative }</li>
 * <li>{@link javax.validation.constraints.NegativeOrZero }</li>
 * <li>{@link javax.validation.constraints.NotBlank }</li>
 * <li>{@link javax.validation.constraints.NotEmpty }</li>
 * <li>{@link javax.validation.constraints.NotNull }</li>
 * <li>{@link javax.validation.constraints.Null }</li>
 * <li>{@link javax.validation.constraints.Past }</li>
 * <li>{@link javax.validation.constraints.PastOrPresent }</li>
 * <li>{@link javax.validation.constraints.Pattern }</li>
 * <li>{@link javax.validation.constraints.Positive }</li>
 * <li>{@link javax.validation.constraints.PositiveOrZero }</li>
 * <li>{@link javax.validation.constraints.Size }</li>
 * <li>{@link DecimalScale }</li>
 * <li>{@link GreaterThan }</li>
 * <li>{@link LengthEquals }</li>
 * <li>{@link LengthMax }</li>
 * <li>{@link LengthMin }</li>
 * <li>{@link LengthRange }</li>
 * <li>{@link LessThan }</li>
 * <li>{@link NotNone }</li>
 * <li>{@link Numeric }</li>
 * <li>{@link Regexp }</li>
 * </ul>
 *
 * @author Qt
 * @since 1.8
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomValidator.class)
public @interface CustomValidated {

	String message() default "{io.polaris.validation.CustomValidated.message}";

	String[] arguments() default {};

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	Class<? extends CustomValidation> value();
}
