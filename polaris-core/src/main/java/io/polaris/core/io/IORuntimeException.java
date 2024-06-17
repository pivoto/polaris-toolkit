package io.polaris.core.io;

import io.polaris.core.err.UncheckedException;

/**
 * @author Qt
 * @since  Jan 19, 2024
 */
@SuppressWarnings("ALL")
public class IORuntimeException extends UncheckedException {
	private static final long serialVersionUID = 1L;

	public IORuntimeException() {
		super();
	}

	public IORuntimeException(String message) {
		super(message);
	}

	public IORuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public IORuntimeException(Throwable cause) {
		super(cause);
	}

	protected IORuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
