package io.polaris.core.err;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * @author Qt
 * @since 1.8
 */
public class ValidationException extends MessageException {
	private static final long serialVersionUID = 1L;
	protected Collection<ErrorDetail> errorDetails = new LinkedHashSet<>();

	public ValidationException() {
		super();
	}

	public ValidationException(String message) {
		super(message);
	}

	public ValidationException(String code, String message) {
		super(code, message);
	}

	public ValidationException(Throwable cause) {
		super(cause);
	}

	public ValidationException(Throwable cause, String message) {
		super(cause, message);
	}

	public ValidationException(Throwable cause, String code, String message) {
		super(cause, code, message);
	}

	public ValidationException(Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code, String message) {
		super(cause, enableSuppression, writableStackTrace, code, message);
	}


	@Override
	public String getMessage() {
		if (errorDetails != null && !errorDetails.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append(super.getMessage());
			for (ErrorDetail errorDetail : errorDetails) {
				sb.append("\n").append(errorDetail.toString());
			}
			return sb.toString();
		}
		return super.getMessage();
	}

	public Collection<ErrorDetail> getErrorDetails() {
		return errorDetails;
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	public static class ErrorDetail {
		private String field;
		private String message;

		@Override
		public String toString() {
			return "[" + field + "]" + message;
		}
	}

}
