package io.polaris.core.jdbc;

import io.polaris.core.err.UncheckedException;

/**
 * @author Qt
 * @since 1.8
 */
public class JdbcException extends UncheckedException {

	private static final long serialVersionUID = 1L;

	public JdbcException() {
		super();
	}

	public JdbcException(String message, Throwable cause) {
		super(message, cause);
	}

	public JdbcException(String message) {
		super(message);
	}

	public JdbcException(Throwable cause) {
		super(cause);
	}

}
