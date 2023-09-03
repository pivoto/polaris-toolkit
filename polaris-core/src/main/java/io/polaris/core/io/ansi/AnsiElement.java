package io.polaris.core.io.ansi;

/**
 * ANSI可转义节点接口，实现为ANSI颜色等
 *
 * @author Phillip Webb
 */
public interface AnsiElement {

	/**
	 * @return ANSI转义编码
	 */
	@Override
	String toString();

	/**
	 * 获取ANSI代码，默认返回-1
	 * @return ANSI代码
	 */
	default int getCode(){
		return -1;
	}
}
