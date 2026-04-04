package io.polaris.core.asm.internal;

import org.objectweb.asm.Label;

/**
 * 代码块，表示一段有起始和结束标签的字节码范围
 * <p>
 * 用于异常处理和流程控制
 *
 * @author Qt
 * @since May 10, 2024
 */
public class Block {
	/** 代码发射器 */
	private CodeEmitter e;
	/** 代码块起始标签 */
	private Label start;
	/** 代码块结束标签 */
	private Label end;

	/**
	 * 构造代码块
	 *
	 * @param e 代码发射器
	 */
	public Block(CodeEmitter e) {
		this.e = e;
		start = e.mark();
	}

	/**
	 * 获取代码发射器
	 *
	 * @return 代码发射器实例
	 */
	public CodeEmitter getCodeEmitter() {
		return e;
	}

	/**
	 * 结束代码块，设置结束标签
	 *
	 * @throws IllegalStateException 如果结束标签已经设置过
	 */
	public void end() {
		if (end != null) {
			throw new IllegalStateException("end of label already set");
		}
		end = e.mark();
	}

	/**
	 * 获取代码块起始标签
	 *
	 * @return 起始标签
	 */
	public Label getStart() {
		return start;
	}

	/**
	 * 获取代码块结束标签
	 *
	 * @return 结束标签
	 */
	public Label getEnd() {
		return end;
	}
}
