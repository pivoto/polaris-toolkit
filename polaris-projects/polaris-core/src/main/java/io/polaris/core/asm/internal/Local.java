package io.polaris.core.asm.internal;

import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since May 10, 2024
 */
public class Local {
	private Type type;
	private int index;

	public Local(int index, Type type) {
		this.type = type;
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public Type getType() {
		return type;
	}
}
