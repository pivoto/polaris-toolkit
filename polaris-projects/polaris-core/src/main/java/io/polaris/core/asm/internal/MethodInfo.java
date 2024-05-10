package io.polaris.core.asm.internal;

import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since May 09, 2024
 */
public abstract class MethodInfo {
	protected MethodInfo() {
	}

	abstract public ClassInfo getClassInfo();

	abstract public int getModifiers();

	abstract public Signature getSignature();

	abstract public Type[] getExceptionTypes();

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof MethodInfo))
			return false;
		return getSignature().equals(((MethodInfo) o).getSignature());
	}

	public int hashCode() {
		return getSignature().hashCode();
	}

	public String toString() {
		return getSignature().toString();
	}
}
