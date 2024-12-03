package io.polaris.validation.validator;

import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.core.consts.StdConsts;
import io.polaris.validation.NotNone;

/**
 * @author Qt
 * @since 1.8
 */
public class NotNoneCollectionValidator implements ConstraintValidator<NotNone, Collection<String>> {

	@Override
	public void initialize(NotNone constraintAnnotation) {
	}

	@Override
	public boolean isValid(Collection<String> value, ConstraintValidatorContext context) {
		if (value == null || value.isEmpty()) {
			return false;
		}
		for (String s : value) {
			s = s.trim();
			if (s.isEmpty()) {
				return false;
			}
			if (StdConsts.NULL.equals(s)) {
				return false;
			}
		}
		return true;
	}
}
