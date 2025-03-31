package io.polaris.json.migration;

/**
 * 用于迁移和替代 com.alibaba.fastjson2.JSONException 类
 *
 * @author Qt
 * @since Mar 30, 2025
 */
public class JSONException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JSONException() {
		super();
	}

	public JSONException(String message) {
		super(message);
	}

	public JSONException(String message, Throwable cause) {
		super(message, cause);
	}

	public JSONException(Throwable cause) {
		super(cause);
	}
}
