package io.polaris.validation;

import io.polaris.core.annotation.Experimental;
import io.polaris.core.err.ValidationException;
import io.polaris.core.lang.Types;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.Strings;
import org.hibernate.validator.internal.engine.MessageInterpolatorContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.messageinterpolation.AbstractMessageInterpolator;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

import javax.validation.*;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ValidateUnwrappedValue;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author Qt
 * @since 1.8
 */
public class Validations {

	private static final Pattern patternPlaceholder = Pattern.compile("\\{([^}]+)\\}");
	private static ResourceBundleLocator defaultResourceBundleLocator;
	private static ValidatorFactory defaultFactory;
	private static Validator defaultValidator;

	public static ResourceBundleLocator getDefaultResourceBundleLocator() {
		if (defaultResourceBundleLocator != null) {
			return defaultResourceBundleLocator;
		}
		return defaultResourceBundleLocator = new PlatformResourceBundleLocator(AbstractMessageInterpolator.USER_VALIDATION_MESSAGES,
			Thread.currentThread().getContextClassLoader(), true);
	}

	public static ValidatorFactory getDefaultFactory() {
		if (defaultFactory != null) {
			return defaultFactory;
		}
		MessageInterpolator messageInterpolator = new ResourceBundleMessageInterpolator(getDefaultResourceBundleLocator());
		return defaultFactory = Validation.byDefaultProvider()
			.configure()
			.messageInterpolator(messageInterpolator)
			.buildValidatorFactory();
	}


	public static Validator getDefaultValidator() {
		if (defaultValidator != null) {
			return defaultValidator;
		}
		return defaultValidator = getDefaultFactory().getValidator();
	}

	public static <A extends Annotation, T> List<Class<? extends ConstraintValidator<A, ?>>> getConstraintValidator(Class<A> annotationType, Class<T> type) {

		type = (Class<T>) Types.getWrapperClass(type);

		List<ConstraintValidatorDescriptor<A>> list =
			ConstraintHelper.forAllBuiltinConstraints().getAllValidatorDescriptors(annotationType);

		List<Class<? extends ConstraintValidator<A, ?>>> targets = new ArrayList<>();
		for (ConstraintValidatorDescriptor<A> descriptor : list) {
			Class<? extends ConstraintValidator<A, ?>> c = descriptor.getValidatorClass();
			Class target = Reflects.findActualTypeArgument(ConstraintValidator.class, c, 1);
			if (target.isAssignableFrom(type)) {
				targets.add(c);
			}
		}
		return targets;
	}

	public static <A extends Annotation, T> Class<? extends ConstraintValidator<A, ?>> getFirstConstraintValidator(Class<A> annotationType, Class<T> type) {

		type = (Class<T>) Types.getWrapperClass(type);

		List<ConstraintValidatorDescriptor<A>> list =
			ConstraintHelper.forAllBuiltinConstraints().getAllValidatorDescriptors(annotationType);

		Class<? extends ConstraintValidator<A, ?>> matched = null;
		List<Class<? extends ConstraintValidator<A, ?>>> targets = new ArrayList<>();
		for (ConstraintValidatorDescriptor<A> descriptor : list) {
			Class<? extends ConstraintValidator<A, ?>> c = descriptor.getValidatorClass();
			Class target = Reflects.findActualTypeArgument(ConstraintValidator.class, c, 1);
			if (target.equals(type)) {
				matched = c;
				break;
			} else if (target.isAssignableFrom(type)) {
				targets.add(c);
			}
		}
		if (matched != null) {
			return matched;
		}
		if (!targets.isEmpty()) {
			return targets.get(0);
		}
		return null;
	}


	public static String formatValidationMessage(String messageTemplate, ConstraintValidatorContext validatorContext,
		Object value, Consumer<Map<String, Object>> parameters) {
		if (validatorContext instanceof ConstraintValidatorContextImpl) {
			Map<String, Object> messageParameters = new HashMap<>();
			messageParameters.put("value", value);
			parameters.accept(messageParameters);
			ConstraintValidatorContextImpl context = (ConstraintValidatorContextImpl) validatorContext;
			ConstraintDescriptor<?> constraintDescriptor = context.getConstraintDescriptor();
			MessageInterpolatorContext msgContext = new MessageInterpolatorContext(
				constraintDescriptor,
				value,
				value.getClass(),
				PathImpl.createRootPath(),
				messageParameters,
				Collections.emptyMap(),
				ExpressionLanguageFeatureLevel.NONE,
				true
			);
			return getDefaultFactory().getMessageInterpolator().interpolate(messageTemplate, msgContext);
		}
		return null;
	}

	@Experimental
	public static String formatValidationMessage(String messageTemplate, Map<String, Object> messageParameters) {
		try {
			Object value = messageParameters.get("value");
			return getDefaultFactory().getMessageInterpolator().interpolate(messageTemplate, new MessageInterpolatorContext(
				new ConstraintDescriptor<Annotation>() {
					@Override
					public Annotation getAnnotation() {
						return null;
					}

					@Override
					public String getMessageTemplate() {
						return messageTemplate;
					}

					@Override
					public Set<Class<?>> getGroups() {
						return Collections.emptySet();
					}

					@Override
					public Set<Class<? extends Payload>> getPayload() {
						return Collections.emptySet();
					}

					@Override
					public ConstraintTarget getValidationAppliesTo() {
						return null;
					}

					@Override
					public List<Class<? extends ConstraintValidator<Annotation, ?>>> getConstraintValidatorClasses() {
						return Collections.emptyList();
					}

					@Override
					public Map<String, Object> getAttributes() {
						return messageParameters;
					}

					@Override
					public Set<ConstraintDescriptor<?>> getComposingConstraints() {
						return Collections.emptySet();
					}

					@Override
					public boolean isReportAsSingleViolation() {
						return false;
					}

					@Override
					public ValidateUnwrappedValue getValueUnwrapping() {
						return null;
					}

					@Override
					public <U> U unwrap(Class<U> type) {
						return null;
					}
				},
				value,
				value == null ? Object.class : value.getClass(),
				PathImpl.createRootPath(),
				messageParameters,
				Collections.emptyMap(),
				ExpressionLanguageFeatureLevel.NONE,
				true
			));
		} catch (Exception ignore) {
			if (Strings.isBlank(messageTemplate)) {
				return "";
			}
			ResourceBundle bundle = getDefaultResourceBundleLocator().getResourceBundle(Locale.getDefault());
			return Strings.resolvePlaceholders(messageTemplate, patternPlaceholder, key -> {
				String v = "";
				if (bundle.containsKey(key)) {
					v = bundle.getString(key);
				} else if (messageParameters.containsKey(key)) {
					v = Objects.toString(messageParameters.get(key), "");
				}
				return v;
			});
		}
	}

	public static <T> void validate(T bean, Class... groups) throws ValidationException {
		Set<ConstraintViolation<T>> violationSet = getDefaultValidator().validate(bean, groups);
		if (violationSet == null || violationSet.isEmpty()) {
			return;
		}
		throw new ValidationException(buildMessage(violationSet));
	}

	public static <T> void validate(T bean, Function<Set<ConstraintViolation<T>>, String> messageBuilder, Class... groups) throws ValidationException {
		Set<ConstraintViolation<T>> violationSet = getDefaultValidator().validate(bean, groups);
		if (violationSet == null || violationSet.isEmpty()) {
			return;
		}
		throw new ValidationException(messageBuilder.apply(violationSet));
	}

	public static <T> ValidationResult validateQuietly(T bean, Class... groups) {
		Set<ConstraintViolation<T>> violationSet = getDefaultValidator().validate(bean, groups);
		if (violationSet == null || violationSet.isEmpty()) {
			return ValidationResult.success();
		}
		String msg = buildMessage(violationSet);
		return ValidationResult.error(msg);
	}

	public static <T> ValidationResult validateQuietly(T bean, Function<Set<ConstraintViolation<T>>, String> messageBuilder, Class... groups) {
		Set<ConstraintViolation<T>> violationSet = getDefaultValidator().validate(bean, groups);
		if (violationSet == null || violationSet.isEmpty()) {
			return ValidationResult.success();
		}
		return ValidationResult.error(messageBuilder.apply(violationSet));
	}

	private static <T> String buildMessage(Set<ConstraintViolation<T>> violationSet) {
		StringBuilder sb = new StringBuilder();
		sb.append("参数规则验证失败！");
		for (ConstraintViolation<T> violation : violationSet) {
			String errorMsg = violation.getMessage();
			String path = violation.getPropertyPath().toString();
			sb.append("\n");
			if (Strings.isNotBlank(path)) {
				sb.append("属性[").append(path).append("]：");
			}
			sb.append(errorMsg).append("！");
		}
		String msg = sb.toString();
		return msg;
	}


	public static void isTrue(boolean condition, String message) throws ValidationException {
		if (!condition) {
			throw new ValidationException(message);
		}
	}

	public static void isFalse(boolean condition, String message) throws ValidationException {
		if (condition) {
			throw new ValidationException(message);
		}
	}

	public static void isNull(Object actual, String message) throws ValidationException {
		if (actual != null) {
			throw new ValidationException(message);
		}
	}

	public static void notNull(Object actual, String message) throws ValidationException {
		if (actual == null) {
			throw new ValidationException(message);
		}
	}

	public static void equals(char expected, char actual, String message) throws ValidationException {
		if (expected != actual) {
			throw new ValidationException(message);
		}
	}

	public static void equals(byte expected, byte actual, String message) throws ValidationException {
		if (expected != actual) {
			throw new ValidationException(message);
		}
	}

	public static void equals(short expected, short actual, String message) throws ValidationException {
		if (expected != actual) {
			throw new ValidationException(message);
		}
	}

	public static void equals(int expected, int actual, String message) throws ValidationException {
		if (expected != actual) {
			throw new ValidationException(message);
		}
	}

	public static void equals(long expected, long actual, String message) throws ValidationException {
		if (expected != actual) {
			throw new ValidationException(message);
		}
	}

	public static void equals(double expected, double actual, String message) throws ValidationException {
		if (expected != actual) {
			throw new ValidationException(message);
		}
	}

	public static void equals(float expected, float actual, String message) throws ValidationException {
		if (expected != actual) {
			throw new ValidationException(message);
		}
	}

	public static void equals(Object expected, Object actual, String message) throws ValidationException {
		if (!Objects.equals(expected, actual)) {
			throw new ValidationException(message);
		}
	}


	public static void notEquals(char expected, char actual, String message) throws ValidationException {
		if (expected == actual) {
			throw new ValidationException(message);
		}
	}

	public static void notEquals(byte expected, byte actual, String message) throws ValidationException {
		if (expected == actual) {
			throw new ValidationException(message);
		}
	}

	public static void notEquals(short expected, short actual, String message) throws ValidationException {
		if (expected == actual) {
			throw new ValidationException(message);
		}
	}

	public static void notEquals(int expected, int actual, String message) throws ValidationException {
		if (expected == actual) {
			throw new ValidationException(message);
		}
	}

	public static void notEquals(long expected, long actual, String message) throws ValidationException {
		if (expected == actual) {
			throw new ValidationException(message);
		}
	}

	public static void notEquals(double expected, double actual, String message) throws ValidationException {
		if (expected == actual) {
			throw new ValidationException(message);
		}
	}

	public static void notEquals(float expected, float actual, String message) throws ValidationException {
		if (expected == actual) {
			throw new ValidationException(message);
		}
	}


	public static void notEquals(Object expected, Object actual, String message) throws ValidationException {
		if (Objects.equals(expected, actual)) {
			throw new ValidationException(message);
		}
	}
	public static void isSame(Object expected, Object actual, String message) throws ValidationException {
		if (expected != actual) {
			throw new ValidationException(message);
		}
	}

	public static void notSame(Object expected, Object actual, String message) throws ValidationException {
		if (expected == actual) {
			throw new ValidationException(message);
		}
	}

	public static void isInstanceOf(Class<?> expectedType, Object actualValue, String message) throws ValidationException {
		if (!expectedType.isInstance(actualValue)) {
			throw new ValidationException(message);
		}
	}

	public static void notInstanceOf(Class<?> expectedType, Object actualValue, String message) throws ValidationException {
		if (expectedType.isInstance(actualValue)) {
			throw new ValidationException(message);
		}
	}

	public static void isAssignable(Class<?> expectedType, Class<?> actualValue, String message) throws ValidationException {
		if (actualValue == null || !expectedType.isAssignableFrom(actualValue)) {
			throw new ValidationException(message);
		}
	}

	public static void notAssignable(Class<?> expectedType, Class<?> actualValue, String message) throws ValidationException {
		if (actualValue != null && expectedType.isAssignableFrom(actualValue)) {
			throw new ValidationException(message);
		}
	}

	public static void isEmpty(CharSequence actual, String message) throws ValidationException {
		if (!Strings.isEmpty(actual)) {
			throw new ValidationException(message);
		}
	}

	public static void notEmpty(CharSequence actual, String message) throws ValidationException {
		if (Strings.isEmpty(actual)) {
			throw new ValidationException(message);
		}
	}

	public static void isBlank(CharSequence actual, String message) throws ValidationException {
		if (!Strings.isBlank(actual)) {
			throw new ValidationException(message);
		}
	}

	public static void notBlank(CharSequence actual, String message) throws ValidationException {
		if (Strings.isBlank(actual)) {
			throw new ValidationException(message);
		}
	}

	public static void equalsAny(CharSequence[] expected, CharSequence actual, String message) throws ValidationException {
		if (!Strings.equalsAny(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void notEqualsAny(CharSequence[] expected, CharSequence actual, String message) throws ValidationException {
		if (Strings.equalsAny(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void equalsAnyIgnoreCase(CharSequence[] expected, CharSequence actual, String message) throws ValidationException {
		if (!Strings.equalsAnyIgnoreCase(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void notEqualsAnyIgnoreCase(CharSequence expected, CharSequence actual, String message) throws ValidationException {
		if (Strings.equalsAnyIgnoreCase(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void containsAny(CharSequence[] expected, CharSequence actual, String message) throws ValidationException {
		if (!Strings.containsAny(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void notContainsAny(CharSequence[] expected, CharSequence actual, String message) throws ValidationException {
		if (Strings.containsAny(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void containsAnyIgnoreCase(CharSequence[] expected, CharSequence actual, String message) throws ValidationException {
		if (!Strings.containsAnyIgnoreCase(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void notContainsAnyIgnoreCase(CharSequence[] expected, CharSequence actual, String message) throws ValidationException {
		if (Strings.containsAnyIgnoreCase(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void startsWithAny(CharSequence[] expected, CharSequence actual, String message) throws ValidationException {
		if (!Strings.startsWithAny(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void notStartsWithAny(CharSequence[] expected, CharSequence actual, String message) throws ValidationException {
		if (Strings.startsWithAny(actual, expected)) {
			throw new ValidationException(message);
		}
	}
	public static void endsWithAny(CharSequence[] expected, CharSequence actual, String message) throws ValidationException {
		if (!Strings.endsWithAny(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void notEndsWithAny(CharSequence[] expected, CharSequence actual, String message) throws ValidationException {
		if (Strings.endsWithAny(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void startsWithAnyIgnoreCase(CharSequence[] expected, CharSequence actual, String message) throws ValidationException {
		if (!Strings.startsWithAnyIgnoreCase(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void notStartsWithAnyIgnoreCase(CharSequence[] expected, CharSequence actual, String message) throws ValidationException {
		if (Strings.startsWithAnyIgnoreCase(actual, expected)) {
			throw new ValidationException(message);
		}
	}
	public static void endsWithAnyIgnoreCase(CharSequence[] expected, CharSequence actual, String message) throws ValidationException {
		if (!Strings.endsWithAnyIgnoreCase(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void notEndsWithAnyIgnoreCase(CharSequence[] expected, CharSequence actual, String message) throws ValidationException {
		if (Strings.endsWithAnyIgnoreCase(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void equals(CharSequence expected, CharSequence actual, String message) throws ValidationException {
		if (!Strings.equals(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void notEquals(CharSequence expected, CharSequence actual, String message) throws ValidationException {
		if (Strings.equals(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void equalsIgnoreCase(CharSequence expected, CharSequence actual, String message) throws ValidationException {
		if (!Strings.equalsIgnoreCase(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void notEqualsIgnoreCase(CharSequence expected, CharSequence actual, String message) throws ValidationException {
		if (Strings.equalsIgnoreCase(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void contains(CharSequence expected, CharSequence actual, String message) throws ValidationException {
		if (!Strings.contains(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void notContains(CharSequence expected, CharSequence actual, String message) throws ValidationException {
		if (Strings.contains(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void containsIgnoreCase(CharSequence expected, CharSequence actual, String message) throws ValidationException {
		if (!Strings.containsIgnoreCase(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void notContainsIgnoreCase(CharSequence expected, CharSequence actual, String message) throws ValidationException {
		if (Strings.containsIgnoreCase(actual, expected)) {
			throw new ValidationException(message);
		}
	}


	public static void startsWith(CharSequence expected, CharSequence actual, String message) throws ValidationException {
		if (!Strings.startsWith(actual, expected)) {
			throw new ValidationException(message);
		}
	}


	public static void notStartsWith(CharSequence expected, CharSequence actual, String message) throws ValidationException {
		if (Strings.startsWith(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void endsWith(CharSequence expected, CharSequence actual, String message) throws ValidationException {
		if (!Strings.endsWith(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void notEndsWith(CharSequence expected, CharSequence actual, String message) throws ValidationException {
		if (Strings.endsWith(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void startsWithIgnoreCase(CharSequence expected, CharSequence actual, String message) throws ValidationException {
		if (!Strings.startsWithIgnoreCase(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void notStartsWithIgnoreCase(CharSequence expected, CharSequence actual, String message) throws ValidationException {
		if (Strings.startsWithIgnoreCase(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void endsWithIgnoreCase(CharSequence expected, CharSequence actual, String message) throws ValidationException {
		if (!Strings.endsWithIgnoreCase(actual, expected)) {
			throw new ValidationException(message);
		}
	}

	public static void notEndsWithIgnoreCase(CharSequence expected, CharSequence actual, String message) throws ValidationException {
		if (Strings.endsWithIgnoreCase(actual, expected)) {
			throw new ValidationException(message);
		}
	}

}
