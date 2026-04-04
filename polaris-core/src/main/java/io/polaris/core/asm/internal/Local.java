package io.polaris.core.asm.internal;

import org.objectweb.asm.Type;

/**
 * 局部变量，封装 ASM 局部变量的类型和索引信息
 *
 * @author Qt
 * @since May 10, 2024
 */
public class Local {
	/** 局部变量类型 */
	private Type type;
	/** 局部变量索引 */
	private int index;

	/**
	 * 构造局部变量
	 *
	 * @param index 局部变量索引
	 * @param type 局部变量类型
	 */
	public Local(int index, Type type) {
		this.type = type;
		this.index = index;
	}

	/**
	 * 获取局部变量索引
	 *
	 * @return 局部变量在栈帧中的索引位置
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * 获取局部变量类型
	 *
	 * @return 局部变量的 ASM Type 对象
	 */
	public Type getType() {
		return type;
	}
}
