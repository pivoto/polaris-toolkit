package io.polaris.core.asm.internal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since May 09, 2024
 */
@EqualsAndHashCode
@Getter
public class Signature {
	private String name;
	private String desc;

	public Signature(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}

	public Signature(String name, Type returnType, Type[] argumentTypes) {
		this(name, Type.getMethodDescriptor(returnType, argumentTypes));
	}

	public Type getReturnType() {
		return Type.getReturnType(desc);
	}

	public Type[] getArgumentTypes() {
		return Type.getArgumentTypes(desc);
	}

	public String toString() {
		return name + desc;
	}


}
