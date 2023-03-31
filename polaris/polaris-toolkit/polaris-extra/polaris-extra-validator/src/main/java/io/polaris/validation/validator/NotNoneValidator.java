/*
 * Copyright (c) 2016-8-31
 * created by alex
 */

package io.polaris.validation.validator;

import io.polaris.core.consts.StdConsts;
import io.polaris.validation.NotNone;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Qt
 * @since 1.8
 */
public class NotNoneValidator implements ConstraintValidator<NotNone, String> {

	@Override
	public void initialize(NotNone constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isEmpty()) {
			return false;
		}
		value = value.trim();
		if (value.isEmpty()) {
			return false;
		}
		if (StdConsts.NULL.equals(value)) {
			return false;
		}
		return true;
	}
}
