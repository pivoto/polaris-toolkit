package io.polaris.validation.validator;


import io.polaris.validation.GreaterThan;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Qt
 * @since 1.8
 */
public class GreaterThanValidator implements ConstraintValidator<GreaterThan, Number> {

	private Long value;

	@Override
	public void initialize(GreaterThan constraintAnnotation) {
		value = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(Number value, ConstraintValidatorContext context) {
		return value == null || value.longValue() > this.value;
	}
}
