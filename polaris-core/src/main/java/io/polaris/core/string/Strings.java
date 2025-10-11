package io.polaris.core.string;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.collection.Iterables;
import io.polaris.core.collection.PrimitiveArrays;
import io.polaris.core.consts.StdConsts;
import io.polaris.core.lang.primitive.Booleans;
import io.polaris.core.lang.primitive.Chars;
import io.polaris.core.map.Maps;
import io.polaris.core.regex.Patterns;
import io.polaris.core.tuple.Ref;
import io.polaris.core.tuple.ValueRef;
import io.polaris.core.ulid.UlidCreator;

/**
 * @author Qt
 * @since 1.8
 */
public class Strings {

	private static final ThreadLocal<Map<String, Ref<String>>> resolvedKeysLocal = new ThreadLocal<>();
	private static final Pattern patternPlaceholder = Patterns.getPattern("\\$\\{([\\w\\.\\-]+)(?:(:-?)([^${}]*))?\\}");
	/// /private static final String patternPlaceholderSeparator = "\\Q:\\E";
	private static final Pattern patternDigits = Patterns.getPattern("(?<!\\\\)\\{(\\d+)\\}");
	private static final Pattern patternEmpty = Patterns.getPattern("(?<!\\\\)\\{\\}");

	/**
	 * 字节大小值转为带单位的可读字符串
	 */
	public static String toReadableByteSizeStr(long byteSize) {
		long b = byteSize % 1024;
		long k = byteSize / 1024;
		if (k == 0) {
			return b + "B";
		}
		long m = k / 1024;
		k = k % 1024;
		if (m == 0) {
			return k + "K " + (b == 0 ? "" : (b + "B"));
		}
		long g = m / 1024;
		m = m % 1024;
		if (g == 0) {
			return m + "M " + (k == 0 ? "" : (k + "K ")) + (b == 0 ? "" : (b + "B"));
		}
		long t = g / 1024;
		g = g % 1024;
		return (t == 0 ? "" : (t + "T ")) + (g == 0 ? "" : (g + "G ")) + (m == 0 ? "" : (m + "M ")) + (k == 0 ? "" : (k + "K ")) + (b == 0 ? "" : (b + "B"));
	}

	public static String truncate(String str, int maxLength) {
		if (str == null) {
			return null;
		}
		str = str.trim();
		if (str.length() <= maxLength) {
			return str;
		}
		return str.substring(0, maxLength);
	}

	public static String truncateUtf8(String str, int maxLength) {
		return truncate(str, maxLength, StandardCharsets.UTF_8);
	}

	public static String truncateGbk(String str, int maxLength) {
		return truncate(str, maxLength, "GBK");
	}

	public static String truncateGb18030(String str, int maxLength) {
		return truncate(str, maxLength, "GB18030");
	}

	public static String truncateAscii(String str, int maxLength) {
		return truncate(str, maxLength, StandardCharsets.US_ASCII);
	}

	public static String truncateIso8859_1(String str, int maxLength) {
		return truncate(str, maxLength, StandardCharsets.ISO_8859_1);
	}

	public static String truncate(String str, int maxLength, Charset charset) {
		if (str == null) {
			return null;
		}
		str = str.trim();
		if (str.length() <= (maxLength / 3)) {
			return str;
		}
		byte[] bytes = str.getBytes(charset);
		if (bytes.length > maxLength) {
			str = new String(bytes, 0, maxLength, charset);
		}
		return str;
	}

	public static String truncate(String str, int maxLength, String charset) {
		return truncate(str, maxLength, Charset.forName(charset));
	}

	public static String padStart(String str, int minLength, char pad) {
		str = coalesce(str, "");
		if (str.length() >= minLength) {
			return str;
		}
		StringBuilder sb = new StringBuilder(minLength);
		for (int i = str.length(); i < minLength; i++) {
			sb.append(pad);
		}
		sb.append(str);
		return sb.toString();
	}

	public static String padEnd(String str, int minLength, char pad) {
		str = coalesce(str, "");
		if (str.length() >= minLength) {
			return str;
		}
		StringBuilder sb = new StringBuilder(minLength);
		sb.append(str);
		for (int i = str.length(); i < minLength; i++) {
			sb.append(pad);
		}
		return sb.toString();
	}

	public static String reverse(String str) {
		return new String(Chars.reverse(str.toCharArray()));
	}

	public static String repeat(char ch, int count) {
		char[] chars = new char[count];
		Arrays.fill(chars, ch);
		return new String(chars);
	}

	public static String repeat(String str, int count) {
		if (str == null) {
			return null;
		}
		if (count <= 0) {
			return "";
		}
		if (count == 1) {
			return str;
		}

		final int len = str.length();
		final long longSize = (long) len * (long) count;
		final int size = (int) longSize;
		if (size != longSize) {
			throw new ArrayIndexOutOfBoundsException("Required array size too large: " + longSize);
		}

		final char[] array = new char[size];
		str.getChars(0, len, array, 0);
		int n;
		for (n = len; n < size - n; n <<= 1) {
			System.arraycopy(array, 0, array, n, n);
		}
		System.arraycopy(array, 0, array, n, size - n);
		return new String(array);
	}

	/**
	 * @see #coalesce(String...)
	 */
	public static <T> T nvl(T o, T reo) {
		if (isEmpty(o)) {
			return reo;
		} else {
			return o;
		}
	}

	/**
	 * @see #coalesceBlank(String[])
	 */
	public static String coalesce(String... args) {
		return coalesceBlank(args);
	}


	public static String coalesceNull(String... args) {
		String v = null;
		for (String arg : args) {
			v = arg;
			if (v != null) {
				break;
			}
		}
		return v;
	}

	public static String coalesceEmpty(String... args) {
		String v = null;
		for (String arg : args) {
			v = arg;
			if (Strings.isNotEmpty(v)) {
				break;
			}
		}
		return v;
	}

	public static String coalesceBlank(String... args) {
		String v = null;
		for (String arg : args) {
			v = arg;
			if (Strings.isNotBlank(v)) {
				break;
			}
		}
		return v;
	}

	public static String uuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static String ulid() {
		return UlidCreator.getUlid().toString();
	}

	/**
	 * 标准化标识符，非法字符替换为下划线
	 */
	public static String normalize(String name) {
		return name == null ? "" : name.trim().replaceAll("\\W", "_");
	}

	/**
	 * @see java.beans.Introspector#decapitalize(String)
	 */
	public static String decapitalize(String name) {
		return StringCases.decapitalize(name);
	}

	public static String capitalize(String name) {
		return StringCases.capitalize(name);
	}

	public static String getExistedEnv(String... keys) {
		for (String key : keys) {
			String val = getEnv(key);
			if (val != null) {
				return val;
			}
		}
		return null;
	}

	public static String getEnv(String key) {
		return System.getenv(key);
	}

	public static String getExistedSystemProperty(String... keys) {
		for (String key : keys) {
			String val = getSystemProperty(key);
			if (val != null) {
				return val;
			}
		}
		return null;
	}


	/**
	 * 获取系统属性或环境变量
	 */
	public static String getSystemProperty(String key) {
		String val = System.getProperty(key);
		if (val != null) {
			return resolvePlaceholders(val, Strings::getSystemProperty);
		}
		val = System.getenv(key);
		if (val != null) {
			return resolvePlaceholders(val, Strings::getSystemProperty);
		}
		{
			final int len = key.length();
			StringBuilder sb = new StringBuilder(len + 16);
			sb.append(Character.toUpperCase(key.charAt(0)));
			for (int i = 1; i < len; i++) {
				final char c = key.charAt(i);
				if (c == '.') {
					sb.append("_");
				} else if (Character.isUpperCase(c)) {
					sb.append("_").append(c);
				} else {
					sb.append(Character.toUpperCase(c));
				}
			}
			String envKey = sb.toString();
			val = System.getenv(envKey);
		}
		if (val != null) {
			return resolvePlaceholders(val, Strings::getSystemProperty);
		}
		return null;
	}


	/**
	 * 字符串占位符替换，占位符：`${xxx.yyy.xxx}`或`${xxx.yyy.xxx:123}`
	 *
	 * @param origin
	 * @param getter
	 * @return
	 */
	public static String resolvePlaceholders(String origin, Function<String, String> getter) {
		return resolvePlaceholders(origin, patternPlaceholder, getter);
	}

	public static String resolvePlaceholders(String origin, Function<String, String> getter, boolean defaultAsEmpty) {
		return resolvePlaceholders(origin, patternPlaceholder, getter, defaultAsEmpty);
	}

	public static String resolvePlaceholders(String origin, Pattern placeholderPattern, Function<String, String> getter) {
		return resolvePlaceholders(origin, placeholderPattern, getter, true);
	}

	public static String resolvePlaceholders(String origin, Pattern placeholderPattern, Function<String, String> getter, boolean defaultAsEmpty) {
		if (origin == null) {
			return origin;
		}
		boolean hasInit = false;
		Map<String, Ref<String>> resovedKeys = resolvedKeysLocal.get();
		if (resovedKeys == null) {
			hasInit = true;
			resolvedKeysLocal.set(resovedKeys = new HashMap<>());
		}
		try {
			Matcher matcher = placeholderPattern.matcher(origin);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				String group = matcher.group();
				if (resovedKeys.containsKey(group)) {
					matcher.appendReplacement(sb, resovedKeys.get(group).get());
					continue;
				}
				String k = matcher.group(1);
				int groupCount = matcher.groupCount();
				String separator = groupCount >= 3 ? matcher.group(2) : ":";
				String defVal = groupCount >= 3 ? matcher.group(3) : groupCount == 2 ? matcher.group(2) : null;
				String v = null;
				if (resovedKeys.containsKey(k)) {
					v = resovedKeys.get(k).get();
				} else {
					// 防止递归死循环
					resovedKeys.put(k, ValueRef.of(null));
					v = getter.apply(k);
					resovedKeys.put(k, new ValueRef<>(v));
				}
				// `:`替换null, `:-`替换空字符串
				if (defVal != null) {
					if (v == null || v.isEmpty() && separator != null && separator.length() > 1) {
						v = defVal;
					}
				}

				String replacement;
				if (v == null) {
					replacement = defaultAsEmpty ? "" : group;
				} else {
					replacement = v;
				}
				replacement = replacement.replace("\\", "\\\\").replace("$", "\\$");
				resovedKeys.put(group, new ValueRef<>(replacement));
				matcher.appendReplacement(sb, replacement);
			}
			matcher.appendTail(sb);
			String rs = sb.toString();

			boolean hasNextMatcher = false;
			Matcher nextMatcher = placeholderPattern.matcher(rs);
			while (nextMatcher.find()) {
				String group = nextMatcher.group();
				if (!resovedKeys.containsKey(group)) {
					hasNextMatcher = true;
					break;
				}
			}
			if (hasNextMatcher) {
				rs = resolvePlaceholders(rs, placeholderPattern, getter, defaultAsEmpty);
			}
			return rs;
		} finally {
			if (hasInit) {
				resolvedKeysLocal.get().clear();
				resolvedKeysLocal.remove();
			}
		}
	}

	public static String filter(CharSequence str, Predicate<Character> filter) {
		if (str == null || filter == null) {
			return Objects.toString(str, "");
		}
		int len = str.length();
		StringBuilder sb = new StringBuilder(len);
		char c;
		for (int i = 0; i < len; i++) {
			c = str.charAt(i);
			if (filter.test(c)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String cleanBlank(CharSequence str) {
		return filter(str, c -> !Chars.isBlankChar(c));
	}

	public static String addPrefix(CharSequence str, CharSequence prefix) {
		if (isEmpty(str)) {
			return Objects.toString(prefix, "");
		}
		if (isEmpty(prefix)) {
			return Objects.toString(str, "");
		}
		return new StringBuilder(str.length() + prefix.length()).append(prefix).append(str).toString();
	}

	public static String addSuffix(CharSequence str, CharSequence suffix) {
		if (isEmpty(str)) {
			return Objects.toString(suffix, "");
		}
		if (isEmpty(suffix)) {
			return Objects.toString(str, "");
		}
		return new StringBuilder(str.length() + suffix.length()).append(str).append(suffix).toString();
	}

	public static String removePrefix(CharSequence str, CharSequence prefix) {
		if (isEmpty(str) || isEmpty(prefix)) {
			return Objects.toString(str, "");
		}
		if (str.length() < prefix.length()) {
			return Objects.toString(str, "");
		}
		String str1 = str.toString();
		if (str1.startsWith(prefix.toString())) {
			return str1.substring(prefix.length());
		}
		return str1;
	}

	public static String removeSuffix(CharSequence str, CharSequence suffix) {
		if (isEmpty(str) || isEmpty(suffix)) {
			return Objects.toString(str, "");
		}
		if (str.length() < suffix.length()) {
			return Objects.toString(str, "");
		}
		String str1 = str.toString();
		if (str1.endsWith(suffix.toString())) {
			return str1.substring(0, str.length() - suffix.length());
		}
		return str1;
	}

	public static String trimToEmpty(String str) {
		if (str == null) {
			return "";
		}
		return trim(str);
	}

	public static String trimToNull(String str) {
		if (str != null) {
			str = trim(str);
			if (str.length() > 0) {
				return str;
			}
		}
		return null;
	}

	public static String trim(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		int begin = 0;
		int end = str.length() - 1;
		while (begin <= end && Character.isWhitespace(str.charAt(begin))) {
			begin++;
		}
		while (end > begin && Character.isWhitespace(str.charAt(end))) {
			end--;
		}
		return str.substring(begin, end + 1);
	}

	public static String trimStart(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		if (!Character.isWhitespace(str.charAt(0))) {
			return str;
		}
		int len = str.length();
		int start = 0;
		for (int i = 0; i < len; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				start = i;
				break;
			}
		}
		return str.substring(start);
	}

	public static String trimEnd(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		if (!Character.isWhitespace(str.charAt(str.length() - 1))) {
			return str;
		}
		int end = str.length();
		for (int i = end; i > 0; i--) {
			if (!Character.isWhitespace(str.charAt(i - 1))) {
				end = i;
				break;
			}
		}
		return str.substring(0, end);
	}

	public static String trim(String str, char ch) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		int begin = 0;
		int end = str.length() - 1;
		while (begin <= end && ch == str.charAt(begin)) {
			begin++;
		}
		while (end > begin && ch == str.charAt(end)) {
			end--;
		}
		return str.substring(begin, end + 1);
	}

	public static String trimStart(String str, char ch) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		if (ch != (str.charAt(0))) {
			return str;
		}
		int len = str.length();
		int start = 0;
		for (int i = 0; i < len; i++) {
			if (ch != str.charAt(i)) {
				start = i;
				break;
			}
		}
		return str.substring(start);
	}

	public static String trimEnd(String str, char ch) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		if (ch != (str.charAt(str.length() - 1))) {
			return str;
		}
		int end = str.length();
		for (int i = end; i > 0; i--) {
			if (ch != str.charAt(i - 1)) {
				end = i;
				break;
			}
		}
		return str.substring(0, end);
	}

	public static boolean isEmpty(Object o) {
		if (o == null) {
			return true;
		}
		if (o instanceof CharSequence) {
			return ((CharSequence) o).length() == 0;
		}
		return false;
	}

	public static boolean isNotEmpty(Object o) {
		return !isEmpty(o);
	}

	public static boolean isNone(Object o) {
		if (o == null) {
			return true;
		}
		if (o instanceof CharSequence) {
			return isNone((CharSequence) o);
		}
		return false;
	}

	public static boolean isNotNone(Object o) {
		return !isNone(o);
	}

	public static boolean isBlank(Object o) {
		if (o == null) {
			return true;
		}
		if (o instanceof CharSequence) {
			return isBlank((CharSequence) o);
		}
		return false;
	}

	public static boolean isNotBlank(Object o) {
		return !isBlank(o);
	}

	public static boolean isNone(CharSequence cs) {
		return isBlank(cs) || "null".equals(cs.toString());
	}

	public static boolean isNotNone(CharSequence str) {
		return !isNone(str);
	}

	public static boolean isBlank(CharSequence str) {
		if (isEmpty(str)) {
			return true;
		}
		for (int i = str.length() - 1; i >= 0; i--) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(CharSequence str) {
		return !isBlank(str);
	}

	public static boolean isEmpty(final CharSequence str) {
		return str == null || str.length() == 0;
	}

	public static boolean isNotEmpty(CharSequence str) {
		return !isEmpty(str);
	}


	/**
	 * 判断是否至少有一个字符串为空，数组为空时返回false
	 */
	public static boolean isAnyNone(CharSequence... strs) {
		return isAnyMatched(Strings::isNone, strs);
	}

	/**
	 * 判断是否至少有一个字符串不为空，数组为空时返回false
	 */
	public static boolean isAnyNotNone(CharSequence... strs) {
		return isAnyMatched(Strings::isNotNone, strs);
	}

	/**
	 * 判断是否所有字符串都为空，数组为空时返回true
	 */
	public static boolean isAllNone(CharSequence... strs) {
		return isAllMatched(Strings::isNone, strs);
	}

	/**
	 * 判断是否所有字符串都不为空，数组为空时返回true
	 */
	public static boolean isAllNotNone(CharSequence... strs) {
		return isAllNotMatched(Strings::isNone, strs);
	}

	/**
	 * 判断是否至少有一个字符串为空，数组为空时返回false
	 */
	public static boolean isAnyBlank(CharSequence... strs) {
		return isAnyMatched(Strings::isBlank, strs);
	}

	/**
	 * 判断是否至少有一个字符串不为空，数组为空时返回false
	 */
	public static boolean isAnyNotBlank(CharSequence... strs) {
		return isAnyMatched(Strings::isNotBlank, strs);
	}

	/**
	 * 判断是否所有字符串都为空，数组为空时返回true
	 */
	public static boolean isAllBlank(CharSequence... strs) {
		return isAllMatched(Strings::isBlank, strs);
	}

	/**
	 * 判断是否所有字符串都不为空，数组为空时返回true
	 */
	public static boolean isAllNotBlank(CharSequence... strs) {
		return isAllNotMatched(Strings::isBlank, strs);
	}

	/**
	 * 判断是否至少有一个字符串为空，数组为空时返回false
	 */
	public static boolean isAnyEmpty(CharSequence... strs) {
		return isAnyMatched(Strings::isEmpty, strs);
	}

	/**
	 * 判断是否至少有一个字符串不为空，数组为空时返回false
	 */
	public static boolean isAnyNotEmpty(CharSequence... strs) {
		return isAnyMatched(Strings::isNotEmpty, strs);
	}

	/**
	 * 判断是否所有字符串都为空，数组为空时返回true
	 */
	public static boolean isAllEmpty(CharSequence... strs) {
		return isAllMatched(Strings::isEmpty, strs);
	}

	/**
	 * 判断是否所有字符串都不为空，数组为空时返回true
	 */
	public static boolean isAllNotEmpty(CharSequence... strs) {
		return isAllNotMatched(Strings::isEmpty, strs);
	}

	/**
	 * 判断是否至少有一个字符串为空，数组为空时返回false
	 */
	public static boolean isAnyMatched(Predicate<CharSequence> predicate, CharSequence... strs) {
		if (strs != null) {
			for (CharSequence str : strs) {
				if (predicate.test(str)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断是否至少有一个字符串不为空，数组为空时返回false
	 */
	public static boolean isAnyNotMatched(Predicate<CharSequence> predicate, CharSequence... strs) {
		if (strs != null) {
			for (CharSequence str : strs) {
				if (!predicate.test(str)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断是否所有字符串都匹配，数组为空时返回true
	 */
	public static boolean isAllMatched(Predicate<CharSequence> predicate, CharSequence... strs) {
		if (strs != null) {
			for (CharSequence str : strs) {
				if (!predicate.test(str)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 判断是否所有字符串都不匹配，数组为空时返回true
	 */
	public static boolean isAllNotMatched(Predicate<CharSequence> predicate, CharSequence... strs) {
		if (strs != null) {
			for (CharSequence str : strs) {
				if (predicate.test(str)) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean containsAny(CharSequence str, char... testChars) {
		if (!isEmpty(str)) {
			int len = str.length();
			for (int i = 0; i < len; i++) {
				if (PrimitiveArrays.contains(testChars, str.charAt(i))) {
					return true;
				}
			}
		}
		return false;
	}


	public static String getContainsStr(CharSequence str, CharSequence... testStrs) {
		if (str == null || testStrs.length == 0) {
			return null;
		}
		String s = str.toString();
		for (CharSequence checkStr : testStrs) {
			if (s.contains(checkStr)) {
				return checkStr.toString();
			}
		}
		return null;
	}

	public static boolean containsAny(CharSequence str, CharSequence... testStrs) {
		if (str == null || testStrs.length == 0) {
			return false;
		}
		String s = str.toString();
		for (CharSequence checkStr : testStrs) {
			if (s.contains(checkStr)) {
				return true;
			}
		}
		return false;
	}

	public static boolean containsAnyIgnoreCase(CharSequence str, CharSequence... testStrs) {
		if (str == null || testStrs.length == 0) {
			return false;
		}
		String s = str.toString();
		for (CharSequence checkStr : testStrs) {
			if (containsIgnoreCase(s, checkStr)) {
				return true;
			}
		}
		return false;
	}

	public static boolean contains(CharSequence str, CharSequence testStr) {
		if (str == null) {
			return false;
		}
		if (testStr == null || testStr.length() == 0) {
			return true;
		}
		if (str.length() == 0) {
			return false;
		}
		return str.toString().contains(testStr);
	}

	public static boolean containsIgnoreCase(CharSequence str, CharSequence testStr) {
		if (str == null) {
			return false;
		}
		if (testStr == null || testStr.length() == 0) {
			return true;
		}
		if (str.length() == 0) {
			return false;
		}
		return indexOfIgnoreCase(str, testStr) >= 0;
	}

	public static boolean startsWithAny(CharSequence str, CharSequence... testStrs) {
		if (str == null || testStrs.length == 0) {
			return false;
		}
		String s = str.toString();
		for (CharSequence checkStr : testStrs) {
			if (startsWith(s, checkStr)) {
				return true;
			}
		}
		return false;
	}

	public static boolean startsWith(CharSequence str, CharSequence prefix) {
		if (str == null) {
			return false;
		}
		if (prefix == null || prefix.length() == 0) {
			return true;
		}
		if (str.length() == 0) {
			return false;
		}
		return str.toString().startsWith(prefix.toString());
	}

	public static boolean endsWithAny(CharSequence str, CharSequence... testStrs) {
		if (str == null || testStrs.length == 0) {
			return false;
		}
		String s = str.toString();
		for (CharSequence checkStr : testStrs) {
			if (endsWith(s, checkStr)) {
				return true;
			}
		}
		return false;
	}

	public static boolean endsWith(CharSequence str, CharSequence suffix) {
		if (str == null) {
			return false;
		}
		if (suffix == null || suffix.length() == 0) {
			return true;
		}
		if (str.length() == 0) {
			return false;
		}
		return str.toString().endsWith(suffix.toString());
	}


	public static boolean startsWithAnyIgnoreCase(CharSequence str, CharSequence... testStrs) {
		if (str == null || testStrs.length == 0) {
			return false;
		}
		String s = str.toString();
		for (CharSequence checkStr : testStrs) {
			if (startsWithIgnoreCase(s, checkStr)) {
				return true;
			}
		}
		return false;
	}

	public static boolean startsWithIgnoreCase(CharSequence str, CharSequence prefix) {
		if (str == null) {
			return false;
		}
		if (prefix == null || prefix.length() == 0) {
			return true;
		}
		if (str.length() == 0) {
			return false;
		}
		return str.toString().regionMatches(true, 0, prefix.toString(), 0, prefix.length());
	}

	public static boolean endsWithAnyIgnoreCase(CharSequence str, CharSequence... testStrs) {
		if (str == null || testStrs.length == 0) {
			return false;
		}
		String s = str.toString();
		for (CharSequence checkStr : testStrs) {
			if (endsWithIgnoreCase(s, checkStr)) {
				return true;
			}
		}
		return false;
	}

	public static boolean endsWithIgnoreCase(CharSequence str, CharSequence suffix) {
		if (str == null) {
			return false;
		}
		if (suffix == null || suffix.length() == 0) {
			return true;
		}
		if (str.length() == 0) {
			return false;
		}
		return str.toString().regionMatches(true, str.length() - suffix.length(), suffix.toString(), 0, suffix.length());
	}

	public static int indexOf(@Nonnull CharSequence source, @Nonnull CharSequence target) {
		return source.toString().indexOf(target.toString());
	}

	public static int indexOf(@Nonnull CharSequence source, @Nonnull CharSequence target, int fromIndex) {
		return source.toString().indexOf(target.toString(), fromIndex);
	}

	public static int indexOfIgnoreCase(@Nonnull CharSequence source, @Nonnull CharSequence target) {
		return indexOfIgnoreCase(source.toString().toCharArray(), 0, source.length(),
			target.toString().toCharArray(), 0, target.length(), 0);
	}

	public static int indexOfIgnoreCase(@Nonnull CharSequence source, @Nonnull CharSequence target, int fromIndex) {
		return indexOfIgnoreCase(source.toString().toCharArray(), 0, source.length(),
			target.toString().toCharArray(), 0, target.length(), fromIndex);
	}


	/**
	 * Case insensitive version of {@link String#indexOf(String, int)}.
	 */
	static int indexOfIgnoreCase(char[] source, int sourceOffset, int sourceCount
		, char[] target, int targetOffset, int targetCount, int fromIndex) {
		if (fromIndex >= sourceCount) {
			return (targetCount == 0 ? sourceCount : -1);
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		if (targetCount == 0) {
			return fromIndex;
		}

		char first = Character.toUpperCase(target[targetOffset]);
		int max = sourceOffset + (sourceCount - targetCount);

		for (int i = sourceOffset + fromIndex; i <= max; i++) {
			/* Look for first character. */
			if (Character.toUpperCase(source[i]) != first) {
				while (++i <= max && Character.toUpperCase(source[i]) != first) ;
			}

			/* Found first character, now look at the rest of v2 */
			if (i <= max) {
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = targetOffset + 1; j < end && Character.toUpperCase(source[j])
					== Character.toUpperCase(target[k]); j++, k++)
					;

				if (j == end) {
					/* Found whole string. */
					return i - sourceOffset;
				}
			}
		}
		return -1;
	}

	public static int lastIndexOf(@Nonnull CharSequence source, @Nonnull CharSequence target) {
		return source.toString().lastIndexOf(target.toString());
	}

	public static int lastIndexOf(@Nonnull CharSequence source, @Nonnull CharSequence target, int fromIndex) {
		return source.toString().lastIndexOf(target.toString(), fromIndex);
	}

	public static int lastIndexOfIgnoreCase(@Nonnull CharSequence source, @Nonnull CharSequence target) {
		return lastIndexOfIgnoreCase(source.toString().toCharArray(), 0, source.length(),
			target.toString().toCharArray(), 0, target.length(), source.length());
	}

	public static int lastIndexOfIgnoreCase(@Nonnull CharSequence source, @Nonnull CharSequence target, int fromIndex) {
		return lastIndexOfIgnoreCase(source.toString().toCharArray(), 0, source.length(),
			target.toString().toCharArray(), 0, target.length(), fromIndex);
	}

	/**
	 * Case insensitive version of {@link String#lastIndexOf(String, int)}.
	 */
	static int lastIndexOfIgnoreCase(char[] source, int sourceOffset, int sourceCount
		, char[] target, int targetOffset, int targetCount, int fromIndex) {
		int rightIndex = sourceCount - targetCount;
		if (fromIndex < 0) {
			return -1;
		}
		if (fromIndex > rightIndex) {
			fromIndex = rightIndex;
		}
		/* Empty string always matches. */
		if (targetCount == 0) {
			return fromIndex;
		}

		int strLastIndex = targetOffset + targetCount - 1;
		char strLastChar = Character.toUpperCase(target[strLastIndex]);
		int min = sourceOffset + targetCount - 1;
		int i = min + fromIndex;

		startSearchForLastChar:
		while (true) {
			while (i >= min && Character.toUpperCase(source[i]) != strLastChar) {
				i--;
			}
			if (i < min) {
				return -1;
			}
			int j = i - 1;
			int start = j - (targetCount - 1);
			int k = strLastIndex - 1;

			while (j > start) {
				if (Character.toUpperCase(source[j--]) != Character.toUpperCase(target[k--])) {
					i--;
					continue startSearchForLastChar;
				}
			}
			return start - sourceOffset + 1;
		}
	}


	public static int kmpIndexOf(@Nonnull CharSequence source, @Nonnull CharSequence target) {
		return KmpMatchers.indexOf(source, target);
	}

	public static int kmpIndexOf(@Nonnull CharSequence source, @Nonnull CharSequence target, int fromIndex) {
		return KmpMatchers.indexOf(source, target, fromIndex);
	}

	public static int kmpIndexOfIgnoreCase(@Nonnull CharSequence source, @Nonnull CharSequence target) {
		return KmpMatchers.indexOfIgnoreCase(source, target);
	}

	public static int kmpIndexOfIgnoreCase(@Nonnull CharSequence source, @Nonnull CharSequence target, int fromIndex) {
		return KmpMatchers.indexOfIgnoreCase(source, target, fromIndex);
	}

	public static int kmpLastIndexOf(@Nonnull CharSequence source, @Nonnull CharSequence target) {
		return KmpMatchers.lastIndexOf(source, target);
	}

	public static int kmpLastIndexOf(@Nonnull CharSequence source, @Nonnull CharSequence target, int fromIndex) {
		return KmpMatchers.lastIndexOf(source, target, fromIndex);
	}

	public static int kmpLastIndexOfIgnoreCase(@Nonnull CharSequence source, @Nonnull CharSequence target) {
		return KmpMatchers.lastIndexOfIgnoreCase(source, target);
	}

	public static int kmpLastIndexOfIgnoreCase(@Nonnull CharSequence source, @Nonnull CharSequence target, int fromIndex) {
		return KmpMatchers.lastIndexOfIgnoreCase(source, target, fromIndex);
	}


	public static boolean equalsAny(CharSequence str1, CharSequence... strs) {
		for (CharSequence str : strs) {
			if (equals(str1, str)) {
				return true;
			}
		}
		return false;
	}

	public static boolean equalsAnyIgnoreCase(CharSequence str1, CharSequence... strs) {
		for (CharSequence str : strs) {
			if (equalsIgnoreCase(str1, str)) {
				return true;
			}
		}
		return false;
	}

	public static boolean equals(CharSequence str1, CharSequence str2) {
		if (str1 == str2) {
			return true;
		}
		if (str1 == null || str2 == null) {
			return false;
		}
		if (str1.length() != str2.length()) {
			return false;
		}
		return str1.toString().equals(str2.toString());
	}

	public static boolean equalsIgnoreCase(CharSequence str1, CharSequence str2) {
		if (str1 == str2) {
			return true;
		}
		if (str1 == null || str2 == null) {
			return false;
		}
		if (str1.length() != str2.length()) {
			return false;
		}
		return str1.toString().equalsIgnoreCase(str2.toString());
	}

	public static boolean equalsIgnoreCase(char[] cs1, char[] cs2) {
		if (cs1 == cs2) {
			return true;
		}
		if (cs1 == null || cs2 == null) {
			return false;
		}
		int len = cs1.length;
		if (len != cs2.length) {
			return false;
		}
		for (int i = 0; i < len; i++) {
			if (Character.toUpperCase(cs1[i]) != Character.toUpperCase(cs2[i])) {
				return false;
			}
		}
		return true;
	}

	public static String getIfMatch(String str, String... args) {
		for (int i = 0; i + 1 < args.length; i += 2) {
			if (Patterns.matches(args[i], str)) {
				return args[i + 1];
			}
		}
		if ((args.length & 1) == 0) {
			return null;
		}
		return args[args.length - 1];
	}

	public static String getIfEquals(String str, String... args) {
		for (int i = 0; i + 1 < args.length; i += 2) {
			if (equals(args[i], str)) {
				return args[i + 1];
			}
		}
		if ((args.length & 1) == 0) {
			return null;
		}
		return args[args.length - 1];
	}

	public static CharSequence getIfEquals(CharSequence str, CharSequence... args) {
		for (int i = 0; i + 1 < args.length; i += 2) {
			if (equals(args[i], str)) {
				return args[i + 1];
			}
		}
		if ((args.length & 1) == 0) {
			return null;
		}
		return args[args.length - 1];
	}


	public static String getIfEqualsIgnoreCase(String str, String... args) {
		for (int i = 0; i + 1 < args.length; i += 2) {
			if (equalsIgnoreCase(args[i], str)) {
				return args[i + 1];
			}
		}
		if ((args.length & 1) == 0) {
			return null;
		}
		return args[args.length - 1];
	}

	public static CharSequence getIfEqualsIgnoreCase(CharSequence str, CharSequence... args) {
		for (int i = 0; i + 1 < args.length; i += 2) {
			if (equalsIgnoreCase(args[i], str)) {
				return args[i + 1];
			}
		}
		if ((args.length & 1) == 0) {
			return null;
		}
		return args[args.length - 1];
	}

	public static <T extends Collection<E>, E> T splitToCollection(Supplier<T> supplier, Function<String, E> converter, String str, String delimiterRegex) {
		return asCollection(supplier, converter, str.split(delimiterRegex));
	}

	public static <T extends Collection<String>> T splitToCollection(Supplier<T> supplier, String str, String delimiterRegex) {
		return asCollection(supplier, str.split(delimiterRegex));
	}

	public static <E> Set<E> splitToSet(String str, Function<String, E> converter, String delimiterRegex) {
		return asCollection(HashSet::new, converter, str.split(delimiterRegex));
	}

	public static Set<String> splitToSet(String str, String delimiterRegex) {
		return asCollection(HashSet::new, str.split(delimiterRegex));
	}

	public static <E> List<E> splitToList(String str, Function<String, E> converter, String delimiterRegex) {
		return asCollection(ArrayList::new, converter, str.split(delimiterRegex));
	}


	public static List<String> splitToList(String str, String delimiterRegex) {
		return asCollection(ArrayList::new, str.split(delimiterRegex));
	}

	public static <T extends Collection<E>, E> T splitToCollection(Supplier<T> supplier, Function<String, E> converter, String str) {
		return asCollection(supplier, converter, str.split(","));
	}

	public static <T extends Collection<String>> T splitToCollection(Supplier<T> supplier, String str) {
		return asCollection(supplier, str.split(","));
	}

	public static <E> Set<E> splitToSet(String str, Function<String, E> converter) {
		return asCollection(HashSet::new, converter, str.split(","));
	}

	public static Set<String> splitToSet(String str) {
		return asCollection(HashSet::new, str.split(","));
	}

	public static <E> List<E> splitToList(String str, Function<String, E> converter) {
		return asCollection(ArrayList::new, converter, str.split(","));
	}

	public static List<String> splitToList(String str) {
		return asCollection(ArrayList::new, str.split(","));
	}

	public static String[] tokenizeToArray(@Nullable String str, String delimiters) {
		return tokenizeToArray(str, delimiters, true, true);
	}

	public static String[] tokenizeToArray(
		@Nullable String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
		return tokenizeToArray(str, delimiters, trimTokens, ignoreEmptyTokens, null);
	}

	public static String[] tokenizeToArray(
		@Nullable String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens, Function<String, String> converter) {
		if (str == null) {
			return StdConsts.EMPTY_STRING_ARRAY;
		}
		StringTokenizer st = new StringTokenizer(str, delimiters);
		List<String> tokens = new ArrayList<>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || !token.isEmpty()) {
				if (converter != null) {
					token = converter.apply(token);
				}
				tokens.add(token);
			}
		}
		return toArray(tokens);
	}

	public static String[] delimitedToArray(@Nullable String str, String delimiters) {
		return delimitedToArray(str, delimiters, true, true);
	}

	public static String[] delimitedToArray(
		@Nullable String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
		return delimitedToArray(str, delimiters, trimTokens, ignoreEmptyTokens, null);
	}

	public static String[] delimitedToArray(
		@Nullable String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens, Function<String, String> converter) {
		if (str == null) {
			return StdConsts.EMPTY_STRING_ARRAY;
		}
		String[] arr = str.split(Pattern.quote(delimiters));
		if (!trimTokens && !ignoreEmptyTokens && converter == null) {
			return arr;
		}
		List<String> tokens = new ArrayList<>();
		for (String token : arr) {
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || !token.isEmpty()) {
				if (converter != null) {
					token = converter.apply(token);
				}
				tokens.add(token);
			}
		}
		return toArray(tokens);
	}

	public static String[] toArray(@Nullable Collection<String> collection) {
		if (collection == null || collection.isEmpty()) {
			return StdConsts.EMPTY_STRING_ARRAY;
		} else {
			return collection.toArray(new String[0]);
		}
	}

	public static String[] toArray(@Nullable Enumeration<String> enumeration) {
		return (enumeration != null ? toArray(Collections.list(enumeration)) : StdConsts.EMPTY_STRING_ARRAY);
	}

	public static String[] toArray(@Nullable Iterator<String> iterator) {
		return (iterator != null ? toArray(Iterables.asList(iterator)) : StdConsts.EMPTY_STRING_ARRAY);
	}

	public static <T extends Collection<E>, E> T asCollection(Supplier<T> supplier, Function<String, E> converter, String... args) {
		T t = supplier.get();
		for (String arg : args) {
			t.add(converter.apply(arg));
		}
		return t;
	}

	public static <T extends Collection<String>> T asCollection(Supplier<T> supplier, String... args) {
		T t = supplier.get();
		t.addAll(Arrays.asList(args));
		return t;
	}

	public static Set<String> asSet(String... args) {
		return asCollection(HashSet::new, args);
	}

	public static <E> Set<E> asSet(Function<String, E> converter, String... args) {
		return asCollection(HashSet::new, converter, args);
	}

	public static <E> List<E> asList(Function<String, E> converter, String... args) {
		return asCollection(ArrayList::new, converter, args);
	}

	public static <T extends Map<String, String>> T asMap(Supplier<T> supplier, String... args) {
		return Maps.asMap(supplier, args);
	}

	public static Map<String, String> asMap(String... args) {
		return Maps.asMap(HashMap::new, args);
	}


	public static String join(CharSequence delimiter, CharSequence... arr) {
		StringJoiner joiner = new StringJoiner(delimiter);
		for (CharSequence s : arr) {
			if (isBlank(s)) {
				continue;
			}
			joiner.add(s);
		}
		return joiner.toString();
	}

	public static String join(CharSequence delimiter, CharSequence prefix, CharSequence suffix,
		CharSequence[] arr) {
		StringJoiner joiner = new StringJoiner(delimiter, prefix, suffix);
		for (CharSequence s : arr) {
			if (isBlank(s)) {
				continue;
			}
			joiner.add(s);
		}
		return joiner.toString();
	}

	public static String join(CharSequence delimiter, Iterable<? extends CharSequence> arr) {
		StringJoiner joiner = new StringJoiner(delimiter);
		for (CharSequence s : arr) {
			if (isBlank(s)) {
				continue;
			}
			joiner.add(s);
		}
		return joiner.toString();
	}

	public static String join(CharSequence delimiter, CharSequence prefix, CharSequence suffix,
		Iterable<? extends CharSequence> arr) {
		StringJoiner joiner = new StringJoiner(delimiter, prefix, suffix);
		for (CharSequence s : arr) {
			if (isBlank(s)) {
				continue;
			}
			joiner.add(s);
		}
		return joiner.toString();
	}

	public static String format(String msg, Object... args) {
		return format(true, msg, args);
	}

	public static String format(boolean appendExtraArgs, String msg, Object... args) {
		if (msg == null || msg.isEmpty()) {
			if (args.length == 0 || !appendExtraArgs) {
				return "";
			}
			StringBuilder sb = new StringBuilder();
			sb.append(args[0]);
			for (int i = 1; i < args.length; i++) {
				sb.append(", ").append(args[i]);
			}
			return sb.toString();
		}
		if (args.length == 0) {
			return msg;
		}
		BitSet bitSet = new BitSet();
		StringBuffer sb = new StringBuffer();
		Matcher matcher = patternDigits.matcher(msg);
		while (matcher.find()) {
			int i = Integer.parseInt(matcher.group(1));
			bitSet.set(i);
			matcher.appendReplacement(sb, String.valueOf(i < args.length ? args[i] : null).replace("\\", "\\\\").replace("$", "\\$"));
		}
		matcher.appendTail(sb);
		msg = sb.toString();

		if (msg.contains("{}")) {
			sb.setLength(0);
			matcher = patternEmpty.matcher(msg);
			int i = 0;
			while (matcher.find()) {
				while (bitSet.get(i)) {
					i++;
				}
				matcher.appendReplacement(sb, String.valueOf(i < args.length ? args[i] : null).replace("\\", "\\\\").replace("$", "\\$"));
				bitSet.set(i);
			}
			matcher.appendTail(sb);
			msg = sb.toString();
		} else {
			if (bitSet.cardinality() >= args.length) {
				return msg;
			}
		}
		if (appendExtraArgs && bitSet.cardinality() < args.length) {
			for (int i = 0; i < args.length; i++) {
				if (!bitSet.get(i)) {
					sb.append(", ").append(String.valueOf(args[i]));
				}
			}
			return sb.toString();
		}
		return msg;
	}

	public static int toInt(String str, int def) {
		if (str == null) {
			return def;
		}
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException ignore) {
			return def;
		}
	}

	public static Integer toInteger(String str) {
		return toInteger(str, null);
	}

	public static Integer toInteger(String str, Integer def) {
		if (str == null) {
			return def;
		}
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException ignore) {
			return def;
		}
	}

	public static long toLong(String str, long def) {
		if (str == null) {
			return def;
		}
		try {
			return Long.parseLong(str);
		} catch (NumberFormatException ignore) {
			return def;
		}
	}

	public static Long toLong(String str) {
		return toLong(str, null);
	}

	public static Long toLong(String str, Long def) {
		if (str == null) {
			return def;
		}
		try {
			return Long.parseLong(str);
		} catch (NumberFormatException ignore) {
			return def;
		}
	}

	public static double toDouble(String str, double def) {
		return toDouble(str, Double.valueOf(def));
	}

	public static Double toDouble(String str, Double def) {
		if (str == null) {
			return def;
		}
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException ignore) {
			return def;
		}
	}

	public static Double toDouble(String str) {
		return toDouble(str, null);
	}

	public static boolean toBoolean(String str, boolean def) {
		return toBoolean(str, Boolean.valueOf(def));
	}

	public static Boolean toBoolean(String str, Boolean def) {
		if (str == null) {
			return def;
		}
		return Booleans.parseBoolean(str);
	}

	public static Boolean toBoolean(String str) {
		return toBoolean(str, null);
	}

}
