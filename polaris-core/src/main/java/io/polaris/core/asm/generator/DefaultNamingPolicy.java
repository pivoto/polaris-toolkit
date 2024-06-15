package io.polaris.core.asm.generator;

import java.util.Objects;
import java.util.function.Predicate;

import io.polaris.core.asm.internal.AsmConsts;
import io.polaris.core.hash.ArrayHash;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since May 10, 2024
 */
public class DefaultNamingPolicy implements NamingPolicy {
	public static final DefaultNamingPolicy INSTANCE = new DefaultNamingPolicy();
	protected final String tag;

	public DefaultNamingPolicy() {
		this(AsmConsts.CLASS_TAG_DEFAULT);
	}

	public DefaultNamingPolicy(String tag) {
		this.tag = Strings.coalesce(tag, AsmConsts.CLASS_TAG_DEFAULT);
	}

	@Override
	public String getClassName(String packageName, String baseName, Object key, Predicate<String> dupChecker) {
		packageName = Strings.trimToNull(packageName);

		if (packageName == null) {
			packageName = "io.polaris.core.asm";
		} else if (packageName.equals("java")) {
			packageName = "javax";
		} else if (packageName.startsWith("java.")) {
			packageName = "javax." + packageName.substring(5);
		}
		baseName = Strings.trimToNull(baseName);
		if (baseName == null) {
			baseName = "Object";
		} else {
			int i = baseName.lastIndexOf('.');
			if (i != -1) {
				baseName = baseName.substring(i + 1);
			}
		}
		StringBuilder sb = new StringBuilder().append(packageName).append(".").append(baseName).append(AsmConsts.CLASS_TAG_SEPARATOR).append(tag);
		if (key != null) {
			sb.append("$").append(Integer.toHexString(ArrayHash.hash(key)));
		}
		int baseLength = sb.length();
		String attempt = sb.toString();
		int index = 1;
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
