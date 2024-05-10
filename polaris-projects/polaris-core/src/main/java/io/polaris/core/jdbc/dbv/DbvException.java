package io.polaris.core.jdbc.dbv;

import io.polaris.core.err.UncheckedException;

/**
 * @author Qt
 * @since 1.8
 */
public class DbvException extends UncheckedException {

	private static final long serialVersionUID = 1L;

	public DbvException() {
		super();
	}

	public DbvException(String message, Throwable cause) {
		super(message, cause);
	}

	public DbvException(String message) {
		super(message);
	}

	public DbvException(Throwable cause) {
		super(cause);
	}
}
