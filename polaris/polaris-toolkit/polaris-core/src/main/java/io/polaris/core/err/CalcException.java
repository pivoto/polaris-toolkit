package io.polaris.core.err;

/**
 * @author Qt
 * @since 1.8
 */
public class CalcException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public CalcException(String message) {
		super(message);
	}

	public CalcException(String message, Throwable cause) {
		super(message, cause);
	}

	public CalcException(Throwable cause) {
		super(cause);
	}

}
