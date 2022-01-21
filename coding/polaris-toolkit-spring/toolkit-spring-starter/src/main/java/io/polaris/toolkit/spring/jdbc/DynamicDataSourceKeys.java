package io.polaris.toolkit.spring.jdbc;

import io.polaris.toolkit.spring.transaction.TransactionAspectHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author Qt
 * @version Dec 30, 2021
 * @since 1.8
 */
@Slf4j
public class DynamicDataSourceKeys {

	private static final ThreadLocal<Optional<String>> CURRENT = new ThreadLocal<>();
	private static final ThreadLocal<Deque<Optional<String>>> HISTORY = new ThreadLocal<>();
	private static Set<String> KEYS = new ConcurrentSkipListSet<>();
	private static String defaultKey = null;


	public static String get() {
		Optional<String> current = CURRENT.get();
		return current != null && current.isPresent() ? current.get() : defaultKey;
	}

	public static boolean set(String key) {
		Optional<String> current = CURRENT.get();
		if (current == null) {
			CURRENT.set(Optional.ofNullable(key));
			return true;
		}
		if (!Objects.equals(current.orElse(null), key)) {
			CURRENT.set(Optional.ofNullable(key));
			Deque<Optional<String>> history = HISTORY.get();
			if (history == null) {
				history = new ArrayDeque<>();
				HISTORY.set(history);
			}
			history.offerLast(current);
			return true;
		}
		return false;
	}

	public static void clear() {
		Deque<Optional<String>> history = HISTORY.get();
		if (history != null) {
			Optional<String> last = history.pollLast();
			CURRENT.set(last);
			if (history.isEmpty()) {
				HISTORY.remove();
			}
		} else {
			CURRENT.remove();
		}
	}

	static void setDefaultKey(String defaultKey) {
		DynamicDataSourceKeys.defaultKey = defaultKey;
	}

	static void setKeys(Set<String> keys) {
		DynamicDataSourceKeys.KEYS = keys;
	}

	static void add(String key) {
		DynamicDataSourceKeys.KEYS.add(key);
	}

	public static boolean contains(String key) {
		return DynamicDataSourceKeys.KEYS.contains(key);
	}


	public static Object doInterceptor(String targetKey, Invokable interceptorCall) throws Throwable {
		boolean binded = false;
		String key = StringUtils.hasText(targetKey) ? targetKey : null;
		try {
			if (key != null && !DynamicDataSourceKeys.contains(key)) {
				log.error("数据源[{}]不存在，使用默认数据源!", key);
				key = null;
			}
			binded = DynamicDataSourceKeys.set(key);
			if (binded) {
				if (log.isDebugEnabled()) {
					log.debug("使用数据源：{}", Objects.toString(key, "{default}"));
				}
			}
			return interceptorCall.invoke();
		} finally {
			if (binded) {
				DynamicDataSourceKeys.clear();
				if (log.isDebugEnabled()) {
					log.debug("停用数据源：{}", Objects.toString(key, "{default}"));
				}
			}
		}
	}

	public interface Invokable {
		Object invoke() throws Throwable;
	}

}
