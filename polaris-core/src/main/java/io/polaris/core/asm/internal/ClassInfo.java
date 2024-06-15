package io.polaris.core.asm.internal;

import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since May 09, 2024
 */
public abstract class ClassInfo {
	protected ClassInfo() {
	}

	abstract public Type getType();

	abstract public Type getSuperType();

	abstract public Type[] getInterfaces();

	abstract public int getModifiers();

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof ClassInfo))
			return false;
		return getType().equals(((ClassInfo) o).getType());
	}

	public int hashCode() {
		return getType().hashCode();
	}

	public String toString() {
		return getType().getClassName();
	}
}
