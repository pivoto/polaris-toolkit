package io.polaris.validation.validator;


import java.math.BigDecimal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.validation.DecimalScale;

/**
 * @author Qt
 * @since 1.8
 */
public class DecimalScaleArrayValidator implements ConstraintValidator<DecimalScale, BigDecimal[]> {

	private int max;
	private int min;

	@Override
	public void initialize(DecimalScale constraintAnnotation) {
		max = constraintAnnotation.max();
		min = constraintAnnotation.min();
	}

	@Override
	public boolean isValid(BigDecimal[] values, ConstraintValidatorContext context) {
		if (values != null) {
			for (BigDecimal value : values) {
				if (value.scale() > this.max || value.scale() < this.min) {
					return false;
				}
			}
		}
		return true;
	}
}
