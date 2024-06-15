package io.polaris.core.hash;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

/**
 * 相关文档： https://stackoverflow.com/questions/1835976/what-is-a-sensible-prime-for-hashcode-calculation
 *
 * @author Qt
 * @since May 11, 2024
 */
public class ArrayHash {

	public static int hash(Object o) {
		return hash(92821, o);
	}

	public static int hash(Object[] o) {
		return hash(92821, o);
	}

	public static int hash(CharSequence o) {
		return hash(92821, o);
	}

	public static int hash(int prime, Object arg) {
		if (arg == null) {
			return 0;
		}
		if (arg instanceof Number) {
			return arg.hashCode();
		}
		if (arg instanceof CharSequence) {
			return hash(prime, (CharSequence) arg);
		}
		if (arg instanceof Object[]) {
			return hash(prime, (Object[]) arg);
		}

		if (arg.getClass().isArray()) {
			int h = 0;
			int len = Array.getLength(arg);
			for (int i = 0; i < len; i++) {
				Object o = Array.get(arg, i);
				h = prime * h + hash(prime, o);
			}
			return h;
		}
		if (arg instanceof Collection) {
			int h = 0;
			if (arg instanceof List && arg instanceof RandomAccess) {
				int size = ((List<?>) arg).size();
				for (int i = 0; i < size; i++) {
					Object o = ((List<?>) arg).get(i);
					h = prime * h + hash(prime, o);
				}
			} else {
				for (Object o : ((Collection<?>) arg)) {
					h = prime * h + hash(prime, o);
				}
			}
			return h;
		}
		if (arg instanceof Map) {
			int h = 0;
			for (Map.Entry<?, ?> entry : ((Map<?, ?>) arg).entrySet()) {
				h = prime * h + (hash(prime, entry.getKey()) ^ hash(prime, entry.getValue()));
			}
			return h;
		}
		return arg.hashCode();
	}

	public static int hash(int prime, CharSequence str) {
		int h = 0;
		int len = str.length();
		if (len > 0) {
			for (int i = 0; i < len; i++) {
				h = prime * h + str.charAt(i);
			}
		}
		return h;
	}

	public static int hash(int prime, Object[] args) {
		if (args == null) {
			return 0;
		}
		int h = 0;
		for (Object o : args) {
			h = prime * h + hash(prime, o);
		}
		return h;
	}

}
