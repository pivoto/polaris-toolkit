package io.polaris.core.err;

/**
 * @author Qt
 * @since  Aug 06, 2023
 */
public class UnimplementedException extends UncheckedException {
	private static final long serialVersionUID = 1L;


	public UnimplementedException() {
	}

	public UnimplementedException(String message) {
		super(message);
	}

	public UnimplementedException(String message, Throwable cause) {
		super(message, cause instanceof UnimplementedException ? cause.getCause() : cause);
	}

	public UnimplementedException(Throwable cause) {
		super(cause instanceof UnimplementedException ? cause.getCause() : cause);
	}

	public UnimplementedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause instanceof UnimplementedException ? cause.getCause() : cause, enableSuppression, writableStackTrace);
	}

	@Override
	public synchronized Throwable getCause() {
		return super.getCause();
	}

	public static UnimplementedException of(Throwable e) {
		return e instanceof UnimplementedException ? (UnimplementedException) e : new UnimplementedException(e);
	}
}
