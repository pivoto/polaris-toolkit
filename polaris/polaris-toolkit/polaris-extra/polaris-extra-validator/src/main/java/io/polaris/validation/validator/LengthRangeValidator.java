/*
 * Copyright (c) 2016-9-8 alex
 */

package io.polaris.validation.validator;

import io.polaris.validation.LengthRange;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Qt
 * @since 1.8
 */
public class LengthRangeValidator implements ConstraintValidator<LengthRange, String> {

	private int min;

	private int max;

	@Override
	public void initialize(LengthRange constraintAnnotation) {
		min = constraintAnnotation.min();
		max = constraintAnnotation.max();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value == null || (value.length() <= max && value.length() >= min);
	}
}
