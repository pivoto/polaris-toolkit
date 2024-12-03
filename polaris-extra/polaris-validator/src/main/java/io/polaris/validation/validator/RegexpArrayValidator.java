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
public class RegexpArrayValidator implements ConstraintValidator<Regexp, String[]> {

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
	public boolean isValid(String[] value, ConstraintValidatorContext context) {
		if (value == null || value.length == 0) {
			return true;
		}
		for (String s : value) {
			if (!pattern.matcher(s).find()) {
				return false;
			}
		}
		return true;
	}

}
