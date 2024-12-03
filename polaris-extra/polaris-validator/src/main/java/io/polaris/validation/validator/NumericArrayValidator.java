package io.polaris.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.validation.Numeric;

/**
 * @author Qt
 * @since 1.8
 */
public class NumericArrayValidator implements ConstraintValidator<Numeric, String[]> {

	@Override
	public void initialize(Numeric constraintAnnotation) {
	}

	@Override
	public boolean isValid(String[] value, ConstraintValidatorContext context) {
		if (value == null || value.length == 0) {
			return true;
		}
		for (String s : value) {
			final int len = s.length();
			for (int i = 0; i < len; i++) {
				if (!Character.isDigit(s.charAt(i))) {
					return false;
				}
			}
		}
		return true;
	}
}
