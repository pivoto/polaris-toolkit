package io.polaris.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.core.regex.PatternConsts;
import io.polaris.core.regex.Patterns;
import io.polaris.validation.Telephone;

/**
 * @author Qt
 * @since Dec 03, 2024
 */
public class TelephoneValidator implements ConstraintValidator<Telephone, String> {


	@Override
	public void initialize(Telephone constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return isValid(value);
	}


	public static boolean isValid(String value) {
		// 为空时，默认不校验，即认为通过
		if (value == null) {
			return true;
		}
		if (Patterns.matches(PatternConsts.MOBILE_WHOLE, value)) {
			return true;
		}
		if (Patterns.matches(PatternConsts.MOBILE_HK_WHOLE, value)) {
			return true;
		}
		if (Patterns.matches(PatternConsts.MOBILE_TW_WHOLE, value)) {
			return true;
		}
		if (Patterns.matches(PatternConsts.MOBILE_MO_WHOLE, value)) {
			return true;
		}
		if (Patterns.matches(PatternConsts.TEL_WHOLE, value)) {
			return true;
		}
		if (Patterns.matches(PatternConsts.TEL_400_800_WHOLE, value)) {
			return true;
		}
		return false;
	}
}
