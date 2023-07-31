package io.polaris.core.codec;

/**
 * @author Qt
 * @since 1.8
 */
public class EncoderException extends IllegalStateException {
	private Throwable cause;

	EncoderException(String msg, Throwable cause) {
		super(msg);

		this.cause = cause;
	}

	public Throwable getCause() {
		return cause;
	}
}
