package io.polaris.core.cluster;

/**
 * @author Qt
 * @since 1.8,  Apr 17, 2024
 */
public class InstanceRegisterException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InstanceRegisterException() {
		super();
	}

	public InstanceRegisterException(String message) {
		super(message);
	}

	public InstanceRegisterException(String message, Throwable cause) {
		super(message, cause);
	}

	public InstanceRegisterException(Throwable cause) {
		super(cause);
	}

	protected InstanceRegisterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
