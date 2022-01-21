package io.polaris.toolkit.spring.crypto;

/**
 * @author Qt
 * @version Nov 02, 2021
 * @since 1.8
 */
public class CryptoOperationException extends RuntimeException {

	public CryptoOperationException() {
	}

	public CryptoOperationException(String message) {
		super(message);
	}

	public CryptoOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	public CryptoOperationException(Throwable cause) {
		super(cause);
	}

	public CryptoOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
