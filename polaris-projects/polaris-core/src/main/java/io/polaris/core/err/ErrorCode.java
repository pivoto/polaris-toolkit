package io.polaris.core.err;

/**
 * @author Qt
 * @since 1.8
 */
public class ErrorCode implements IErrorCode {
	private String code;
	private String message;

	public ErrorCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
