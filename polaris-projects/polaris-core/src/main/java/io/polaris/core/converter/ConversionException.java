package io.polaris.core.converter;

import io.polaris.core.err.UncheckedException;

/**
 * @author Qt
 * @since  Apr 17, 2024
 */
public class ConversionException extends UncheckedException {

	private static final long serialVersionUID = 1L;

	public ConversionException() {
		super();
	}

	public ConversionException(String message) {
		super(message);
	}

	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConversionException(Throwable cause) {
		super(cause);
	}

	protected ConversionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
