package io.polaris.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.validation.Numeric;

/**
 * @author Qt
 * @since 1.8
 */
public class NumericValidator implements ConstraintValidator<Numeric, String> {

	@Override
	public void initialize(Numeric constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		final int sz = value.length();
		for (int i = 0; i < sz; i++) {
			if (!Character.isDigit(value.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
