package io.polaris.core.cluster;

/**
 * @author Qt
 * @since 1.8,  Apr 17, 2024
 */
public class InstanceQueryException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InstanceQueryException() {
		super();
	}

	public InstanceQueryException(String message) {
		super(message);
	}

	public InstanceQueryException(String message, Throwable cause) {
		super(message, cause);
	}

	public InstanceQueryException(Throwable cause) {
		super(cause);
	}

	protected InstanceQueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
