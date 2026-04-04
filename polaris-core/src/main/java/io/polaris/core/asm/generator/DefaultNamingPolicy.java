package io.polaris.core.asm.generator;

import java.util.Objects;
import java.util.function.Predicate;

import io.polaris.core.asm.internal.AsmConsts;
import io.polaris.core.hash.ArrayHash;
import io.polaris.core.string.Strings;

/**
 * 默认命名策略实现类
 * <p>根据包名、基础类名和键值生成唯一的类名，支持处理重复名称的情况</p>
 *
 * @author Qt
 * @since May 10, 2024
 */
public class DefaultNamingPolicy implements NamingPolicy {
	/** 单例实例 */
	public static final DefaultNamingPolicy INSTANCE = new DefaultNamingPolicy();
	/** 类名标签标识 */
	protected final String tag;

	/**
	 * 默认构造函数，使用默认标签
	 */
	public DefaultNamingPolicy() {
		this(AsmConsts.CLASS_TAG_DEFAULT);
	}

	/**
	 * 带参数的构造函数
	 *
	 * @param tag 类名标签标识
	 */
	public DefaultNamingPolicy(String tag) {
		this.tag = Strings.coalesce(tag, AsmConsts.CLASS_TAG_DEFAULT);
	}

	@Override
	public String getClassName(String packageName, String baseName, Object key, Predicate<String> dupChecker) {
		// 处理包名：去除首尾空格，为空时使用默认包名
		packageName = Strings.trimToNull(packageName);

		if (packageName == null) {
			packageName = "io.polaris.core.asm";
		} else if (packageName.equals("java")) {
			// 避免使用 java. 包名，改为 javax.
			packageName = "javax";
		} else if (packageName.startsWith("java.")) {
			// 避免使用 java. 开头的包名，改为 javax. 开头
			packageName = "javax." + packageName.substring(5);
		}
		// 处理基础类名：去除首尾空格和包路径，只保留简单类名
		baseName = Strings.trimToNull(baseName);
		if (baseName == null) {
			baseName = "Object";
		} else {
			int i = baseName.lastIndexOf('.');
			if (i != -1) {
				baseName = baseName.substring(i + 1);
			}
		}
		// 构建基础类名：包名.基础名$$标签
		StringBuilder sb = new StringBuilder().append(packageName).append(".").append(baseName).append(AsmConsts.CLASS_TAG_SEPARATOR).append(tag);
		if (key != null) {
			// 如果提供了键值，追加哈希值到类名中
			sb.append("$").append(Integer.toHexString(ArrayHash.hash(key)));
		}
		int baseLength = sb.length();
		String attempt = sb.toString();
		int index = 1;
		// 检查类名是否重复，如重复则追加序号直到找到不重复的名称
		while (dupChecker.test(attempt)) {
			sb.setLength(baseLength);
			attempt = sb.append('$').append(index++).toString();
		}
		return attempt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DefaultNamingPolicy that = (DefaultNamingPolicy) o;
		return Objects.equals(tag, that.tag);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(tag);
	}

}
