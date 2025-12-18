package io.polaris.validation;

import javax.validation.ConstraintValidatorContext;

/**
 * @author Qt
 */
public interface RuntimeValidation {

	/**
	 * 验证
	 *
	 * @param context
	 * @param value
	 * @param formatter
	 * @return
	 */
	ValidationResult isValid(ConstraintValidatorContext context, Object value, ValidationMessageFormatter formatter);

}
