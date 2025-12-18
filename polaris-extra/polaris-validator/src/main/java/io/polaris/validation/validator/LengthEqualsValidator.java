package io.polaris.validation.validator;

import io.polaris.validation.LengthEquals;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Qt
 */
public class LengthEqualsValidator implements ConstraintValidator<LengthEquals, String> {

	private int length;

	@Override
	public void initialize(LengthEquals constraintAnnotation) {
		length = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value == null || value.length() == length;
	}
}
