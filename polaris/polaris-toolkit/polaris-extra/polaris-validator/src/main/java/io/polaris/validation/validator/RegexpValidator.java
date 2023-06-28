/*
 * Copyright (c) 2016-9-7 alex
 */

package io.polaris.validation.validator;

import io.polaris.validation.Regexp;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;


/**
 * @author Qt
 * @since 1.8
 */
public class RegexpValidator implements ConstraintValidator<Regexp, String> {

	private String value;
	private int flags;
	private Pattern pattern;

	@Override
	public void initialize(Regexp constraintAnnotation) {
		value = constraintAnnotation.value();
		flags = constraintAnnotation.flags();
		pattern = Pattern.compile(this.value, flags);
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		return pattern.matcher(value).find();
	}

}
