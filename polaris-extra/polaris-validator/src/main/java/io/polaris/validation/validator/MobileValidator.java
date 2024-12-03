package io.polaris.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.core.regex.PatternConsts;
import io.polaris.core.regex.Patterns;
import io.polaris.validation.Mobile;

/**
 * @author Qt
 * @since Dec 03, 2024
 */
public class MobileValidator implements ConstraintValidator<Mobile, String> {


	@Override
	public void initialize(Mobile constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// 为空时，默认不校验，即认为通过
		if (value == null) {
			return true;
		}
		return isValid(value);
	}

	public static boolean isValid(String value) {
		// 为空时，默认不校验，即认为通过
		if (value == null) {
			return true;
		}
		// 校验手机
		return Patterns.matches(PatternConsts.MOBILE_WHOLE, value);
	}

}
