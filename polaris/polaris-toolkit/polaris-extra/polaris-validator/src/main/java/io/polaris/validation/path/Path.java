package io.polaris.validation.path;

import io.polaris.validation.CustomValidated;
import io.polaris.validation.Regexp;

import javax.validation.Constraint;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
