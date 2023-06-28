/*
 * Copyright (c) 2016-8-31
 * created by alex
 */

package io.polaris.validation.validator;

import io.polaris.validation.Numeric;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Qt
 * @since 1.8
 */
public class NumericValidator implements ConstraintValidator<Numeric, String> {

	@Override
	public void initialize(Numeric constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.length() == 0) {
			return true;
		}
		final int sz = value.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isDigit(value.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}
}
