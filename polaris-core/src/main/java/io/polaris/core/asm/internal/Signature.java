package io.polaris.core.asm.internal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.objectweb.asm.Type;

/**
 * 方法签名类，封装方法名和描述符
 * <p>
 * 用于唯一标识一个方法的签名信息
 *
 * @author Qt
 * @since May 09, 2024
 */
@EqualsAndHashCode
@Getter
public class Signature {
	/** 方法名 */
	private final String name;
	/** 方法描述符 */
	private final String descriptor;

	/**
	 * 构造方法签名
	 *
	 * @param name 方法名称
	 * @param descriptor 方法描述符（JVM 格式）
	 */
	public Signature(String name, String descriptor) {
		this.name = name;
		this.descriptor = descriptor;
	}

	/**
	 * 构造方法签名（通过返回参数类型）
	 *
	 * @param name 方法名
	 * @param returnType 返回值类型
	 * @param argumentTypes 参数类型数组
	 */
	public Signature(String name, Type returnType, Type[] argumentTypes) {
		this(name, Type.getMethodDescriptor(returnType, argumentTypes));
	}

	/**
	 * 获取返回值类型
	 *
	 * @return ASM Type 对象，表示返回值类型
	 */
	public Type getReturnType() {
		return Type.getReturnType(descriptor);
	}

	/**
	 * 获取参数类型数组
	 *
	 * @return ASM Type 数组，表示所有参数类型
	 */
	public Type[] getArgumentTypes() {
		return Type.getArgumentTypes(descriptor);
	}

	/**
	 * 转换为字符串
	 *
	 * @return 方法名 + 方法描述符的组合
	 */
	public String toString() {
		return name + descriptor;
	}


}
