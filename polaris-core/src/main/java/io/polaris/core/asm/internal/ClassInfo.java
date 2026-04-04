package io.polaris.core.asm.internal;

import org.objectweb.asm.Type;

/**
 * 类信息抽象类，封装 ASM 类的基本信息
 * <p>
 * 包含类类型、父类类型、接口列表和访问标志
 *
 * @author Qt
 * @since May 09, 2024
 */
public abstract class ClassInfo {
	/**
	 * 受保护构造器，防止外部直接实例化
	 */
	protected ClassInfo() {
	}

	/**
	 * 获取类的 Type 对象
	 *
	 * @return 当前类的 ASM Type
	 */
	/**
	 * 获取当前类的类型
	 *
	 * @return ASM Type 对象，表示当前类
	 */
	abstract public Type getType();

	/**
	 * 获取父类的 Type 对象
	 *
	 * @return 父类的 ASM Type，如果是 Object 则返回 null
	 */
	/**
	 * 获取父类的类型
	 *
	 * @return ASM Type 对象，表示父类；如果是 Object 或接口则可能为 null
	 */
	abstract public Type getSuperType();

	/**
	 * 获取实现的接口数组
	 *
	 * @return 所有实现接口的 ASM Type 数组
	 */
	/**
	 * 获取实现的接口数组
	 *
	 * @return ASM Type 数组，表示所有实现的接口
	 */
	abstract public Type[] getInterfaces();

	/**
	 * 获取访问标志
	 *
	 * @return ASM 访问标志位（如 ACC_PUBLIC、ACC_FINAL 等）
	 */
	/**
	 * 获取类的访问标志
	 *
	 * @return 访问标志整数（如 ACC_PUBLIC, ACC_FINAL 等）
	 */
	abstract public int getModifiers();

	/**
	 * 判断是否相等
	 *
	 * @param o 待比较的对象
	 * @return 如果类型相同则返回 true
	 */
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof ClassInfo))
			return false;
		return getType().equals(((ClassInfo) o).getType());
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
