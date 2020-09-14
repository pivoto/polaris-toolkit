package io.polaris.validation;

import javax.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Qt
 * @since 1.8
 */
public interface ValidationMessageFormatter {

	String formatValidationMessage(String messageTemplate, ConstraintValidatorContext context,
		Object value, Consumer<Map<String, Object>> messageParameters);

}
