package io.polaris.core.string;

import io.polaris.core.collection.ObjectArrays;
import io.polaris.core.collection.PrimitiveArrays;
import io.polaris.core.lang.primitive.Chars;
import io.polaris.core.ulid.UlidCreator;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Qt
 * @since 1.8
 */
public class Strings {

	private static final ThreadLocal<Map<String, String>> resolvedKeysLocal = new ThreadLocal<>();
	private static Pattern patternPlaceholder = Pattern.compile("\\$\\{([\\w\\.\\-]+(?::([^${}]*))?)\\}");
	private static final String patternPlaceholderSeparator = "\\Q:\\E";
	private static Pattern patternDigits = Pattern.compile("(?<!\\\\)\\{(\\d+)\\}");
	private static Pattern patternEmpty = Pattern.compile("(?<!\\\\)\\{\\}");

	/**
	 * 字节大小值转为带单位的可读字符串
	 */
	public static String toReadableByteSizeStr(long byteSize) {
		long b = byteSize % 1024;
		long k = byteSize / 1024;
		if (k == 0) {
			return b + "B" ;
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
			return "" ;
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

	public static <T> T nvl(T o, T reo) {
		if (isEmpty(o)) {
			return reo;
		} else {
			return o;
		}
	}

	public static String coalesce(String... args) {
		String v = null;
		for (String arg : args) {
			v = arg;
			if (Strings.isNotBlank(v)) {
				break;
			}
		}
		return v;
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


	public static String uuid() {
		return UUID.randomUUID().toString().replace("-" , "");
	}

	public static String ulid() {
		return UlidCreator.getUlid().toString();
	}

	/**
	 * 标准化标识符，非法字符替换为下划线
	 */
	public static String normalize(String name) {
		return name == null ? "" : name.trim().replaceAll("\\W" , "_");
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
		return resolvePlaceholders(origin, patternPlaceholder, patternPlaceholderSeparator, getter);
	}

	public static String resolvePlaceholders(String origin, Pattern placeholderPattern, String placeholderSeparator, Function<String, String> getter) {
		if (origin == null) {
			return origin;
		}
		boolean hasInit = false;
		Map<String, String> resovedKeys = resolvedKeysLocal.get();
		if (resovedKeys == null) {
			hasInit = true;
			resolvedKeysLocal.set(resovedKeys = new HashMap<>());
		}
		try {
			Matcher matcher = placeholderPattern.matcher(origin);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				String placeholder = matcher.group(1);
				String[] arr = placeholder.split(placeholderSeparator, 2);
				String k = arr[0].trim();
				String defVal = arr.length > 1 ? arr[1].trim() : "" ;
				String v = null;
				if (resovedKeys.containsKey(k)) {
					v = resovedKeys.get(k);
				} else {
					resovedKeys.put(k, "");
					v = getter.apply(k);
					resovedKeys.put(k, v);
				}
				if (v == null) {
					v = defVal;
				}
				v = v.replace("\\" , "\\\\").replace("$" , "\\$");
				matcher.appendReplacement(sb, v);
			}
			matcher.appendTail(sb);
			String rs = sb.toString();
			Matcher nextMatcher = placeholderPattern.matcher(rs);
			if (nextMatcher.find()) {
				rs = resolvePlaceholders(rs, placeholderPattern, placeholderSeparator, getter);
			}
			return rs;
		} finally {
			if (hasInit) {
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

	public static String removePrefix(CharSequence str, CharSequence prefix) {
		if (isEmpty(str) || isEmpty(prefix)) {
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
		String str1 = str.toString();
		if (str1.endsWith(suffix.toString())) {
			return str1.substring(0, str.length() - suffix.length());
		}
		return str1;
	}

	public static String trimToEmpty(String str) {
		if (str == null) {
			return "" ;
		}
		return str.trim();
	}

	public static String trimToNull(String str) {
		if (str != null) {
			str = str.trim();
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
		return str.trim();
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
		if (o == null || "".equals(o)) {
			return true;
		}
		return false;
	}

	public static boolean isNotEmpty(Object o) {
		return !isEmpty(o);
	}

	public static boolean isNotNone(CharSequence str) {
		return !isNone(str);
	}

	public static boolean isNone(CharSequence cs) {
		return isBlank(cs) || "null".equals(cs.toString());
	}

	public static boolean isNotBlank(CharSequence str) {
		return !isBlank(str);
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

	public static boolean isNotEmpty(CharSequence str) {
		return !isEmpty(str);
	}

	public static boolean isEmpty(final CharSequence str) {
		return str == null || str.length() == 0;
	}

	public static boolean containsAny(CharSequence str, CharSequence... testStrs) {
		return null != getContainsStr(str, testStrs);
	}

	public static String getContainsStr(CharSequence str, CharSequence... testStrs) {
		if (isEmpty(str) || ObjectArrays.isEmpty(testStrs)) {
			return null;
		}
		for (CharSequence checkStr : testStrs) {
			if (str.toString().contains(checkStr)) {
				return checkStr.toString();
			}
		}
		return null;
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

	public static boolean startWithIgnoreCase(@Nonnull String str, @Nonnull String prefix) {
		if (prefix.length() == 0) {
			return true;
		}
		if (str.length() == 0) {
			return false;
		}
		return str.regionMatches(true, 0, prefix, 0, prefix.length());
	}

	public static boolean endWithIgnoreCase(@Nonnull String str, @Nonnull String prefix) {
		if (prefix.length() == 0) {
			return true;
		}
		if (str.length() == 0) {
			return false;
		}
		return str.regionMatches(true, str.length() - prefix.length(), prefix, 0, prefix.length());
	}

	public static int indexOfIgnoreCase(String source, String target) {
		return indexOfIgnoreCase(source.toCharArray(), 0, source.length(),
			target.toCharArray(), 0, target.length(), 0);
	}

	public static int indexOfIgnoreCase(String source, String target, int fromIndex) {
		return indexOfIgnoreCase(source.toCharArray(), 0, source.length(),
			target.toCharArray(), 0, target.length(), fromIndex);
	}


	@SuppressWarnings({"StatementWithEmptyBody" , "AliControlFlowStatementWithoutBraces"})
	public static int indexOfIgnoreCase(char[] source, int sourceOffset, int sourceCount
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


	public static int lastIndexOfIgnoreCase(String source, String target) {
		return lastIndexOfIgnoreCase(source.toCharArray(), 0, source.length(),
			target.toCharArray(), 0, target.length(), source.length());
	}

	public static int lastIndexOfIgnoreCase(String source, String target, int fromIndex) {
		return lastIndexOfIgnoreCase(source.toCharArray(), 0, source.length(),
			target.toCharArray(), 0, target.length(), fromIndex);
	}

	public static int lastIndexOfIgnoreCase(char[] source, int sourceOffset, int sourceCount
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

	public static boolean isEquals(CharSequence str1, CharSequence str2) {
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

	public static boolean isEqualsIgnoreCase(CharSequence str1, CharSequence str2) {
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

	public static boolean isEqualsIgnoreCase(char[] cs1, char[] cs2) {
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


	public static <T extends Collection<String>> T splitToCollection(Supplier<T> supplier, String str, String delimiterRegex) {
		return asCollection(supplier, str.split(delimiterRegex));
	}


	public static Set<String> splitToSet(String str, String delimiterRegex) {
		return asCollection(HashSet::new, str.split(delimiterRegex));
	}

	public static List<String> splitToList(String str, String delimiterRegex) {
		return asCollection(ArrayList::new, str.split(delimiterRegex));
	}

	public static <T extends Collection<String>> T splitToCollection(Supplier<T> supplier, String str) {
		return asCollection(supplier, str.split(","));
	}

	public static Set<String> splitToSet(String str) {
		return asCollection(HashSet::new, str.split(","));
	}

	public static List<String> splitToList(String str) {
		return asCollection(ArrayList::new, str.split(","));
	}

	public static <T extends Collection<String>> T asCollection(Supplier<T> supplier, String... args) {
		T t = supplier.get();
		for (String arg : args) {
			t.add(arg);
		}
		return t;
	}

	public static Set<String> asSet(String... args) {
		return asCollection(HashSet::new, args);
	}

	public static List<String> asList(String... args) {
		return asCollection(ArrayList::new, args);
	}

	public static <T extends Map<String, String>> T asMap(Supplier<T> supplier, String... args) {
		T map = supplier.get();
		for (int i = 0; i + 1 < args.length; i += 2) {
			map.put(args[i], args[i + 1]);
		}
		return map;
	}

	public static Map<String, String> asMap(String... args) {
		return asMap(HashMap::new, args);
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
		if (msg == null || msg.isEmpty()) {
			if (args.length == 0) {
				return "" ;
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
			matcher.appendReplacement(sb, String.valueOf(i < args.length ? args[i] : null).replace("\\" , "\\\\").replace("$" , "\\$"));
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
				matcher.appendReplacement(sb, String.valueOf(i < args.length ? args[i] : null).replace("\\" , "\\\\").replace("$" , "\\$"));
				bitSet.set(i);
			}
			matcher.appendTail(sb);
			msg = sb.toString();
		} else {
			if (bitSet.cardinality() >= args.length) {
				return msg;
			}
		}
		if (bitSet.cardinality() < args.length) {
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
		try {
			return Boolean.parseBoolean(str);
		} catch (NumberFormatException ignore) {
			return def;
		}
	}

	public static Boolean toBoolean(String str) {
		return toBoolean(str, null);
	}

}
