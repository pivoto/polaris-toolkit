package io.polaris.core.script;

/**
 * @author Qt
 * @since 1.8
 */
public class ScriptEvalException extends RuntimeException{

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
