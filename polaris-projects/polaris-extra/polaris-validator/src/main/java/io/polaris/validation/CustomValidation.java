package io.polaris.validation;

import javax.validation.ConstraintValidatorContext;

/**
 * @author Qt
 * @since 1.8
 */
public interface CustomValidation {
	/**
	 * 验证
	 *
	 * @param context
	 * @param value
	 * @param arguments
	 * @return
	 */
	boolean isValid(ConstraintValidatorContext context, Object value, String... arguments);
}
