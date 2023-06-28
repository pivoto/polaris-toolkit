package io.polaris.core.regex;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Qt
 * @since 1.8
 */
public class Patterns {

	private static final Map<String, Pattern> CACHED_PATTERNS = new ConcurrentHashMap<>();

	public static boolean find(String regex, String content) {
		return getPattern(regex).matcher(content).find();
	}

	public static boolean matches(String regex, String content) {
		return getPattern(regex).matcher(content).matches();
	}

	public static Matcher matcher(String regex, String content) {
		return getPattern(regex).matcher(content);
	}

	public static Pattern getPattern(String regex) {
		Pattern pattern = CACHED_PATTERNS.get(regex);
		if (pattern == null) {
			CACHED_PATTERNS.put(regex, pattern = Pattern.compile(regex));
		}
		return pattern;
	}
}
