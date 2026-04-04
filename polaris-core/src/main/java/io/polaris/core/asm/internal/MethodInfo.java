package io.polaris.core.asm.internal;

import org.objectweb.asm.Type;

/**
 * 方法信息抽象类，封装 ASM 方法的基本信息
 * <p>
 * 包含方法所属类、访问标志、签名和异常类型
 *
 * @author Qt
 * @since May 09, 2024
 */
public abstract class MethodInfo {
	/**
	 * 受保护构造器，防止外部直接实例化
	 */
	protected MethodInfo() {
	}

	/**
	 * 获取方法所属类的信息
	 *
	 * @return 声明该方法的类的 ClassInfo
	 */
	/**
	 * 获取方法所属的类信息
	 *
	 * @return ClassInfo 对象
	 */
	abstract public ClassInfo getClassInfo();

	/**
	 * 获取访问标志
	 *
	 * @return ASM 访问标志位（如 ACC_PUBLIC、ACC_FINAL 等）
	 */
	/**
	 * 获取方法的访问标志
	 *
	 * @return 访问标志整数（如 ACC_PUBLIC, ACC_STATIC 等）
	 */
	abstract public int getModifiers();

	/**
	 * 获取方法签名
	 *
	 * @return 方法的 Signature 对象，包含方法名和描述符
	 */
	/**
	 * 获取方法签名
	 *
	 * @return Signature 对象，包含方法名和描述符
	 */
	abstract public Signature getSignature();

	/**
	 * 获取异常类型数组
	 *
	 * @return 声明抛出的所有异常的 ASM Type 数组
	 */
	/**
	 * 获取方法声明的异常类型数组
	 *
	 * @return ASM Type 数组，表示所有声明的异常类型
	 */
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
