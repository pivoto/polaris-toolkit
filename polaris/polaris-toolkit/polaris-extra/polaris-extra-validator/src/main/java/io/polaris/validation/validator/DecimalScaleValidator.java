/*
 * Copyright (c) 2016-8-31
 * created by alex
 */

package io.polaris.validation.validator;


import io.polaris.validation.DecimalScale;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

/**
 * @author Qt
 * @since 1.8
 */
public class DecimalScaleValidator implements ConstraintValidator<DecimalScale, BigDecimal> {

	private int max;
	private int min;

	@Override
	public void initialize(DecimalScale constraintAnnotation) {
		max = constraintAnnotation.max();
		min = constraintAnnotation.min();
	}

	@Override
	public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
		if (value != null) {
			/*if (context instanceof HibernateConstraintValidatorContext) {
				context.unwrap(HibernateConstraintValidatorContext.class)
					.addMessageParameter("max", max)
					.addMessageParameter("min", min)
				;
			}*/
			return value.scale() <= this.max && value.scale() >= this.min;
		}
		return true;
	}
}
