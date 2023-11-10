package io.polaris.core.jdbc.dbv;

/**
 * @author Qt
 * @since 1.8
 */
public class DbvException extends RuntimeException {

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
