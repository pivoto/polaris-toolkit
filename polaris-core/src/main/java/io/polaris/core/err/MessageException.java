package io.polaris.core.err;

import java.util.function.Supplier;

import io.polaris.core.msg.MessageResources;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since 1.8
 */
public class MessageException extends UncheckedException implements IErrorCode {
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

	public MessageException(String code, String message, Object[] params) {
		super(message);
		withCode(code, message, params);
	}

	public MessageException(IErrorCode errorCode) {
		this(errorCode.getCode(), errorCode.getMessage());
	}


	public MessageException(IErrorCode errorCode, Object[] params) {
		this(errorCode.getCode(), errorCode.getMessage(), params);
	}

	public MessageException(Throwable cause) {
		super(cause);
		fetchCode(cause, null, null);
	}

	public MessageException(Throwable cause, Object[] params) {
		super(cause);
		fetchCode(cause, null, null, params);
	}

	public MessageException(Throwable cause, String message) {
		super(message, cause);
		fetchCode(cause, null, message);
	}

	public MessageException(Throwable cause, String message, Object[] params) {
		super(message, cause);
		fetchCode(cause, null, message, params);
	}

	public MessageException(Throwable cause, String code, String message) {
		super(message, cause);
		fetchCode(cause, code, message);
	}

	public MessageException(Throwable cause, String code, String message, Object[] params) {
		super(message, cause);
		fetchCode(cause, code, message, params);
	}

	public MessageException(Throwable cause, IErrorCode errorCode) {
		this(cause, errorCode.getCode(), errorCode.getMessage());
	}

	public MessageException(Throwable cause, IErrorCode errorCode, Object[] params) {
		this(cause, errorCode.getCode(), errorCode.getMessage(), params);
	}

	public MessageException(Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code, String message) {
		super(message, cause, enableSuppression, writableStackTrace);
		fetchCode(cause, code, message);
	}

	public MessageException(Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code, String message, Object[] params) {
		super(message, cause, enableSuppression, writableStackTrace);
		fetchCode(cause, code, message, params);
	}

	public MessageException(Throwable cause, boolean enableSuppression, boolean writableStackTrace, IErrorCode errorCode) {
		this(cause, enableSuppression, writableStackTrace, errorCode.getCode(), errorCode.getMessage());
	}

	public MessageException(Throwable cause, boolean enableSuppression, boolean writableStackTrace, IErrorCode errorCode, Object[] params) {
		this(cause, enableSuppression, writableStackTrace, errorCode.getCode(), errorCode.getMessage(), params);
	}


	private void fetchCode(Throwable cause, String code, String message) {
		if (code == null || code.trim().isEmpty()) {
			if (cause instanceof IErrorCode) {
				withCode(((IErrorCode) cause).getCode(), Strings.coalesce(message, cause.getMessage(), code));
			}
		} else {
			withCode(code, Strings.coalesce(message, cause == null ? null : cause.getMessage(), code));
		}
	}

	private void fetchCode(Throwable cause, String code, String message, Object[] params) {
		if (code == null || code.trim().isEmpty()) {
			if (cause instanceof IErrorCode) {
				withCode(((IErrorCode) cause).getCode(), Strings.coalesce(message, cause.getMessage(), code), params);
			}
		} else {
			withCode(code, Strings.coalesce(message, cause == null ? null : cause.getMessage(), code), params);
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

	public MessageException withCode(String code, Object[] params) {
		withCode(code, this.message, params);
		return this;
	}

	public MessageException withCode(String code, String defaultMessage) {
		this.code = code;
		if (code != null && !code.isEmpty()) {
			// 获取编码对应的提示信息，不存在则使用默认消息
			this.message = MessageResources.getDefaultMessageResource().getMessageOrDefault(code, defaultMessage);
		}
		return this;
	}

	public MessageException withCode(String code, String defaultMessage, Object[] params) {
		this.code = code;
		if (code != null && !code.isEmpty()) {
			// 获取编码对应的提示信息，不存在则使用默认消息
			this.message = MessageResources.getDefaultMessageResource().getMessageOrDefault(code, defaultMessage, params);
		}
		return this;
	}

	public MessageException withMessage(String message) {
		this.message = message;
		return this;
	}

	@Override
	public String getMessage() {
		return message != null && !message.isEmpty() ? message : super.getMessage();
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
			String msg = t.getMessage();
			if (msg == null) {
				((MessageException) t).withMessage(message);
			} else if (!msg.startsWith(message)) {
				((MessageException) t).withMessage(message + ": " + msg);
			}
			return (MessageException) t;
		}
		while (t.getCause() != null) {
			t = t.getCause();
			if (t instanceof MessageException) {
				String msg = t.getMessage();
				if (msg == null) {
					((MessageException) t).withMessage(message);
				} else if (!msg.startsWith(message)) {
					((MessageException) t).withMessage(message + ": " + msg);
				}
				return (MessageException) t;
			}
		}
		return new MessageException(t, message);
	}

	@SuppressWarnings("unchecked")
	public static <T extends MessageException> T of(Throwable t, Class<T> type, String message, Supplier<T> builder) {
		if (t == null) {
			return builder.get();
		}
		if (type.isAssignableFrom(t.getClass())) {
			if (MessageException.class.isAssignableFrom(type)) {
				String msg = t.getMessage();
				if (msg == null) {
					((MessageException) t).withMessage(message);
				} else if (!msg.startsWith(message)) {
					((MessageException) t).withMessage(message + ": " + msg);
				}
			}
			return (T) t;
		}
		while (t.getCause() != null) {
			t = t.getCause();
			if (type.isAssignableFrom(t.getClass())) {
				if (MessageException.class.isAssignableFrom(type)) {
					String msg = t.getMessage();
					if (msg == null) {
						((MessageException) t).withMessage(message);
					} else if (!msg.startsWith(message)) {
						((MessageException) t).withMessage(message + ": " + msg);
					}
				}
				return (T) t;
			}
		}
		return builder.get();
	}
}
