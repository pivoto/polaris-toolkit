package io.polaris.core.assertion;

import java.util.Objects;

import io.polaris.core.err.ValidationException;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since  Jan 31, 2024
 */
public interface Validations {

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
