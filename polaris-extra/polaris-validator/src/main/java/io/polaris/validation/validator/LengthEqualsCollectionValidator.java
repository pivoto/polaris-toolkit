package io.polaris.validation.validator;

import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.validation.LengthEquals;

/**
 * @author Qt
 * @since 1.8
 */
public class LengthEqualsCollectionValidator implements ConstraintValidator<LengthEquals, Collection<String>> {

	private int length;

	@Override
	public void initialize(LengthEquals constraintAnnotation) {
		length = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(Collection<String> value, ConstraintValidatorContext context) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		for (String s : value) {
			if (s != null && s.length() != length) {
				return false;
			}
		}
		return true;
	}
}
