package io.polaris.core.err;

import io.polaris.core.msg.MessageResources;
import io.polaris.core.string.Strings;

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
		fetchCode(null, cause, null);
	}

	public MessageException(Throwable cause, String message) {
		super(message, cause);
		fetchCode(null, cause, message);
	}

	public MessageException(Throwable cause, String code, String message) {
		super(message, cause);
		fetchCode(code, cause, message);
	}

	public MessageException(Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code, String message) {
		super(message, cause, enableSuppression, writableStackTrace);
		fetchCode(code, cause, message);
	}


	private void fetchCode(String code, Throwable cause, String message) {
		if (code == null || code.trim().length() == 0) {
			if (cause instanceof IErrorCode) {
				withCode(((IErrorCode) cause).getCode(), Strings.coalesce(message, cause.getMessage(), code));
			}
		} else {
			withCode(code, Strings.coalesce(message, cause == null ? null : cause.getMessage(), code));
		}
	}

	@Override
	public String getCode() {
		return code;
	}

	public MessageException withCode(String code) {
		withCode(code, this.message);
		return this;
	}

	public MessageException withCode(String code, String defaultMessage) {
		this.code = code;
		if (code != null && code.length() > 0) {
			// 获取编码对应的提示信息，不存在则使用默认消息
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

	public static MessageException of(Throwable t) {
		if (t == null) {
			return new MessageException();
		}
		if (t instanceof MessageException) {
			return (MessageException) t;
		}
		while (t.getCause() != null) {
			t = t.getCause();
			if (t instanceof MessageException) {
				return (MessageException) t;
			}
		}
		return new MessageException(t);
	}

	public static MessageException of(Throwable t, String message) {
		if (t == null) {
			return new MessageException(message);
		}
		if (t instanceof MessageException) {
			return (MessageException) t;
		}
		while (t.getCause() != null) {
			t = t.getCause();
			if (t instanceof MessageException) {
				return (MessageException) t;
			}
		}
		return new MessageException(t, message);
	}

}
