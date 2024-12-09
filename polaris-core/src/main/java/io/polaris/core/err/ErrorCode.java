package io.polaris.core.err;

import io.polaris.core.string.Strings;

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

	public ErrorCode(String code, String message, Object... args) {
		this.code = code;
		this.message = Strings.format(message, args);
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
