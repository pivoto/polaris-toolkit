package io.polaris.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.validation.LengthEquals;

/**
 * @author Qt
 * @since 1.8
 */
public class LengthEqualsArrayValidator implements ConstraintValidator<LengthEquals, String[]> {

	private int length;

	@Override
	public void initialize(LengthEquals constraintAnnotation) {
		length = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(String[] value, ConstraintValidatorContext context) {
		if (value == null || value.length == 0) {
			return true;
		}
		for (String s : value) {
			if (s != null && s.length() != length) {
				return false;
			}
		}
		return true;
	}
}
