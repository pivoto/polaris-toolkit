package io.polaris.core.err;

/**
 * @author Qt
 * @since  Aug 06, 2023
 */
public class InvocationException extends UncheckedException {
	private static final long serialVersionUID = 1L;

	public InvocationException(Throwable cause) {
		super(cause instanceof InvocationException ? cause.getCause() : cause);
	}

	@Override
	public synchronized Throwable getCause() {
		return super.getCause();
	}

	public static InvocationException of(Throwable e) {
		return e instanceof InvocationException ? (InvocationException) e : new InvocationException(e);
	}
}
