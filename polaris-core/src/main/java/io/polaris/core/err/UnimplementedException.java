package io.polaris.core.err;

/**
 * @author Qt
 * @since  Aug 06, 2023
 */
public class UnimplementedException extends UncheckedException {
	private static final long serialVersionUID = 1L;

	public UnimplementedException(Throwable cause) {
		super(cause instanceof UnimplementedException ? cause.getCause() : cause);
	}

	@Override
	public synchronized Throwable getCause() {
		return super.getCause();
	}

	public static UnimplementedException of(Throwable e) {
		return e instanceof UnimplementedException ? (UnimplementedException) e : new UnimplementedException(e);
	}
}
