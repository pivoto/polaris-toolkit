package io.polaris.core.script;

import io.polaris.core.err.UncheckedException;

/**
 * @author Qt
 * @since 1.8
 */
public class ScriptEvalException extends UncheckedException {

	private static final long serialVersionUID = 1L;

	public ScriptEvalException(String message) {
		super(message);
	}

	public ScriptEvalException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScriptEvalException(Throwable cause) {
		super(cause);
	}

}
