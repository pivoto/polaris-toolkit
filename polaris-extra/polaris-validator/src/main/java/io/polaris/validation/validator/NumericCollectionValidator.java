package io.polaris.validation.validator;

import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.validation.Numeric;

/**
 * @author Qt
 * @since 1.8
 */
public class NumericCollectionValidator implements ConstraintValidator<Numeric, Collection<String>> {

	@Override
	public void initialize(Numeric constraintAnnotation) {
	}

	@Override
	public boolean isValid(Collection<String> value, ConstraintValidatorContext context) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		for (String s : value) {
			final int len = s.length();
			for (int i = 0; i < len; i++) {
				if (!Character.isDigit(s.charAt(i))) {
					return false;
				}
			}
		}
		return true;
	}
}
