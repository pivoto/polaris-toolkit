package io.polaris.core.assertion;

import java.util.Objects;

/**
 * @author Qt
 * @since 1.8,  Jan 31, 2024
 */
public interface Arguments {

	public static void isTrue(boolean condition, String message) throws IllegalArgumentException {
		if (!condition) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isFalse(boolean condition, String message) throws IllegalArgumentException {
		if (condition) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isNull(Object actual, String message) throws IllegalArgumentException {
		if (actual != null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notNull(Object actual, String message) throws IllegalArgumentException {
		if (actual == null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isEquals(char expected, char actual, String message) throws IllegalArgumentException {
		if (expected != actual) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isEquals(byte expected, byte actual, String message) throws IllegalArgumentException {
		if (expected != actual) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isEquals(short expected, short actual, String message) throws IllegalArgumentException {
		if (expected != actual) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isEquals(int expected, int actual, String message) throws IllegalArgumentException {
		if (expected != actual) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isEquals(long expected, long actual, String message) throws IllegalArgumentException {
		if (expected != actual) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isEquals(double expected, double actual, String message) throws IllegalArgumentException {
		if (expected != actual) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isEquals(float expected, float actual, String message) throws IllegalArgumentException {
		if (expected != actual) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isEquals(Object expected, Object actual, String message) throws IllegalArgumentException {
		if (!Objects.equals(expected, actual)) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isSame(Object expected, Object actual, String message) throws IllegalArgumentException {
		if (expected != actual) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notEquals(char expected, char actual, String message) throws IllegalArgumentException {
		if (expected == actual) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notEquals(byte expected, byte actual, String message) throws IllegalArgumentException {
		if (expected == actual) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notEquals(short expected, short actual, String message) throws IllegalArgumentException {
		if (expected == actual) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notEquals(int expected, int actual, String message) throws IllegalArgumentException {
		if (expected == actual) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notEquals(long expected, long actual, String message) throws IllegalArgumentException {
		if (expected == actual) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notEquals(double expected, double actual, String message) throws IllegalArgumentException {
		if (expected == actual) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notEquals(float expected, float actual, String message) throws IllegalArgumentException {
		if (expected == actual) {
			throw new IllegalArgumentException(message);
		}
	}


	public static void notEquals(Object expected, Object actual, String message) throws IllegalArgumentException {
		if (Objects.equals(expected, actual)) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notSame(Object expected, Object actual, String message) throws IllegalArgumentException {
		if (expected == actual) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isInstanceOf(Class<?> expectedType, Object actualValue, String message) throws IllegalArgumentException {
		if (!expectedType.isInstance(actualValue)) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notInstanceOf(Class<?> expectedType, Object actualValue, String message) throws IllegalArgumentException {
		if (expectedType.isInstance(actualValue)) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isAssignable(Class<?> expectedType, Class<?> actualValue, String message) throws IllegalArgumentException {
		if (actualValue == null || !expectedType.isAssignableFrom(actualValue)) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notAssignable(Class<?> expectedType, Class<?> actualValue, String message) throws IllegalArgumentException {
		if (actualValue != null && expectedType.isAssignableFrom(actualValue)) {
			throw new IllegalArgumentException(message);
		}
	}
}
