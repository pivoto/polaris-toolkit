package io.polaris.validation.validator;


import java.math.BigDecimal;
import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.validation.DecimalScale;

/**
 * @author Qt
 * @since 1.8
 */
public class DecimalScaleCollectionValidator implements ConstraintValidator<DecimalScale, Collection<BigDecimal>> {

	private int max;
	private int min;

	@Override
	public void initialize(DecimalScale constraintAnnotation) {
		max = constraintAnnotation.max();
		min = constraintAnnotation.min();
	}

	@Override
	public boolean isValid(Collection<BigDecimal> values, ConstraintValidatorContext context) {
		if (values != null && !values.isEmpty()) {
			for (BigDecimal value : values) {
				if (value.scale() > this.max || value.scale() < this.min) {
					return false;
				}
			}
		}
		return true;
	}
}
