package io.polaris.validation.validator;

import java.util.Collection;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.core.regex.Patterns;
import io.polaris.validation.Identifier;


/**
 * @author Qt
 * @since 1.8
 */
public class IdentifierCollectionValidator implements ConstraintValidator<Identifier, Collection<String>> {

	private Pattern pattern;

	@Override
	public void initialize(Identifier constraintAnnotation) {
		pattern = Patterns.getPattern(constraintAnnotation.regexp(), constraintAnnotation.flags());
	}

	@Override
	public boolean isValid(Collection<String> value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		if (value.isEmpty()) {
			return true;
		}
		for (String s : value) {
			if (!pattern.matcher(s).matches()) {
				return false;
			}
		}
		return true;
	}

}
