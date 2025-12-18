package io.polaris.core.codec;

/**
 * @author Qt
 */
public class CodecException extends IllegalStateException {
	private Throwable cause;

	CodecException(String msg, Throwable cause) {
		super(msg);

		this.cause = cause;
	}

	public Throwable getCause() {
		return cause;
	}
}
