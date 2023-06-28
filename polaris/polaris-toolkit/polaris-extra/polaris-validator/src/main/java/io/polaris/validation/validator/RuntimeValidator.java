package io.polaris.validation.validator;

import io.polaris.core.string.Strings;
import io.polaris.validation.RuntimeValidated;
import io.polaris.validation.RuntimeValidation;
import io.polaris.validation.ValidationMessageFormatter;
import io.polaris.validation.ValidationResult;
import io.polaris.validation.Validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public class RuntimeValidator implements ConstraintValidator<RuntimeValidated, Object> {
	private static final ThreadLocal<RuntimeValidation> LOCAL = new ThreadLocal<>();
	private Class<? extends Payload>[] payload;


	public static void with(RuntimeValidation validation, Runnable runnable) {
		bind(validation);
		try {
			runnable.run();
		} finally {
			clear();
		}
	}

	public static <T> T with(RuntimeValidation validation, Supplier<T> supplier) {
		bind(validation);
		try {
			return supplier.get();
		} finally {
			clear();
		}
	}


	public static void bind(RuntimeValidation validation) {
		LOCAL.set(validation);
	}

	public static void clear() {
		LOCAL.remove();
	}

	@Override
	public void initialize(RuntimeValidated constraintAnnotation) {
		payload = constraintAnnotation.payload();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		RuntimeValidation runtimeValidation = LOCAL.get();
		if (runtimeValidation != null) {
			ValidationMessageFormatter formatter = Validations::formatValidationMessage;
			ValidationResult rs = runtimeValidation.isValid(context, value, formatter);
			if (rs != null && !rs.isValid()) {
				String message = rs.getMessage();
				if (Strings.isNotBlank(message)) {
					// 禁用默认的message的值
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate(message)
						.addConstraintViolation()
					;
				}
				return false;
			}
		}
		return true;
	}
}
