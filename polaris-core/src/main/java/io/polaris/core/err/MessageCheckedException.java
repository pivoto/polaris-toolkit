package io.polaris.core.err;

import java.util.function.Supplier;

import io.polaris.core.msg.MessageResources;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since 1.8
 */
public class MessageCheckedException extends CheckedException implements IErrorCode {
	private static final long serialVersionUID = 1L;
	private String code;
	private String message;

	public MessageCheckedException() {
		super();
	}

	public MessageCheckedException(String message) {
		super(message);
	}

	public MessageCheckedException(String code, String message) {
		super(message);
		withCode(code, message);
	}

	public MessageCheckedException(String code, String message, Object[] params) {
		super(message);
		withCode(code, message, params);
	}

	public MessageCheckedException(IErrorCode errorCode) {
		this(errorCode.getCode(), errorCode.getMessage());
	}


	public MessageCheckedException(IErrorCode errorCode, Object[] params) {
		this(errorCode.getCode(), errorCode.getMessage(), params);
	}

	public MessageCheckedException(Throwable cause) {
		super(cause);
		fetchCode(cause, null, null);
	}

	public MessageCheckedException(Throwable cause, Object[] params) {
		super(cause);
		fetchCode(cause, null, null, params);
	}

	public MessageCheckedException(Throwable cause, String message) {
		super(message, cause);
		fetchCode(cause, null, message);
	}

	public MessageCheckedException(Throwable cause, String message, Object[] params) {
		super(message, cause);
		fetchCode(cause, null, message, params);
	}

	public MessageCheckedException(Throwable cause, String code, String message) {
		super(message, cause);
		fetchCode(cause, code, message);
	}

	public MessageCheckedException(Throwable cause, String code, String message, Object[] params) {
		super(message, cause);
		fetchCode(cause, code, message, params);
	}

	public MessageCheckedException(Throwable cause, IErrorCode errorCode) {
		this(cause, errorCode.getCode(), errorCode.getMessage());
	}

	public MessageCheckedException(Throwable cause, IErrorCode errorCode, Object[] params) {
		this(cause, errorCode.getCode(), errorCode.getMessage(), params);
	}

	public MessageCheckedException(Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code, String message) {
		super(message, cause, enableSuppression, writableStackTrace);
		fetchCode(cause, code, message);
	}

	public MessageCheckedException(Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code, String message, Object[] params) {
		super(message, cause, enableSuppression, writableStackTrace);
		fetchCode(cause, code, message, params);
	}

	public MessageCheckedException(Throwable cause, boolean enableSuppression, boolean writableStackTrace, IErrorCode errorCode) {
		this(cause, enableSuppression, writableStackTrace, errorCode.getCode(), errorCode.getMessage());
	}

	public MessageCheckedException(Throwable cause, boolean enableSuppression, boolean writableStackTrace, IErrorCode errorCode, Object[] params) {
		this(cause, enableSuppression, writableStackTrace, errorCode.getCode(), errorCode.getMessage(), params);
	}


	private void fetchCode(Throwable cause, String code, String message) {
		if (code == null || code.trim().isEmpty()) {
			if (cause instanceof IErrorCode) {
				withCode(((IErrorCode) cause).getCode(), Strings.coalesce(message, cause.getMessage(), code));
			}
		} else {
			withCode(code, Strings.coalesce(message, cause.getMessage(), code));
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

	public MessageCheckedException withCode(String code) {
		withCode(code, this.message);
		return this;
	}

	public MessageCheckedException withCode(String code, Object[] params) {
		withCode(code, this.message, params);
		return this;
	}

	public MessageCheckedException withCode(String code, String defaultMessage) {
		this.code = code;
		if (code != null && !code.isEmpty()) {
			// 获取编码对应的提示信息，不存在则使用默认消息
			this.message = MessageResources.getDefaultMessageResource().getMessageOrDefault(code, defaultMessage);
		}
		return this;
	}

	public MessageCheckedException withCode(String code, String defaultMessage, Object[] params) {
		this.code = code;
		if (code != null && !code.isEmpty()) {
			// 获取编码对应的提示信息，不存在则使用默认消息
			this.message = MessageResources.getDefaultMessageResource().getMessageOrDefault(code, defaultMessage, params);
		}
		return this;
	}

	public MessageCheckedException withMessage(String message) {
		this.message = message;
		return this;
	}

	@Override
	public String getMessage() {
		return message != null && !message.isEmpty() ? message : super.getMessage();
	}

	public static MessageCheckedException of(Throwable t) {
		if (t == null) {
			return new MessageCheckedException();
		}
		if (t instanceof MessageCheckedException) {
			return (MessageCheckedException) t;
		}
		while (t.getCause() != null) {
			t = t.getCause();
			if (t instanceof MessageCheckedException) {
				return (MessageCheckedException) t;
			}
		}
		return new MessageCheckedException(t);
	}

	public static MessageCheckedException of(Throwable t, String message) {
		if (t == null) {
			return new MessageCheckedException(message);
		}
		if (t instanceof MessageCheckedException) {
			String msg = t.getMessage();
			if (msg == null) {
				((MessageCheckedException) t).withMessage(message);
			} else if (!msg.startsWith(message)) {
				((MessageCheckedException) t).withMessage(message + ": " + msg);
			}
			return (MessageCheckedException) t;
		}
		while (t.getCause() != null) {
			t = t.getCause();
			if (t instanceof MessageCheckedException) {
				String msg = t.getMessage();
				if (msg == null) {
					((MessageCheckedException) t).withMessage(message);
				} else if (!msg.startsWith(message)) {
					((MessageCheckedException) t).withMessage(message + ": " + msg);
				}
				return (MessageCheckedException) t;
			}
		}
		return new MessageCheckedException(t, message);
	}

	@SuppressWarnings("unchecked")
	public static <T extends MessageCheckedException> T of(Throwable t, Class<T> type, String message, Supplier<T> builder) {
		if (t == null) {
			return builder.get();
		}
		if (type.isAssignableFrom(t.getClass())) {
			if (MessageCheckedException.class.isAssignableFrom(type)) {
				String msg = t.getMessage();
				if (msg == null) {
					((MessageCheckedException) t).withMessage(message);
				} else if (!msg.startsWith(message)) {
					((MessageCheckedException) t).withMessage(message + ": " + msg);
				}
			}
			return (T) t;
		}
		while (t.getCause() != null) {
			t = t.getCause();
			if (type.isAssignableFrom(t.getClass())) {
				if (MessageCheckedException.class.isAssignableFrom(type)) {
					String msg = t.getMessage();
					if (msg == null) {
						((MessageCheckedException) t).withMessage(message);
					} else if (!msg.startsWith(message)) {
						((MessageCheckedException) t).withMessage(message + ": " + msg);
					}
				}
				return (T) t;
			}
		}
		return builder.get();
	}

}
