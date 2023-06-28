package io.polaris.validation.validator;

import io.polaris.validation.CustomValidated;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.reflect.Constructor;

/**
 * @author Qt
 * @since 1.8
 */
public class CustomValidator implements ConstraintValidator<CustomValidated, Object> {

	private Class<? extends CustomValidation> clazz;
	private Class<? extends Payload>[] payload;
	private String[] arguments;

	@Override
	public void initialize(CustomValidated constraintAnnotation) {
		clazz = constraintAnnotation.value();
		payload = constraintAnnotation.payload();
		arguments = constraintAnnotation.arguments();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		CustomValidation customValidation = null;
		if (customValidation == null) {
			try {
				customValidation = clazz.newInstance();
			} catch (ReflectiveOperationException e) {
			}
		}
		if (customValidation == null) {
			try {
				Constructor<? extends CustomValidation> constructor = clazz.getConstructor(Class[].class);
				customValidation = constructor.newInstance(payload);
			} catch (ReflectiveOperationException e) {
			}
		}
		if (customValidation != null) {
			return customValidation.isValid(context, value, arguments);
		}
		return true;
	}
}
