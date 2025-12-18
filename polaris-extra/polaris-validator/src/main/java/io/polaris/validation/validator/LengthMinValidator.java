package io.polaris.validation.validator;

import io.polaris.validation.LengthMin;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Qt
 */
public class LengthMinValidator implements ConstraintValidator<LengthMin, String> {

	private int length;

	@Override
	public void initialize(LengthMin constraintAnnotation) {
		length = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value == null || value.length() >= length;
	}
}
