package io.polaris.core.crypto;

import io.polaris.core.err.UncheckedException;

/**
 * @author Qt
 * @since  Jan 19, 2024
 */
public class CryptoRuntimeException extends UncheckedException {

	private static final long serialVersionUID = 1L;

	public CryptoRuntimeException() {
	}

	public CryptoRuntimeException(String message) {
		super(message);
	}

	public CryptoRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public CryptoRuntimeException(Throwable cause) {
		super(cause);
	}

	public CryptoRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
