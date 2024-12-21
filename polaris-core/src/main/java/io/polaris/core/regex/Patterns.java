package io.polaris.core.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.polaris.core.collection.Sets;
import io.polaris.core.string.Strings;
import io.polaris.core.tuple.Tuple2;

/**
 * @author Qt
 * @since 1.8
 */
public class Patterns {

	private static final Map<String, Pattern> CACHED_PATTERNS = new ConcurrentHashMap<>();
	private static final Map<Tuple2<String, Integer>, Pattern> CACHED_FLAG_PATTERNS = new ConcurrentHashMap<>();
	/** 正则中需要被转义的关键字 */
	private final static Set<Character> RE_KEYS = Sets.asSet('$', '(', ')', '*', '+', '.', '[', ']', '?', '\\', '^', '{', '}', '|');

	public static Pattern getPattern(String regex) {
		return CACHED_PATTERNS.computeIfAbsent(regex, k -> Pattern.compile(regex));
	}

	public static Pattern getPattern(String regex, int flags) {
		return CACHED_FLAG_PATTERNS.computeIfAbsent(Tuple2.of(regex, flags), k -> Pattern.compile(regex, flags));
	}

	public static Pattern getWholePattern(String regex) {
		return getPattern(quoteWhole(regex));
	}

	public static Pattern getWholePattern(String regex, int flags) {
		return getPattern(quoteWhole(regex), flags);
	}

	public static String quote(String regex) {
		return Pattern.quote(regex);
	}

	public static Pattern quoteWhole(Pattern pattern) {
		return getWholePattern(pattern.pattern(), pattern.flags());
	}

	public static String quoteWhole(String regex) {
		if (regex.startsWith("^")) {
			if (regex.endsWith("$")) {
				return (regex);
			} else {
				return (regex + "$");
			}
		} else {
			if (regex.endsWith("$")) {
				return ("^" + regex);
			} else {
				return ("^" + regex + "$");
			}
		}
	}

	public static boolean find(String regex, CharSequence content) {
		return getPattern(regex).matcher(content).find();
	}

	public static boolean find(Pattern pattern, CharSequence content) {
		return pattern.matcher(content).find();
	}

	public static boolean matches(String regex, CharSequence content) {
		return getPattern(regex).matcher(content).matches();
	}

	public static boolean matches(Pattern pattern, CharSequence content) {
		return pattern.matcher(content).matches();
	}

	/**
	 * 判断是否存在任意字符串匹配，数组为空时返回false
	 */
	public static boolean matchesAny(Pattern pattern, CharSequence... contents) {
		if (contents != null) {
			for (CharSequence content : contents) {
				if (matches(pattern, content)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断是否所有字符串都匹配，数组为空时返回true
	 */
	public static boolean matchesAll(Pattern pattern, CharSequence... contents) {
		if (contents != null) {
			for (CharSequence content : contents) {
				if (!matches(pattern, content)) {
					return false;
				}
			}
		}
		return true;
	}

	public static Matcher matcher(String regex, CharSequence content) {
		return getPattern(regex).matcher(content);
	}

	public static Matcher matcher(Pattern pattern, CharSequence content) {
		return pattern.matcher(content);
	}

	public static String getGroup(String regex, CharSequence content, int groupIndex) {
		return getGroup(getPattern(regex), content, groupIndex);
	}


	public static String getGroup(String regex, CharSequence content, String groupName) {
		return getGroup(getPattern(regex), content, groupName);
	}

	public static String getGroup(Pattern pattern, CharSequence content, int groupIndex) {
		Matcher m = pattern.matcher(content);
		if (m.find()) {
			return m.group(groupIndex);
		}
		return null;
	}

	public static String getGroup(Pattern pattern, CharSequence content, String groupName) {
		Matcher m = pattern.matcher(content);
		if (m.find()) {
			return m.group(groupName);
		}
		return null;
	}

	public static List<String> getAllGroups(String regex, CharSequence content, boolean withGroup0) {
		return getAllGroups(getPattern(regex), content, withGroup0, false);
	}

	public static List<String> getAllGroups(Pattern pattern, CharSequence content, boolean withGroup0) {
		return getAllGroups(pattern, content, withGroup0, false);
	}

	public static List<String> getAllGroups(String regex, CharSequence content, boolean withGroup0, boolean findAll) {
		return getAllGroups(getPattern(regex), content, withGroup0, findAll);
	}

	public static List<String> getAllGroups(Pattern pattern, CharSequence content, boolean withGroup0, boolean findAll) {
		if (null == content || null == pattern) {
			return null;
		}
		List<String> result = new ArrayList<>();
		final Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			final int startGroup = withGroup0 ? 0 : 1;
			final int groupCount = matcher.groupCount();
			for (int i = startGroup; i <= groupCount; i++) {
				result.add(matcher.group(i));
			}
			if (!findAll) {
				break;
			}
		}
		return result;
	}

	/**
	 * 找到指定正则匹配到字符串的开始位置
	 *
	 * @param regex   正则
	 * @param content 字符串
	 * @return 位置，{@code null}表示未找到
	 */
	public static MatchResult indexOf(String regex, CharSequence content) {
		return indexOf(getPattern(regex), content);
	}

	/**
	 * 找到指定模式匹配到字符串的开始位置
	 *
	 * @param pattern 模式
	 * @param content 字符串
	 * @return 位置，{@code null}表示未找到
	 */
	public static MatchResult indexOf(Pattern pattern, CharSequence content) {
		if (null != pattern && null != content) {
			final Matcher matcher = pattern.matcher(content);
			if (matcher.find()) {
				return matcher.toMatchResult();
			}
		}
		return null;
	}

	/**
	 * 找到指定正则匹配到第一个字符串的位置
	 *
	 * @param regex   正则
	 * @param content 字符串
	 * @return 位置，{@code null}表示未找到
	 */
	public static MatchResult lastIndexOf(String regex, CharSequence content) {
		return lastIndexOf(getPattern(regex), content);
	}

	/**
	 * 找到指定模式匹配到最后一个字符串的位置
	 *
	 * @param pattern 模式
	 * @param content 字符串
	 * @return 位置，{@code null}表示未找到
	 */
	public static MatchResult lastIndexOf(Pattern pattern, CharSequence content) {
		MatchResult result = null;
		if (null != pattern && null != content) {
			final Matcher matcher = pattern.matcher(content);
			while (matcher.find()) {
				result = matcher.toMatchResult();
			}
		}
		return result;
	}


	/**
	 * 转义字符，将正则的关键字转义
	 *
	 * @param c 字符
	 * @return 转义后的文本
	 */
	public static String escape(char c) {
		final StringBuilder builder = new StringBuilder();
		if (RE_KEYS.contains(c)) {
			builder.append('\\');
		}
		builder.append(c);
		return builder.toString();
	}

	/**
	 * 转义字符串，将正则的关键字转义
	 *
	 * @param content 文本
	 * @return 转义后的文本
	 */
	public static String escape(CharSequence content) {
		if (Strings.isBlank(content)) {
			return content == null ? null : content.toString();
		}

		final StringBuilder builder = new StringBuilder();
		int len = content.length();
		char current;
		for (int i = 0; i < len; i++) {
			current = content.charAt(i);
			if (RE_KEYS.contains(current)) {
				builder.append('\\');
			}
			builder.append(current);
		}
		return builder.toString();
	}
}
