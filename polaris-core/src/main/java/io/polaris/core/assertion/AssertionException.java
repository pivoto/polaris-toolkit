package io.polaris.core.assertion;

import io.polaris.core.tuple.ValueRef;

/**
 * @author Qt
 * @since 1.8
 */
public class AssertionException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;
	private ValueRef<Object> expected;
	private ValueRef<Object> actual;

	public AssertionException(String s, Object expected, Object actual) {
		super(s);
		init(expected, actual);
	}

	public AssertionException(String message, Throwable cause, Object expected, Object actual) {
		super(message, cause);
		init(expected, actual);
	}


	private void init(Object expected, Object actual) {
		this.expected = ValueRef.of(expected);
		this.actual = ValueRef.of(actual);
	}

	public AssertionException(String s) {
		super(s);
	}

	public AssertionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValueRef<Object> getExpected() {
		return expected;
	}

	public ValueRef<Object> getActual() {
		return actual;
	}
}
