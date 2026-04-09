package io.polaris.core.asm.internal;

import org.objectweb.asm.Type;

/**
 * 类信息抽象类，封装 ASM 类的基本信息
 * <p>
 * 包含类类型、父类类型、接口列表和访问标志
 *
 * @author Qt
 * @since Apr 09, 2026
 */
public interface ClassDescriptor {


	/**
	 * 获取类的 Type 对象
	 *
	 * @return 当前类的 ASM Type
	 */
	Type getType();

	/**
	 * 获取父类的 Type 对象
	 *
	 * @return 父类的 ASM Type，如果是 Object 则返回 null
	 */
	Type getSuperType();

	/**
	 * 获取实现的接口数组
	 *
	 * @return 所有实现接口的 ASM Type 数组
	 */
	Type[] getInterfaces();

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
