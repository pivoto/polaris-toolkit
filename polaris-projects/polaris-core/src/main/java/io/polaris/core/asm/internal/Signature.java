package io.polaris.core.asm.internal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since May 09, 2024
 */
@EqualsAndHashCode
@Getter
public class Signature {
	private final String name;
	private final String descriptor;

	public Signature(String name, String descriptor) {
		this.name = name;
		this.descriptor = descriptor;
	}

	public Signature(String name, Type returnType, Type[] argumentTypes) {
		this(name, Type.getMethodDescriptor(returnType, argumentTypes));
	}

	public Type getReturnType() {
		return Type.getReturnType(descriptor);
	}

	public Type[] getArgumentTypes() {
		return Type.getArgumentTypes(descriptor);
	}

	public String toString() {
		return name + descriptor;
	}


}
