package io.polaris.validation.validator;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.core.regex.Patterns;
import io.polaris.validation.Regexp;


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
		// pattern = Pattern.compile(this.value, flags);
		pattern = Patterns.getPattern(this.value, flags);
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		return pattern.matcher(value).find();
	}

}
