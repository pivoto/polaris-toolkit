package io.polaris.validation.validator;

import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.validation.LengthMax;

/**
 * @author Qt
 * @since 1.8
 */
public class LengthMaxCollectionValidator implements ConstraintValidator<LengthMax, Collection<String>> {

	private int length;

	@Override
	public void initialize(LengthMax constraintAnnotation) {
		length = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(Collection<String> value, ConstraintValidatorContext context) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		for (String s : value) {
			if (s != null && s.length() > length) {
				return false;
			}
		}
		return true;
	}
}