package io.polaris.core.asm.internal;

import org.objectweb.asm.Type;

/**
 * 方法信息抽象类，封装 ASM 方法的基本信息
 * <p>
 * 包含方法所属类、访问标志、签名和异常类型
 *
 * @author Qt
 * @since Apr 09, 2026
 */
public interface MethodDescriptor {

	/**
	 * 获取方法所属类的信息
	 *
	 * @return 声明该方法的类的描述对象
	 */
	ClassDescriptor getClassDescriptor();

	/**
	 * 获取方法签名
	 *
	 * @return 方法的 Signature 对象，包含方法名和描述符
	 */
	Signature getSignature();

	/**
	 * 获取方法声明的异常类型数组
	 *
	 * @return ASM Type 数组，表示所有声明的异常类型
	 */
	Type[] getExceptionTypes();


	/**
	 * 获取访问标志
	 *
	 * @return ASM 访问标志位（如 ACC_PUBLIC、ACC_FINAL 等）
	 */
	int getModifiers();

	/**
	 * 获取访问标志
	 *
	 * @return ASM 访问标志位（如 ACC_PUBLIC、ACC_FINAL 等）
	 */
	default int getAccess() {
		return getModifiers();
	}

}
