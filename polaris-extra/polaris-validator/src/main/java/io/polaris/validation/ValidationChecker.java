package io.polaris.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.polaris.core.err.ValidationException;
import io.polaris.core.function.Condition;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Nov 13, 2025
 */
public class ValidationChecker {


	private final List<ValidationChecker> checkers;
	private final ValidationChecker parent;
	private String code;
	private String message;
	private Condition condition;


	ValidationChecker(String code, String message) {
		this.parent = null;
		this.checkers = new ArrayList<>();
		this.code = code;
		this.message = message;
	}

	private ValidationChecker(ValidationChecker parent, Condition condition) {
		this.parent = parent;
		this.checkers = null;
		this.code = parent.code;
		this.message = parent.message;
		this.condition = condition;
	}

	public ValidationChecker withCode(String code) {
		this.code = code;
		return this;
	}

	public ValidationChecker withMessage(String message) {
		this.message = message;
		return this;
	}

	public ValidationChecker withMessage(String code, String message) {
		this.code = code;
		this.message = message;
		return this;
	}

	public ValidationChecker expect(Condition condition) {
		if (this.condition == null) {
			this.condition = condition;
			return this;
		}
		if (this.parent != null) {
			return this.parent.expect(condition);
		}
		assert this.checkers != null;
		ValidationChecker sub = new ValidationChecker(this, condition);
		this.checkers.add(sub);
		return sub;
	}


	public void check() throws ValidationException {
		if (parent != null) {
			parent.check();
		} else {
			this.$check();
		}
	}


	private void $check() throws ValidationException {
		if (condition != null) {
			if (!condition.get()) {
				if (code != null && !code.isEmpty()) {
					throw new ValidationException(code, message);
				}
				throw new ValidationException(message);
			}
		}
		if (checkers != null) {
			for (ValidationChecker checker : checkers) {
				checker.$check();
			}
		}
	}

	public void checkAll() throws ValidationException {
		if (parent != null) {
			parent.checkAll();
		} else {
			ValidationException err = this.$checkAll(null);
			if (err != null) {
				throw err;
			}
		}
	}

	private ValidationException $checkAll(ValidationException base) {
		if (condition != null) {
			if (!condition.get()) {
				if (code != null && !code.isEmpty()) {
					if (base == null) {
						base = new ValidationException(code, message);
					} else {
						base.addSuppressed(new ValidationException(code, message));
					}
				} else {
					if (base == null) {
						base = new ValidationException(message);
					} else {
						base.addSuppressed(new ValidationException(message));
					}
				}
			}
		}
		if (checkers != null) {
			for (ValidationChecker checker : checkers) {
				base = checker.$checkAll(base);
			}
		}
		return base;
	}

	public ValidationChecker isTrue(boolean condition) {
		return expect(() -> condition);
	}

	public ValidationChecker isFalse(boolean condition) {
		return expect(() -> !condition);
	}

	public ValidationChecker isNull(Object actual) {
		return expect(() -> actual == null);
	}

	public ValidationChecker notNull(Object actual) {
		return expect(() -> actual != null);
	}

	public ValidationChecker equals(char expected, char actual) {
		return expect(() -> expected == actual);
	}

	public ValidationChecker equals(byte expected, byte actual) {
		return expect(() -> expected == actual);
	}

	public ValidationChecker equals(short expected, short actual) {
		return expect(() -> expected == actual);
	}

	public ValidationChecker equals(int expected, int actual) {
		return expect(() -> expected == actual);
	}

	public ValidationChecker equals(long expected, long actual) {
		return expect(() -> expected == actual);
	}

	public ValidationChecker equals(double expected, double actual) {
		return expect(() -> expected == actual);
	}

	public ValidationChecker equals(float expected, float actual) {
		return expect(() -> expected == actual);
	}

	public ValidationChecker equals(Object expected, Object actual) {
		return expect(() -> Objects.equals(expected, actual));
	}

	public ValidationChecker notEquals(char expected, char actual) {
		return expect(() -> expected == actual);
	}

	public ValidationChecker notEquals(byte expected, byte actual) {
		return expect(() -> expected == actual);
	}

	public ValidationChecker notEquals(short expected, short actual) {
		return expect(() -> expected != actual);
	}

	public ValidationChecker notEquals(int expected, int actual) {
		return expect(() -> expected != actual);
	}

	public ValidationChecker notEquals(long expected, long actual) {
		return expect(() -> expected != actual);
	}

	public ValidationChecker notEquals(double expected, double actual) {
		return expect(() -> expected != actual);
	}

	public ValidationChecker notEquals(float expected, float actual) {
		return expect(() -> expected != actual);
	}

	public ValidationChecker notEquals(Object expected, Object actual) {
		return expect(() -> !Objects.equals(expected, actual));
	}

	public ValidationChecker isSame(Object expected, Object actual) {
		return expect(() -> expected == actual);
	}

	public ValidationChecker notSame(Object expected, Object actual) {
		return expect(() -> expected != actual);
	}

	public ValidationChecker isInstanceOf(Class<?> expectedType, Object actualValue) {
		return expect(() -> expectedType.isInstance(actualValue));
	}

	public ValidationChecker notInstanceOf(Class<?> expectedType, Object actualValue) {
		return expect(() -> !expectedType.isInstance(actualValue));
	}

	public ValidationChecker isAssignable(Class<?> expectedType, Class<?> actualValue) {
		return expect(() -> actualValue != null && expectedType.isAssignableFrom(actualValue));
	}

	public ValidationChecker notAssignable(Class<?> expectedType, Class<?> actualValue) {
		return expect(() -> actualValue == null || !expectedType.isAssignableFrom(actualValue));
	}

	public ValidationChecker isEmpty(CharSequence actual) {
		return expect(() -> Strings.isEmpty(actual));
	}


	public ValidationChecker notEmpty(CharSequence actual) {
		return expect(() -> !Strings.isEmpty(actual));
	}

	public ValidationChecker isBlank(CharSequence actual) {
		return expect(() -> Strings.isBlank(actual));
	}

	public ValidationChecker notBlank(CharSequence actual) {
		return expect(() -> !Strings.isBlank(actual));
	}

	public ValidationChecker equalsAny(CharSequence[] expected, CharSequence actual) {
		return expect(() -> Strings.equalsAny(actual, expected));
	}

	public ValidationChecker notEqualsAny(CharSequence[] expected, CharSequence actual) {
		return expect(() -> !Strings.equalsAny(actual, expected));
	}

	public ValidationChecker equalsAnyIgnoreCase(CharSequence[] expected, CharSequence actual) {
		return expect(() -> Strings.equalsAnyIgnoreCase(actual, expected));
	}

	public ValidationChecker notEqualsAnyIgnoreCase(CharSequence[] expected, CharSequence actual) {
		return expect(() -> !Strings.equalsAnyIgnoreCase(actual, expected));
	}

	public ValidationChecker containsAny(CharSequence[] expected, CharSequence actual) {
		return expect(() -> Strings.containsAny(actual, expected));
	}

	public ValidationChecker notContainsAny(CharSequence[] expected, CharSequence actual) {
		return expect(() -> !Strings.containsAny(actual, expected));
	}

	public ValidationChecker containsAnyIgnoreCase(CharSequence[] expected, CharSequence actual) {
		return expect(() -> Strings.containsAnyIgnoreCase(actual, expected));
	}

	public ValidationChecker notContainsAnyIgnoreCase(CharSequence[] expected, CharSequence actual) {
		return expect(() -> !Strings.containsAny(actual, expected));
	}

	public ValidationChecker startsWithAny(CharSequence[] expected, CharSequence actual) {
		return expect(() -> Strings.startsWithAny(actual, expected));
	}

	public ValidationChecker notStartsWithAny(CharSequence[] expected, CharSequence actual) {
		return expect(() -> !Strings.startsWithAny(actual, expected));
	}

	public ValidationChecker endsWithAny(CharSequence[] expected, CharSequence actual) {
		return expect(() -> Strings.endsWithAny(actual, expected));
	}

	public ValidationChecker notEndsWithAny(CharSequence[] expected, CharSequence actual) {
		return expect(() -> !Strings.endsWithAny(actual, expected));
	}

	public ValidationChecker startsWithAnyIgnoreCase(CharSequence[] expected, CharSequence actual) {
		return expect(() -> Strings.startsWithAnyIgnoreCase(actual, expected));
	}

	public ValidationChecker notStartsWithAnyIgnoreCase(CharSequence[] expected, CharSequence actual) {
		return expect(() -> !Strings.startsWithAnyIgnoreCase(actual, expected));
	}

	public ValidationChecker endsWithAnyIgnoreCase(CharSequence[] expected, CharSequence actual) {
		return expect(() -> Strings.endsWithAnyIgnoreCase(actual, expected));
	}

	public ValidationChecker notEndsWithAnyIgnoreCase(CharSequence[] expected, CharSequence actual) {
		return expect(() -> !Strings.endsWithAnyIgnoreCase(actual, expected));
	}

	public ValidationChecker equals(CharSequence expected, CharSequence actual) {
		return expect(() -> Strings.equals(actual, expected));
	}

	public ValidationChecker notEquals(CharSequence expected, CharSequence actual) {
		return expect(() -> !Strings.equals(actual, expected));
	}

	public ValidationChecker equalsIgnoreCase(CharSequence expected, CharSequence actual) {
		return expect(() -> Strings.equalsIgnoreCase(actual, expected));
	}

	public ValidationChecker notEqualsIgnoreCase(CharSequence expected, CharSequence actual) {
		return expect(() -> !Strings.equalsIgnoreCase(actual, expected));
	}

	public ValidationChecker contains(CharSequence expected, CharSequence actual) {
		return expect(() -> Strings.contains(actual, expected));
	}

	public ValidationChecker notContains(CharSequence expected, CharSequence actual) {
		return expect(() -> !Strings.contains(actual, expected));
	}

	public ValidationChecker containsIgnoreCase(CharSequence expected, CharSequence actual) {
		return expect(() -> Strings.containsIgnoreCase(actual, expected));
	}

	public ValidationChecker notContainsIgnoreCase(CharSequence expected, CharSequence actual) {
		return expect(() -> !Strings.containsIgnoreCase(actual, expected));
	}

	public ValidationChecker startsWith(CharSequence expected, CharSequence actual) {
		return expect(() -> Strings.startsWith(actual, expected));
	}

	public ValidationChecker notStartsWith(CharSequence expected, CharSequence actual) {
		return expect(() -> !Strings.startsWith(actual, expected));
	}

	public ValidationChecker endsWith(CharSequence expected, CharSequence actual) {
		return expect(() -> Strings.endsWith(actual, expected));
	}

	public ValidationChecker notEndsWith(CharSequence expected, CharSequence actual) {
		return expect(() -> !Strings.endsWith(actual, expected));
	}

	public ValidationChecker startsWithIgnoreCase(CharSequence expected, CharSequence actual) {
		return expect(() -> Strings.startsWithIgnoreCase(actual, expected));
	}

	public ValidationChecker notStartsWithIgnoreCase(CharSequence expected, CharSequence actual) {
		return expect(() -> !Strings.startsWithIgnoreCase(actual, expected));
	}

	public ValidationChecker endsWithIgnoreCase(CharSequence expected, CharSequence actual) {
		return expect(() -> Strings.endsWithIgnoreCase(actual, expected));
	}

	public ValidationChecker notEndsWithIgnoreCase(CharSequence expected, CharSequence actual) {
		return expect(() -> !Strings.endsWithIgnoreCase(actual, expected));
	}


}
