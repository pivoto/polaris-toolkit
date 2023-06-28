/*
 * Copyright (c) 2016-8-31
 * created by alex
 */

package io.polaris.validation.validator;

import io.polaris.validation.LengthEquals;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Qt
 * @since 1.8
 */
public class LengthEqualsValidator implements ConstraintValidator<LengthEquals, String> {

	private int length;

	@Override
	public void initialize(LengthEquals constraintAnnotation) {
		length = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value == null || value.length() == length;
	}
}
