package io.polaris.validation.validator;

import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.validation.LengthRange;

/**
 * @author Qt
 * @since 1.8
 */
public class LengthRangeCollectionValidator implements ConstraintValidator<LengthRange, Collection<String>> {

	private int min;

	private int max;

	@Override
	public void initialize(LengthRange constraintAnnotation) {
		min = constraintAnnotation.min();
		max = constraintAnnotation.max();
	}

	@Override
	public boolean isValid(Collection<String> value, ConstraintValidatorContext context) {
		if (value == null || value.size() == 0) {
			return true;
		}
		for (String s : value) {
			if (s != null && (s.length() > max || s.length() < min)) {
				return false;
			}
		}
		return true;
	}
}
