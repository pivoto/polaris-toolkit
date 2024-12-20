package io.polaris.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.validation.LessThan;

/**
 * @author Qt
 * @since 1.8
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
