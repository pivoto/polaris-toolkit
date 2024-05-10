package io.polaris.core.asm;

import io.polaris.core.err.UncheckedException;

/**
 * @author Qt
 * @since May 10, 2024
 */
public class BytecodeOperationException extends UncheckedException {
	private static final long serialVersionUID = 1L;

	public BytecodeOperationException() {
	}

	public BytecodeOperationException(String message) {
		super(message);
	}

	public BytecodeOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	public BytecodeOperationException(Throwable cause) {
		super(cause);
	}

}
