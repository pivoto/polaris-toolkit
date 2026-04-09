package io.polaris.core.asm.internal;

import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since Apr 09, 2026
 */
class AsmMethodDescriptor implements MethodDescriptor {

	/** 所属类信息 */
	private final ClassDescriptor classDescriptor;
	/** 访问标志 */
	private final int access;
	/** 方法签名 */
	private final Signature signature;
	/** 异常类型数组 */
	private final Type[] exceptionTypes;

	AsmMethodDescriptor(ClassDescriptor classDescriptor, int access, Signature signature, Type[] exceptionTypes) {
		this.classDescriptor = classDescriptor;
		this.access = access;
		this.signature = signature;
		this.exceptionTypes = exceptionTypes;
	}


	@Override
	public ClassDescriptor getClassDescriptor() {
		return classDescriptor;
	}

	@Override
	public Signature getSignature() {
		return signature;
	}

	@Override
	public Type[] getExceptionTypes() {
		return exceptionTypes;
	}

	@Override
	public int getModifiers() {
		return access;
	}

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof MethodDescriptor)) {
			return false;
		}
		return getSignature().equals(((MethodDescriptor) o).getSignature());
	}

	public int hashCode() {
		return getSignature().hashCode();
	}

	public String toString() {
		return getSignature().toString();
	}
}
