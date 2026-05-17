package io.polaris.core.string;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.polaris.core.concurrent.ThreadLocalSpec;
import io.polaris.core.concurrent.Threads;
import io.polaris.core.regex.Patterns;
import io.polaris.core.tuple.Ref;
import io.polaris.core.tuple.ValueRef;

/**
 * @author Qt
 * @since May 17, 2026
 */
public class Placeholders {

	private static final ThreadLocalSpec<Map<String, Ref<String>>> resolvedKeysLocal = Threads.newThreadLocal();
	private static final Pattern patternPlaceholder = Patterns.getPattern("\\$\\{([\\w\\.\\-]+)(?:(:-?)([^${}]*))?\\}");
	/// /private static final String patternPlaceholderSeparator = "\\Q:\\E";



	/**
	 * 字符串占位符替换，占位符：`${xxx.yyy.xxx}`或`${xxx.yyy.xxx:123}`
	 *
	 * @param origin
	 * @param getter
	 * @return
	 */
	public static String resolve(String origin, Function<String, String> getter) {
		return resolve(origin, patternPlaceholder, getter);
	}

	public static String resolve(String origin, Function<String, String> getter, boolean defaultAsEmpty) {
		return resolve(origin, patternPlaceholder, getter, defaultAsEmpty);
	}

	public static String resolve(String origin, Pattern placeholderPattern, Function<String, String> getter) {
		return resolve(origin, placeholderPattern, getter, true);
	}

	public static String resolve(String origin, Pattern placeholderPattern, Function<String, String> getter, boolean defaultAsEmpty) {
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
				rs = resolve(rs, placeholderPattern, getter, defaultAsEmpty);
			}
			return rs;
		} finally {
			if (hasInit) {
				resolvedKeysLocal.get().clear();
				resolvedKeysLocal.remove();
			}
		}
	}

}
