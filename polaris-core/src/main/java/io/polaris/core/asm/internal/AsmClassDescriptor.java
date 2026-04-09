package io.polaris.core.asm.internal;

import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since Apr 09, 2026
 */
class AsmClassDescriptor implements ClassDescriptor {

	private final Type type;
	private final Type superType;
	private final Type[] interfaces;
	private final int modifiers;

	AsmClassDescriptor(Type type, Type superType, Type[] interfaces, int modifiers) {
		this.type = type;
		this.superType = (superType != null) ? superType : AsmConsts.TYPE_OBJECT;
		this.interfaces = interfaces;
		this.modifiers = modifiers;
	}

	AsmClassDescriptor(final Class<?> clazz) {
		final Type type = Type.getType(clazz);
		final Type superType = (clazz.getSuperclass() == null) ? null : Type.getType(clazz.getSuperclass());
		this.type = type;
		this.superType = (superType != null) ? superType : AsmConsts.TYPE_OBJECT;
		this.interfaces = AsmTypes.getTypes(clazz.getInterfaces());
		this.modifiers = clazz.getModifiers();
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Type getSuperType() {
		return superType;
	}

	@Override
	public Type[] getInterfaces() {
		return interfaces;
	}

	@Override
	public int getModifiers() {
		return modifiers;
	}


	/**
	 * 判断是否相等
	 *
	 * @param o 待比较的对象
	 * @return 如果类型相同则返回 true
	 */
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof ClassDescriptor)) {
			return false;
		}
		return getType().equals(((ClassDescriptor) o).getType());
	}

	/**
	 * 获取哈希码
	 *
	 * @return 类型的哈希值
	 */
	public int hashCode() {
		return getType().hashCode();
	}

	/**
	 * 转换为字符串
	 *
	 * @return 类名的字符串表示
	 */
	public String toString() {
		return getType().getClassName();
	}
}
