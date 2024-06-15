package io.polaris.core.cluster;

import io.polaris.core.err.UncheckedException;

/**
 * @author Qt
 * @since  Apr 17, 2024
 */
public class InstanceRegisterException extends UncheckedException {

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
