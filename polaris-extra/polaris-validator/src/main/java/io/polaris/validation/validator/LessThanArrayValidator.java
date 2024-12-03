package io.polaris.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.validation.LessThan;

/**
 * @author Qt
 * @since 1.8
 */
public class LessThanArrayValidator implements ConstraintValidator<LessThan, Number[]> {

	private long value;

	@Override
	public void initialize(LessThan constraintAnnotation) {
		value = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(Number[] value, ConstraintValidatorContext context) {
		if (value == null || value.length == 0) {
			return true;
		}

		for (Number i : value) {
			if (i != null && i.longValue() >= this.value) {
				return false;
			}
		}
		return true;
	}
}
