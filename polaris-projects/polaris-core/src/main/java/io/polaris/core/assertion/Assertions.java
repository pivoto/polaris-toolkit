package io.polaris.core.assertion;

import io.polaris.core.err.AssertionException;
import io.polaris.core.err.MultipleException;
import io.polaris.core.function.Executable;
import io.polaris.core.lang.Other;
import io.polaris.core.string.Strings;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Qt
 * @since 1.8
 */
public class Assertions {
	private static final String EXPECTED_TRUE = "expected: <true> but was: <false>";
	private static final String EXPECTED_FALSE = "expected: <false> but was: <true>";

	public static void fail(String message) throws AssertionException {
		throw new AssertionException(message);
	}

	public static void fail(String message, Throwable t) throws AssertionException {
		throw new AssertionException(message, t);
	}

	static String buildPrefix(String message) throws AssertionException {
		return (Strings.isNotBlank(message) ? message + " ==> " : "");
	}

	public static void assertTrue(boolean condition, String message) throws AssertionException {
		if (!condition) {
			throw new AssertionException(buildPrefix(message) + EXPECTED_TRUE, true, false);
		}
	}

	public static void assertFalse(boolean condition, String message) throws AssertionException {
		if (condition) {
			throw new AssertionException(buildPrefix(message) + EXPECTED_FALSE, false, true);
		}
	}

	public static void assertNull(Object actual, String message) throws AssertionException {
		if (actual != null) {
			throw new AssertionException(buildPrefix(message) + "expected: <null> but was: <" + actual + ">", null, actual);
		}
	}

	public static void assertNotNull(Object actual, String message) throws AssertionException {
		if (actual == null) {
			throw new AssertionException(buildPrefix(message) + "expected: not <null>", null, actual);
		}
	}

	public static void assertEquals(char expected, char actual, String message) throws AssertionException {
		if (expected != actual) {
			throw new AssertionException(format(expected, actual, message), expected, actual);
		}
	}

	public static void assertEquals(byte expected, byte actual, String message) throws AssertionException {
		if (expected != actual) {
			throw new AssertionException(format(expected, actual, message), expected, actual);
		}
	}

	public static void assertEquals(short expected, short actual, String message) throws AssertionException {
		if (expected != actual) {
			throw new AssertionException(format(expected, actual, message), expected, actual);
		}
	}

	public static void assertEquals(int expected, int actual, String message) throws AssertionException {
		if (expected != actual) {
			throw new AssertionException(format(expected, actual, message), expected, actual);
		}
	}

	public static void assertEquals(long expected, long actual, String message) throws AssertionException {
		if (expected != actual) {
			throw new AssertionException(format(expected, actual, message), expected, actual);
		}
	}

	public static void assertEquals(double expected, double actual, String message) throws AssertionException {
		if (expected != actual) {
			throw new AssertionException(format(expected, actual, message), expected, actual);
		}
	}

	public static void assertEquals(float expected, float actual, String message) throws AssertionException {
		if (expected != actual) {
			throw new AssertionException(format(expected, actual, message), expected, actual);
		}
	}

	public static void assertEquals(Object expected, Object actual, String message) throws AssertionException {
		if (!Objects.equals(expected, actual)) {
			throw new AssertionException(format(expected, actual, message), expected, actual);
		}
	}

	public static void assertSame(Object expected, Object actual, String message) throws AssertionException {
		if (expected != actual) {
			throw new AssertionException(format(expected, actual, message), expected, actual);
		}
	}

	public static void assertNotEquals(char expected, char actual, String message) throws AssertionException {
		if (expected == actual) {
			throw new AssertionException(buildPrefix(message) + "expected: not equal but was: <" + actual + ">", Other.of(actual), actual);
		}
	}

	public static void assertNotEquals(byte expected, byte actual, String message) throws AssertionException {
		if (expected == actual) {
			throw new AssertionException(buildPrefix(message) + "expected: not equal but was: <" + actual + ">", Other.of(actual), actual);
		}
	}

	public static void assertNotEquals(short expected, short actual, String message) throws AssertionException {
		if (expected == actual) {
			throw new AssertionException(buildPrefix(message) + "expected: not equal but was: <" + actual + ">", Other.of(actual), actual);
		}
	}

	public static void assertNotEquals(int expected, int actual, String message) throws AssertionException {
		if (expected == actual) {
			throw new AssertionException(buildPrefix(message) + "expected: not equal but was: <" + actual + ">", Other.of(actual), actual);
		}
	}

	public static void assertNotEquals(long expected, long actual, String message) throws AssertionException {
		if (expected == actual) {
			throw new AssertionException(buildPrefix(message) + "expected: not equal but was: <" + actual + ">", Other.of(actual), actual);
		}
	}

	public static void assertNotEquals(double expected, double actual, String message) throws AssertionException {
		if (expected == actual) {
			throw new AssertionException(buildPrefix(message) + "expected: not equal but was: <" + actual + ">", Other.of(actual), actual);
		}
	}

	public static void assertNotEquals(float expected, float actual, String message) throws AssertionException {
		if (expected == actual) {
			throw new AssertionException(buildPrefix(message) + "expected: not equal but was: <" + actual + ">", Other.of(actual), actual);
		}
	}


	public static void assertNotEquals(Object expected, Object actual, String message) throws AssertionException {
		if (Objects.equals(expected, actual)) {
			throw new AssertionException(buildPrefix(message) + "expected: not equal but was: <" + actual + ">", Other.of(actual), actual);
		}
	}

	public static void assertNotSame(Object expected, Object actual, String message) throws AssertionException {
		if (expected == actual) {
			throw new AssertionException(buildPrefix(message) + "expected: not same but was: <" + actual + ">", Other.of(actual), actual);
		}
	}

	static String format(Object expected, Object actual, String message) {
		return buildPrefix(message) + formatValues(expected, actual);
	}

	static String formatValues(Object expected, Object actual) {
		String expectedString = toString(expected);
		String actualString = toString(actual);
		if (expectedString.equals(actualString)) {
			return String.format("expected: %s but was: %s", formatClassAndValue(expected, expectedString),
				formatClassAndValue(actual, actualString));
		}
		return String.format("expected: <%s> but was: <%s>", expectedString, actualString);
	}

	private static String formatClassAndValue(Object value, String valueString) {
		// If the value is null, return <null> instead of null<null>.
		if (value == null) {
			return "<null>";
		}
		String hash = Integer.toHexString(System.identityHashCode(value));
		return (value instanceof Class ?
			"<" + ((Class<?>) value).getCanonicalName() + "@" + hash + ">"
			: value.getClass().getCanonicalName() + "@" + hash + "<" + valueString + ">");
	}

	private static String toString(Object obj) {
		if (obj == null) {
			return "null";
		}
		if (obj instanceof Class) {
			return ((Class<?>) obj).getCanonicalName();
		}
		if (obj.getClass().isArray()) {
			if (obj.getClass().getComponentType().isPrimitive()) {
				if (obj instanceof boolean[]) {
					return Arrays.toString((boolean[]) obj);
				}
				if (obj instanceof char[]) {
					return Arrays.toString((char[]) obj);
				}
				if (obj instanceof short[]) {
					return Arrays.toString((short[]) obj);
				}
				if (obj instanceof byte[]) {
					return Arrays.toString((byte[]) obj);
				}
				if (obj instanceof int[]) {
					return Arrays.toString((int[]) obj);
				}
				if (obj instanceof long[]) {
					return Arrays.toString((long[]) obj);
				}
				if (obj instanceof float[]) {
					return Arrays.toString((float[]) obj);
				}
				if (obj instanceof double[]) {
					return Arrays.toString((double[]) obj);
				}
			}
			return Arrays.deepToString((Object[]) obj);
		}
		return String.valueOf(obj);
	}

	public static void assertAll(String message, Executable... executables) throws MultipleException {
		assertAll(message, Arrays.stream(executables));
	}

	public static void assertAll(String message, Collection<Executable> executables) throws MultipleException {
		assertAll(message, executables.stream());
	}

	public static void assertAll(String message, Stream<Executable> executables) throws MultipleException {
		List<Throwable> failures = executables.map(executable -> {
				try {
					executable.execute();
					return null;
				} catch (Throwable e) {
					return e;
				}
			}).filter(Objects::nonNull)
			.collect(Collectors.toList());

		if (!failures.isEmpty()) {
			MultipleException err = new MultipleException(message, failures);
			failures.forEach(err::addSuppressed);
			throw err;
		}
	}


	public static <T extends Throwable> T assertThrowsExactly(Class<T> expected, Executable executable, String message) throws AssertionException {
		try {
			executable.execute();
		} catch (Throwable actual) {
			if (expected.equals(actual.getClass())) {
				return (T) actual;
			}
			throw new AssertionException(buildPrefix(message)
				+ format(expected, actual.getClass(), "Unexpected exception type thrown"), actual);
		}
		throw new AssertionException(buildPrefix(message)
			+ String.format("Expected %s to be thrown, but nothing was thrown.", expected.getCanonicalName()));
	}

	public static <T extends Throwable> T assertThrows(Class<T> expected, Executable executable, String message) throws AssertionException {
		try {
			executable.execute();
		} catch (Throwable actual) {
			if (expected.isInstance(actual)) {
				return (T) actual;
			}
			throw new AssertionException(buildPrefix(message)
				+ format(expected, actual.getClass(), "Unexpected exception type thrown"), actual);
		}
		throw new AssertionException(buildPrefix(message)
			+ String.format("Expected %s to be thrown, but nothing was thrown.", expected.getCanonicalName()));
	}

	public static <T extends Throwable> void assertNotThrowsExactly(Class<T> expected, Executable executable, String message) throws AssertionException {
		try {
			executable.execute();
		} catch (Throwable actual) {
			if (expected.equals(actual.getClass())) {
				throw new AssertionException(buildPrefix(message)
					+ String.format("expected: not <%s> to be thrown, but <%s> was thrown.", expected.getCanonicalName(), actual.getClass().getCanonicalName())
					, actual, Other.of(expected), actual);
			}
		}
	}

	public static <T extends Throwable> void assertNotThrows(Class<T> expected, Executable executable, String message) throws AssertionException {
		try {
			executable.execute();
		} catch (Throwable actual) {
			if (expected.isInstance(actual)) {
				throw new AssertionException(buildPrefix(message)
					+ String.format("expected: not <%s> to be thrown, but <%s> was thrown.", expected.getCanonicalName(), actual.getClass().getCanonicalName())
					, actual, Other.of(expected), actual);
			}
		}
	}

	public static void assertSomethingThrows(Executable executable, String message) throws AssertionException {
		try {
			executable.execute();
			throw new AssertionException(buildPrefix(message) + "Expected something to be thrown, but nothing was thrown.", Throwable.class, null);
		} catch (Throwable actual) {
		}
	}

	public static void assertNothingThrows(Executable executable, String message) throws AssertionException {
		try {
			executable.execute();
		} catch (Throwable actual) {
			throw new AssertionException(buildPrefix(message)
				+ String.format("Expected nothing to be thrown, but <%s> was thrown.", actual.getClass().getCanonicalName()), actual, null, actual);
		}
	}

	public static void assertInstanceOf(Class<?> expectedType, Object actualValue, String message) throws AssertionException {
		if (!expectedType.isInstance(actualValue)) {
			String reason = (actualValue == null ? "Unexpected null value" : "Unexpected type");
			throw new AssertionException(buildPrefix(message)
				+ format(expectedType, actualValue == null ? null : actualValue.getClass(), reason),
				expectedType, actualValue == null ? null : actualValue.getClass());
		}
	}

	public static void assertNotInstanceOf(Class<?> expectedType, Object actualValue, String message) throws AssertionException {
		if (expectedType.isInstance(actualValue)) {
			throw new AssertionException(buildPrefix(message)
				+ format(Other.of(expectedType), actualValue.getClass(), "Unexpected type"),
				Other.of(expectedType), actualValue.getClass());
		}
	}

	public static void assertAssignable(Class<?> expectedType, Class<?> actualValue, String message) throws AssertionException {
		if (actualValue == null || !expectedType.isAssignableFrom(actualValue)) {
			String reason = (actualValue == null ? "Unexpected null value" : "Unexpected type");
			throw new AssertionException(buildPrefix(message)
				+ format(expectedType, actualValue == null ? null : actualValue, reason),
				expectedType, actualValue == null ? null : actualValue);
		}
	}

	public static void assertNotAssignable(Class<?> expectedType, Class<?> actualValue, String message) throws AssertionException {
		if (actualValue != null && expectedType.isAssignableFrom(actualValue)) {
			throw new AssertionException(buildPrefix(message)
				+ format(Other.of(expectedType), actualValue, "Unexpected type"),
				Other.of(expectedType), actualValue);
		}
	}


}
