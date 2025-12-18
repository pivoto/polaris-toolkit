package io.polaris.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.validation.LessThan;

/**
 * @author Qt
 */
public class LessThanValidator implements ConstraintValidator<LessThan, Number> {

	private long value;

	@Override
	public void initialize(LessThan constraintAnnotation) {
		value = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(Number value, ConstraintValidatorContext context) {
		return value == null || value.longValue() < this.value;
	}
}
