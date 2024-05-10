package io.polaris.core.err;

/**
 * @author Qt
 * @since May 10, 2024
 */
public class UncheckedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UncheckedException() {
	}

	public UncheckedException(String message) {
		super(message);
	}

	public UncheckedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UncheckedException(Throwable cause) {
		super(cause);
	}

	public UncheckedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
