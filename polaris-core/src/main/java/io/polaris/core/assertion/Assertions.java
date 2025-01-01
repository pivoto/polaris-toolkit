package io.polaris.core.assertion;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.polaris.core.err.MultipleException;
import io.polaris.core.function.Executable;
import io.polaris.core.lang.Other;
import io.polaris.core.string.Strings;

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

	public static void expect(Supplier<Boolean> condition, String message) throws IllegalArgumentException {
		if (!condition.get()) {
			throw new AssertionException(message);
		}
	}

	public static void expect(boolean condition, String message) throws IllegalArgumentException {
		if (!condition) {
			throw new AssertionException(message);
		}
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


	public static void assertSame(Object expected, Object actual, String message) throws AssertionException {
		if (expected != actual) {
			throw new AssertionException(format(expected, actual, message), expected, actual);
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

	public static void assertEmpty(CharSequence actual, String message) throws AssertionException {
		if (!Strings.isEmpty(actual)) {
			throw new AssertionException(buildPrefix(message) + "expected: <empty> but was: <" + actual + ">", null, actual);
		}
	}

	public static void assertNotEmpty(CharSequence actual, String message) throws AssertionException {
		if (Strings.isEmpty(actual)) {
			throw new AssertionException(buildPrefix(message) + "expected: not <empty> but was: <" + actual + ">", null, actual);
		}
	}

	public static void assertBlank(CharSequence actual, String message) throws AssertionException {
		if (!Strings.isBlank(actual)) {
			throw new AssertionException(buildPrefix(message) + "expected: <blank> but was: <" + actual + ">", null, actual);
		}
	}

	public static void assertNotBlank(CharSequence actual, String message) throws AssertionException {
		if (Strings.isBlank(actual)) {
			throw new AssertionException(buildPrefix(message) + "expected: not <blank> but was: <" + actual + ">", null, actual);
		}
	}

	public static void assertEqualsAny(CharSequence[] expected, CharSequence actual, String message) throws AssertionException {
		if (!Strings.equalsAny(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: equals any of <" + Arrays.toString(expected) + "> but was: <" + actual + ">", null, actual);
		}
	}

	public static void assertNotEqualsAny(CharSequence[] expected, CharSequence actual, String message) throws AssertionException {
		if (Strings.equalsAny(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: not equals any of <" + Arrays.toString(expected) + "> but was: <" + actual + ">", null, actual);
		}
	}

	public static void assertEqualsAnyIgnoreCase(CharSequence[] expected, CharSequence actual, String message) throws AssertionException {
		if (!Strings.equalsAnyIgnoreCase(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: equals ignore case any of <" + Arrays.toString(expected) + "> but was: <" + actual + ">", null, actual);
		}
	}

	public static void assertNotEqualsAnyIgnoreCase(CharSequence[] expected, CharSequence actual, String message) throws AssertionException {
		if (Strings.equalsAnyIgnoreCase(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: not equals ignore case any of <" + Arrays.toString(expected) + "> but was: <" + actual + ">", null, actual);
		}
	}

	public static void assertContainsAny(CharSequence[] expected, CharSequence actual, String message) throws AssertionException {
		if (!Strings.containsAny(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: contains any of <" + Arrays.toString(expected) + "> but was: <" + actual + ">", null, actual);
		}
	}

	public static void assertNotContainsAny(CharSequence[] expected, CharSequence actual, String message) throws AssertionException {
		if (Strings.containsAny(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: not contains any of <" + Arrays.toString(expected) + "> but was: <" + actual + ">", null, actual);
		}
	}


	public static void assertContainsAnyIgnoreCase(CharSequence[] expected, CharSequence actual, String message) throws AssertionException {
		if (!Strings.containsAnyIgnoreCase(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: contains ignore case any of <" + Arrays.toString(expected) + "> but was: <" + actual + ">", null, actual);
		}
	}

	public static void assertNotContainsAnyIgnoreCase(CharSequence[] expected, CharSequence actual, String message) throws AssertionException {
		if (Strings.containsAnyIgnoreCase(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: not contains ignore case any of <" + Arrays.toString(expected) + "> but was: <" + actual + ">", null, actual);
		}
	}

	public static void assertStartsWithAny(CharSequence[] expected, CharSequence actual, String message) throws AssertionException {
		if (!Strings.startsWithAny(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: starts with any of <" + Arrays.toString(expected) + "> but was: <" + actual + ">", null, actual);
		}
	}

	public static void assertNotStartsWithAny(CharSequence[] expected, CharSequence actual, String message) throws AssertionException {
		if (Strings.startsWithAny(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: not starts with any of <" + Arrays.toString(expected) + "> but was: <" + actual + ">", null, actual);
		}
	}

	public static void assertEndsWithAny(CharSequence[] expected, CharSequence actual, String message) throws AssertionException {
		if (!Strings.endsWithAny(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: ends with any of <" + Arrays.toString(expected) + "> but was: <" + actual + ">", null, actual);
		}
	}

	public static void assertNotEndsWithAny(CharSequence[] expected, CharSequence actual, String message) throws AssertionException {
		if (Strings.endsWithAny(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: not ends with any of <" + Arrays.toString(expected) + "> but was: <" + actual + ">", null, actual);
		}
	}


	public static void assertStartsWithAnyIgnoreCase(CharSequence[] expected, CharSequence actual, String message) throws AssertionException {
		if (!Strings.startsWithAnyIgnoreCase(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: starts with ignore case any of <" + Arrays.toString(expected) + "> but was: <" + actual + ">", null, actual);
		}
	}

	public static void assertNotStartsWithAnyIgnoreCase(CharSequence[] expected, CharSequence actual, String message) throws AssertionException {
		if (Strings.startsWithAnyIgnoreCase(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: not starts with ignore case any of <" + Arrays.toString(expected) + "> but was: <" + actual + ">", null, actual);
		}
	}

	public static void assertEndsWithAnyIgnoreCase(CharSequence[] expected, CharSequence actual, String message) throws AssertionException {
		if (!Strings.endsWithAnyIgnoreCase(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: ends with ignore case any of <" + Arrays.toString(expected) + "> but was: <" + actual + ">", null, actual);
		}
	}

	public static void assertNotEndsWithAnyIgnoreCase(CharSequence[] expected, CharSequence actual, String message) throws AssertionException {
		if (Strings.endsWithAnyIgnoreCase(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: not ends with ignore case any of <" + Arrays.toString(expected) + "> but was: <" + actual + ">", null, actual);
		}
	}


	public static void assertEqualsIgnoreCase(CharSequence expected, CharSequence actual, String message) throws AssertionException {
		if (!Strings.equalsIgnoreCase(expected, actual)) {
			throw new AssertionException(buildPrefix(message) + "expected: equal ignore case <" + expected + "> but was: <" + actual + ">", Other.of(actual), actual);
		}
	}

	public static void assertNotEqualsIgnoreCase(CharSequence expected, CharSequence actual, String message) throws AssertionException {
		if (Strings.equalsIgnoreCase(expected, actual)) {
			throw new AssertionException(buildPrefix(message) + "expected: not equal ignore case <" + expected + "> but was: <" + actual + ">", Other.of(actual), actual);
		}
	}

	public static void assertContains(CharSequence expected, CharSequence actual, String message) throws AssertionException {
		if (!Strings.contains(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: contains <" + expected + "> but was: <" + actual + ">", Other.of(actual), actual);
		}
	}

	public static void assertNotContains(CharSequence expected, CharSequence actual, String message) throws IllegalArgumentException {
		if (Strings.contains(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: not contains <" + expected + "> but was: <" + actual + ">", Other.of(actual), actual);
		}
	}


	public static void assertContainsIgnoreCase(CharSequence expected, CharSequence actual, String message) throws IllegalArgumentException {
		if (!Strings.containsIgnoreCase(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: contains ignore case <" + expected + "> but was: <" + actual + ">", Other.of(actual), actual);
		}
	}

	public static void assertNotContainsIgnoreCase(CharSequence expected, CharSequence actual, String message) throws IllegalArgumentException {
		if (Strings.containsIgnoreCase(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: not contains ignore case <" + expected + "> but was: <" + actual + ">", Other.of(actual), actual);
		}
	}


	public static void assertStartsWith(CharSequence expected, CharSequence actual, String message) throws IllegalArgumentException {
		if (!Strings.startsWith(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: starts with <" + expected + "> but was: <" + actual + ">", Other.of(actual), actual);
		}
	}


	public static void assertNotStartsWith(CharSequence expected, CharSequence actual, String message) throws IllegalArgumentException {
		if (Strings.startsWith(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: not start with <" + expected + "> but was: <" + actual + ">", Other.of(actual), actual);
		}
	}

	public static void assertEndsWith(CharSequence expected, CharSequence actual, String message) throws IllegalArgumentException {
		if (!Strings.endsWith(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: ends with <" + expected + "> but was: <" + actual + ">", Other.of(actual), actual);
		}
	}

	public static void assertNotEndsWith(CharSequence expected, CharSequence actual, String message) throws IllegalArgumentException {
		if (Strings.endsWith(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: not end with <" + expected + "> but was: <" + actual + ">", Other.of(actual), actual);
		}
	}

	public static void assertStartsWithIgnoreCase(CharSequence expected, CharSequence actual, String message) throws IllegalArgumentException {
		if (!Strings.startsWithIgnoreCase(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: starts with ignore case <" + expected + "> but was: <" + actual + ">", Other.of(actual), actual);
		}
	}

	public static void assertNotStartsWithIgnoreCase(CharSequence expected, CharSequence actual, String message) throws IllegalArgumentException {
		if (Strings.startsWithIgnoreCase(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: not start with ignore case <" + expected + "> but was: <" + actual + ">", Other.of(actual), actual);
		}
	}

	public static void assertEndsWithIgnoreCase(CharSequence expected, CharSequence actual, String message) throws IllegalArgumentException {
		if (!Strings.endsWithIgnoreCase(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: ends with ignore case <" + expected + "> but was: <" + actual + ">", Other.of(actual), actual);
		}
	}

	public static void assertNotEndsWithIgnoreCase(CharSequence expected, CharSequence actual, String message) throws IllegalArgumentException {
		if (Strings.endsWithIgnoreCase(actual, expected)) {
			throw new AssertionException(buildPrefix(message) + "expected: not end with ignore case <" + expected + "> but was: <" + actual + ">", Other.of(actual), actual);
		}
	}
}
