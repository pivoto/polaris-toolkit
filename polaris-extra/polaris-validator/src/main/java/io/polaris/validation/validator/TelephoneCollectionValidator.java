package io.polaris.validation.validator;

import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.validation.Telephone;

/**
 * @author Qt
 * @since Dec 03, 2024
 */
public class TelephoneCollectionValidator implements ConstraintValidator<Telephone, Collection<String>> {

	@Override
	public void initialize(Telephone constraintAnnotation) {
	}

	@Override
	public boolean isValid(Collection<String> value, ConstraintValidatorContext context) {
		// 为空时，默认不校验，即认为通过
		if (value == null) {
			return true;
		}
		if (value.isEmpty()) {
			return true;
		}
		for (String s : value) {
			if (!TelephoneValidator.isValid(s)) {
				return false;
			}
		}
		return true;
	}

}
