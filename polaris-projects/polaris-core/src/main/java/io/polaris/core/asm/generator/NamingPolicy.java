package io.polaris.core.asm.generator;

import java.util.function.Predicate;

/**
 * @author Qt
 * @since May 10, 2024
 */
public interface NamingPolicy {
	/**
	 * 生成类全名
	 *
	 * @param packageName 期望包名
	 * @param baseName    期望基础类名（不含包名）
	 * @param key         生成类的标识键值，可为null，不为null时，会参与生成类名的计算
	 * @param dupChecker  判断是否已使用的重复类名
	 * @return 生成的类全名
	 */
	String getClassName(String packageName, String baseName, Object key, Predicate<String> dupChecker);
}
