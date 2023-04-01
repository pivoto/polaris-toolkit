package io.polaris.core.lang;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Qt
 * @since 1.8
 */
public class Strings {

	private static final ThreadLocal<Map<String, String>> resolvedKeysLocal = new ThreadLocal<>();
	private static Pattern patternPlaceholder = Pattern.compile("\\$\\{([\\w\\.\\-:]+)\\}");
	private static Pattern patternDigits = Pattern.compile("(?<!\\\\)\\{(\\d+)\\}");
	private static Pattern patternEmpty = Pattern.compile("(?<!\\\\)\\{\\}");

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

	public static String coalesce(String... args) {
		String v = null;
		if (args.length > 0) {
			for (String arg : args) {
				if (arg != null && !arg.trim().equals("")) {
					v = arg.trim();
					break;
				}
			}
		}
		return v;
	}

	public static <T> T nvl(T o, T reo) {
		if (isEmpty(o)) {
			return reo;
		} else {
			return o;
		}
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


	public static String uuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/** 标准化标识符，非法字符替换为下划线 */
	public static String normalize(String name) {
		return name == null ? "" : name.trim().replaceAll("\\W", "_");
	}

	/**
	 * @see java.beans.Introspector#decapitalize(String)
	 */
	public static String decapitalize(String name) {
		return Introspector.decapitalize(name);
	}

	public static String capitalize(String name) {
		if (name == null || name.length() == 0) {
			return name;
		}
		if (name.length() > 1 && Character.isUpperCase(name.charAt(1))) {
			return name;
		}
		char chars[] = name.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
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
	 * 字符串占位符替换，点位符：`${xxx.yyy.xxx}`或`${xxx.yyy.xxx:123}`
	 *
	 * @param origin
	 * @param getter
	 * @return
	 */
	public static String resolvePlaceholders(String origin, Function<String, String> getter) {
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
			Matcher matcher = patternPlaceholder.matcher(origin);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				String[] arr = matcher.group(1).split(":", 2);
				String k = arr[0].trim();
				String defVal = arr.length > 1 ? arr[1].trim() : "";
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
				v = v.replace("$", "\\$").replace("\\", "\\\\");
				matcher.appendReplacement(sb, v);
			}
			matcher.appendTail(sb);
			return sb.toString();
		} finally {
			if (hasInit) {
				resolvedKeysLocal.remove();
			}
		}
	}


	public static String trimToEmpty(String str) {
		if (str == null) {
			return "";
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

	public static String join(CharSequence delimiter, Iterable<CharSequence> arr) {
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
		Iterable<CharSequence> arr) {
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
			matcher.appendReplacement(sb, String.valueOf(i < args.length ? args[i] : null));
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
				matcher.appendReplacement(sb, String.valueOf(i < args.length ? args[i] : null));
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
