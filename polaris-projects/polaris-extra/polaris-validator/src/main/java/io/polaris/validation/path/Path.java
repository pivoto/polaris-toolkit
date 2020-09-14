package io.polaris.validation.path;

import io.polaris.validation.CustomValidated;
import io.polaris.validation.Regexp;

import javax.validation.Constraint;
import javax.validation.constraints.*;
import java.lang.annotation.*;

/**
 * @author Qt
 * @since 1.8
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Paths.class)
public @interface Path {

	String path() default "";

	Class<?> valueType();

	Constraint[] validator() default {};

	DecimalMax[] decimalMax() default {};

	DecimalMin[] decimalMin() default {};

	Max[] max() default {};

	Min[] min() default {};

	PositiveOrZero[] positiveOrZero() default {};

	Negative[] negative() default {};

	NegativeOrZero[] negativeOrZero() default {};

	Digits[] digits() default {};

	Email[] email() default {};

	Size[] size() default {};

	NotBlank[] notBlank() default {};

	NotEmpty[] notEmpty() default {};

	NotNull[] notNull() default {};


	Future[] future() default {};

	FutureOrPresent[] futureOrPresent() default {};

	Past[] past() default {};

	PastOrPresent[] pastOrPresent() default {};

	Pattern[] pattern() default {};


	Regexp[] regexp() default {};

	CustomValidated[] custom() default {};

}
