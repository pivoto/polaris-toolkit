package io.polaris.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.validation.LengthMin;

/**
 * @author Qt
 * @since 1.8
 */
public class LengthMinArrayValidator implements ConstraintValidator<LengthMin, String[]> {

	private int length;

	@Override
	public void initialize(LengthMin constraintAnnotation) {
		length = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(String[] value, ConstraintValidatorContext context) {
		if (value == null || value.length == 0) {
			return true;
		}
		for (String s : value) {
			if (s != null && s.length() < length) {
				return false;
			}
		}
		return true;
	}
}
