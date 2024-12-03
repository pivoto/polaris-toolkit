package io.polaris.validation.validator;


import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.validation.GreaterThan;

/**
 * @author Qt
 * @since 1.8
 */
public class GreaterThanCollectionValidator implements ConstraintValidator<GreaterThan, Collection<Number>> {

	private long value;

	@Override
	public void initialize(GreaterThan constraintAnnotation) {
		value = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(Collection<Number> value, ConstraintValidatorContext context) {
		if (value == null || value.isEmpty()) {
			return true;
		}

		for (Number i : value) {
			if (i != null && i.longValue() <= this.value) {
				return false;
			}
		}
		return true;
	}
}
