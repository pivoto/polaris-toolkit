package io.polaris.core.err;

import io.polaris.core.msg.MessageResources;

/**
 * @author Qt
 * @since 1.8
 */
public class MessageException extends RuntimeException implements IErrorCode {
	private static final long serialVersionUID = 1L;
	private String code;
	private String message;

	public MessageException() {
		super();
	}

	public MessageException(String message) {
		super(message);
	}

	public MessageException(String code, String message) {
		super(message);
		withCode(code, message);
	}

	public MessageException(Throwable cause) {
		super(cause);
		fetchCode(cause);
	}

	private void fetchCode(Throwable cause) {
		if (cause instanceof IErrorCode) {
			withCode(((IErrorCode) cause).getCode());
		}
	}

	public MessageException(Throwable cause, String message) {
		super(message, cause);
		fetchCode(cause);
	}

	public MessageException(Throwable cause, String code, String message) {
		super(message, cause);
		fetchCode(code, cause);
	}

	public MessageException(Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code, String message) {
		super(message, cause, enableSuppression, writableStackTrace);
		fetchCode(code, cause);
	}

	private void fetchCode(String code, Throwable cause) {
		if (code == null || code.trim().length() == 0) {
			fetchCode(cause);
		} else {
			withCode(code);
		}
	}

	@Override
	public String getCode() {
		return code;
	}

	public MessageException withCode(String code) {
		withCode(code, code);
		return this;
	}

	public MessageException withCode(String code, String defaultMessage) {
		this.code = code;
		if (code != null && code.length() > 0) {
			this.message = MessageResources.getDefaultMessageResource().getMessageOrDefault(code, defaultMessage);
		}
		return this;
	}

	public MessageException withMessage(String message) {
		this.message = message;
		return this;
	}

	@Override
	public String getMessage() {
		return message != null && message.length() > 0 ? message : super.getMessage();
	}

	public static MessageException of(Throwable e) {
		return e instanceof MessageException ? (MessageException) e : new MessageException(e);
	}

	public static MessageException of(Throwable e, String message) {
		return e instanceof MessageException ? (MessageException) e : new MessageException(e, message);
	}

}
