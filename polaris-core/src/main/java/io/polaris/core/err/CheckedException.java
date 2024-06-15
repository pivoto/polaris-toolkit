package io.polaris.core.err;

/**
 * @author Qt
 * @since May 10, 2024
 */
public class CheckedException extends Exception {

	private static final long serialVersionUID = 1L;

	public CheckedException() {
	}

	public CheckedException(String message) {
		super(message);
	}

	public CheckedException(String message, Throwable cause) {
		super(message, cause);
	}

	public CheckedException(Throwable cause) {
		super(cause);
	}

	public CheckedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
