package io.polaris.validation;

import io.polaris.commons.err.ValidationException;
import io.polaris.commons.lang.Types;
import io.polaris.commons.reflect.Reflects;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.messageinterpolation.AbstractMessageInterpolator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.junit.platform.commons.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Qt
 * @since 1.8
 */
public class Validations {

	private static ValidatorFactory defaultFactory;
	private static Validator defaultValidator;

	public static ValidatorFactory getDefaultFactory() {
		if (defaultFactory != null) {
			return defaultFactory;
		}
		return defaultFactory = Validation.byDefaultProvider()
			.configure()
			.messageInterpolator(
				new ResourceBundleMessageInterpolator(
					new PlatformResourceBundleLocator(AbstractMessageInterpolator.USER_VALIDATION_MESSAGES,
						Thread.currentThread().getContextClassLoader(), true)
				)
			)
			.buildValidatorFactory();
	}

	public static Validator getDefaultValidator() {
		if (defaultValidator != null) {
			return defaultValidator;
		}
		return defaultValidator = getDefaultFactory().getValidator();
	}

	public static <A extends Annotation, T> List<Class<? extends ConstraintValidator<A, ?>>> getConstraintValidator(
		Class<A> annotationType, Class<T> type) {
		List<ConstraintValidatorDescriptor<A>> list =
			ConstraintHelper.forAllBuiltinConstraints().getAllValidatorDescriptors(annotationType);
		List<Class<? extends ConstraintValidator<A, ?>>> targets = new ArrayList<>();
		type = Types.getWrapperClass(type);
		for (ConstraintValidatorDescriptor<A> descriptor : list) {
			Class<? extends ConstraintValidator<A, ?>> c = descriptor.getValidatorClass();
			Class target = Reflects.findParameterizedType(ConstraintValidator.class, c, 1);
			if (target.isAssignableFrom(type)) {
				targets.add(c);
			}
		}
		return targets;
	}

	public static void validate(Object bean, Class... groups) throws ValidationException {
		Validator validator = getDefaultValidator();
		Set<ConstraintViolation<Object>> violationSet = validator.validate(bean, groups);
		raiseWhenError(violationSet);
	}

	private static <T> void raiseWhenError(Set<ConstraintViolation<T>> violationSet) throws ValidationException {
		if (violationSet == null || violationSet.isEmpty()) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("规则验证失败！");
		for (ConstraintViolation<T> violation : violationSet) {
			String errorMsg = violation.getMessage();
			String path = violation.getPropertyPath().toString();
			sb.append("\n");
			if (StringUtils.isNotBlank(path)) {
				sb.append("字段[").append(path).append("]有误：");
			}
			sb.append(errorMsg).append("！");
		}
		throw new ValidationException(sb.toString());
	}
}
