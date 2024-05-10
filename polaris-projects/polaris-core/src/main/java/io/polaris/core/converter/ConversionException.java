package io.polaris.core.converter;

/**
 * @author Qt
 * @since  Apr 17, 2024
 */
public class ConversionException extends RuntimeException {

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
