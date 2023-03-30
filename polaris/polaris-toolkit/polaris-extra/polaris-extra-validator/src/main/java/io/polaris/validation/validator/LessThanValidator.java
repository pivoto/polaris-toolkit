/*
 * Copyright (c) 2016-8-31
 * created by alex
 */

package io.polaris.validation.validator;

import io.polaris.validation.LessThan;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Qt
 * @since 1.8
 */
public class LessThanValidator implements ConstraintValidator<LessThan, Integer> {

	private Integer value;

	@Override
	public void initialize(LessThan constraintAnnotation) {
		value = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		return value == null || value < this.value;
	}
}
