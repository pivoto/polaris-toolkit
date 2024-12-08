package io.polaris.core.string;

/**
 * 在字符串中查找子串：Knuth–Morris–Pratt 算法
 * <p>
 * KMP算法于1977年被提出，全称 Knuth–Morris–Pratt 算法，包含了三位前辈名字，分别是：Donald Knuth(K), James H. Morris(M), Vaughan Pratt(P)01算法核心思想
 * <p>
 * KMP算法步骤
 * <p>
 * 1. 计算部分匹配表（Partial Match Table）:
 * <p>
 * 这个表记录了模式串中每个位置前缀和后缀的最大公共长度。
 * <p>
 * 2. 字符串匹配:
 * <p>
 * 使用部分匹配表来指导主串和模式串的比较，当字符不匹配时，根据部分匹配表跳过一些不必要的比较。
 * <p>
 * <ul>
 * 参考：
 *   <li>https://oi-wiki.org/string/kmp/
 *   </li>
 *   <li>https://zhuanlan.zhihu.com/p/145536254
 *   </li>
 * </ul>
 *
 * @author Qt
 * @since Dec 08, 2024
 */
public class KmpMatchers {

	public static boolean contains(CharSequence container, CharSequence pattern) {
		return indexOf(container, pattern) >= 0;
	}

	public static boolean contains(char[] container, char[] pattern) {
		return indexOf(container, pattern) >= 0;
	}

	public static int indexOf(CharSequence container, CharSequence pattern) {
		return indexOf(container, pattern, 0);
	}

	public static int indexOf(CharSequence container, CharSequence pattern, int fromIndex) {
		int cl = container.length();
		if (fromIndex < 0) {
			fromIndex = 0;
		} else if (fromIndex >= cl) {
			return -1;
		}
		int pl = pattern.length();
		if (pl == 0) {
			return fromIndex;
		}
		if (cl - fromIndex < pl) {
			return -1;
		}
		if (pl == 1) {
			for (int i = fromIndex; i < cl; i++) {
				if (container.charAt(i) == pattern.charAt(0)) {
					return i;
				}
			}
			return -1;
		}
		int[] next = calcNextArray(pattern);
		int j = 0;
		for (int i = fromIndex; i < cl; ) {
			if (container.charAt(i) == pattern.charAt(j)) {
				if (j == pl - 1) {
					// 完全匹配成功
					return i - j;
				}
				// 继续匹配下一个字符
				i++;
				j++;
				continue;
			}
			// 如果模式串第一字符就不匹配，则跳过
			if (j == 0) {
				i++;
				continue;
			}
			// 如果存在部分匹配，使用部分匹配表，继续匹配
			j = next[j];
		}
		return -1;
	}

	public static int indexOf(char[] container, char[] pattern) {
		return indexOf(container, pattern, 0);
	}

	public static int indexOf(char[] container, char[] pattern, int fromIndex) {
		int cl = container.length;
		if (fromIndex < 0) {
			fromIndex = 0;
		} else if (fromIndex >= cl) {
			return -1;
		}
		int pl = pattern.length;
		if (pl == 0) {
			return fromIndex;
		}
		if (cl - fromIndex < pl) {
			return -1;
		}
		if (pl == 1) {
			for (int i = fromIndex; i < cl; i++) {
				if (container[i] == pattern[0]) {
					return i;
				}
			}
			return -1;
		}
		int[] next = calcNextArray(pattern);
		int j = 0;
		for (int i = fromIndex; i < cl; ) {
			if (container[i] == pattern[j]) {
				if (j == pl - 1) {
					// 完全匹配成功
					return i - j;
				}
				// 继续匹配下一个字符
				i++;
				j++;
				continue;
			}
			// 如果模式串第一字符就不匹配，则跳过
			if (j == 0) {
				i++;
				continue;
			}
			// 如果存在部分匹配，使用部分匹配表，继续匹配
			j = next[j];
		}
		return -1;
	}


	public static int indexOfIgnoreCase(CharSequence container, CharSequence pattern) {
		return indexOfIgnoreCase(container.toString().toCharArray(), pattern.toString().toCharArray());
	}

	public static int indexOfIgnoreCase(CharSequence container, CharSequence pattern, int fromIndex) {
		int cl = container.length();
		if (fromIndex < 0) {
			fromIndex = 0;
		} else if (fromIndex >= cl) {
			return -1;
		}
		int pl = pattern.length();
		if (pl == 0) {
			return fromIndex;
		}
		if (cl - fromIndex < pl) {
			return -1;
		}
		if (pl == 1) {
			for (int i = fromIndex; i < cl; i++) {
				if (container.charAt(i) == pattern.charAt(0) || container.charAt(i) == Character.toLowerCase(pattern.charAt(0))) {
					return i;
				}
			}
			return -1;
		}
		int[] next = calcNextArray(pattern);
		int j = 0;
		for (int i = fromIndex; i < cl; ) {
			if (container.charAt(i) == pattern.charAt(j) || container.charAt(i) == Character.toLowerCase(pattern.charAt(j))) {
				if (j == pl - 1) {
					// 完全匹配成功
					return i - j;
				}
				// 继续匹配下一个字符
				i++;
				j++;
				continue;
			}
			// 如果模式串第一字符就不匹配，则跳过
			if (j == 0) {
				i++;
				continue;
			}
			// 如果存在部分匹配，使用部分匹配表，继续匹配
			j = next[j];
		}
		return -1;
	}

	public static int indexOfIgnoreCase(char[] container, char[] pattern) {
		return indexOfIgnoreCase(container, pattern, 0);
	}

	public static int indexOfIgnoreCase(char[] container, char[] pattern, int fromIndex) {
		int cl = container.length;
		if (fromIndex < 0) {
			fromIndex = 0;
		} else if (fromIndex >= cl) {
			return -1;
		}
		int pl = pattern.length;
		if (pl == 0) {
			return fromIndex;
		}
		if (cl - fromIndex < pl) {
			return -1;
		}
		if (pl == 1) {
			for (int i = fromIndex; i < cl; i++) {
				if (container[i] == pattern[0] || container[i] == Character.toLowerCase(pattern[0])) {
					return i;
				}
			}
			return -1;
		}
		int[] next = calcNextArray(pattern);
		int j = 0;
		for (int i = fromIndex; i < cl; ) {
			if (container[i] == pattern[j] || container[i] == Character.toLowerCase(pattern[j])) {
				if (j == pl - 1) {
					// 完全匹配成功
					return i - j;
				}
				// 继续匹配下一个字符
				i++;
				j++;
				continue;
			}
			// 如果模式串第一字符就不匹配，则跳过
			if (j == 0) {
				i++;
				continue;
			}
			// 如果存在部分匹配，使用部分匹配表，继续匹配
			j = next[j];
		}
		return -1;
	}

	public static int lastIndexOf(CharSequence container, CharSequence pattern) {
		return lastIndexOf(container.toString().toCharArray(), pattern.toString().toCharArray());
	}

	public static int lastIndexOf(CharSequence container, CharSequence pattern, int fromIndex) {
		return lastIndexOf(container.toString().toCharArray(), pattern.toString().toCharArray(), fromIndex);
	}

	public static int lastIndexOf(char[] container, char[] pattern) {
		return lastIndexOf(container, pattern, container.length - 1);
	}

	public static int lastIndexOf(char[] container, char[] pattern, int fromIndex) {
		int cl = container.length;
		if (fromIndex < 0) {
			return -1;
		} else if (fromIndex >= cl) {
			fromIndex = cl - 1;
		}
		int pl = pattern.length;
		if (pl == 0) {
			return fromIndex;
		}
		if (fromIndex + 1 < pl) {
			return -1;
		}
		if (pl == 1) {
			for (int i = fromIndex; i >= 0; i--) {
				if (container[i] == pattern[0]) {
					return i;
				}
			}
			return -1;
		}

		int[] next = calcNextArrayReverse(pattern);
		int j = 0;
		for (int i = fromIndex; i >= 0; ) {
			if (container[i] == pattern[pl - j - 1]) {
				if (j == pl - 1) {
					// 完全匹配
					return i;
				}
				// 继续匹配下一个字符
				i--;
				j++;
				continue;
			}
			// 如果模式串第一字符就不匹配，则跳过
			if (j == 0) {
				i--;
				continue;
			}
			// 如果存在部分匹配，使用部分匹配表，继续匹配
			j = next[j];
		}
		return -1;
	}

	public static int lastIndexOfIgnoreCase(CharSequence container, CharSequence pattern) {
		return lastIndexOfIgnoreCase(container.toString().toCharArray(), pattern.toString().toCharArray());
	}

	public static int lastIndexOfIgnoreCase(CharSequence container, CharSequence pattern, int fromIndex) {
		return lastIndexOfIgnoreCase(container.toString().toCharArray(), pattern.toString().toCharArray(), fromIndex);
	}

	public static int lastIndexOfIgnoreCase(char[] container, char[] pattern) {
		return lastIndexOfIgnoreCase(container, pattern, container.length - 1);
	}

	public static int lastIndexOfIgnoreCase(char[] container, char[] pattern, int fromIndex) {
		int cl = container.length;
		if (fromIndex < 0) {
			return -1;
		} else if (fromIndex >= cl) {
			fromIndex = cl - 1;
		}
		int pl = pattern.length;
		if (pl == 0) {
			return fromIndex;
		}
		if (fromIndex + 1 < pl) {
			return -1;
		}
		if (pl == 1) {
			for (int i = fromIndex; i >= 0; i--) {
				if (container[i] == pattern[0] || container[i] == Character.toLowerCase(pattern[0])) {
					return i;
				}
			}
			return -1;
		}

		int[] next = calcNextArrayReverse(pattern);
		int j = 0;
		for (int i = fromIndex; i >= 0; ) {
			if (container[i] == pattern[pl - j - 1] || container[i] == Character.toLowerCase(pattern[pl - j - 1])) {
				if (j == pl - 1) {
					// 完全匹配
					return i;
				}
				// 继续匹配下一个字符
				i--;
				j++;
				continue;
			}
			// 如果模式串第一字符就不匹配，则跳过
			if (j == 0) {
				i--;
				continue;
			}
			// 如果存在部分匹配，使用部分匹配表，继续匹配
			j = next[j];
		}
		return -1;
	}

	static int[] calcNextArray(CharSequence pattern) {
		return calcNextArray(pattern.toString().toCharArray());
	}

	static int[] calcNextArray(char[] pattern) {
		int len = pattern.length;
		int[] next = new int[len];
		if (len == 0) {
			return next;
		}
		next[0] = -1;
		if (len == 1) {
			return next;
		}
		next[1] = 0;

		// 当前求解的next位置
		int i = 2;
		// 始终记录next[i-1]的值
		int lastVal = next[1];
		while (i < len) {
			if (pattern[i - 1] == pattern[lastVal]) {
				next[i++] = ++lastVal;
			} else if (lastVal == 0) {
				next[i++] = 0;
			} else {
				lastVal = next[lastVal];
			}
		}
		return next;
	}

	static int[] calcNextArrayReverse(CharSequence pattern) {
		return calcNextArrayReverse(pattern.toString().toCharArray());
	}

	static int[] calcNextArrayReverse(char[] pattern) {
		int len = pattern.length;
		int[] next = new int[len];
		if (len == 0) {
			return next;
		}
		next[0] = -1;
		if (len == 1) {
			return next;
		}
		next[1] = 0;

		// 当前求解的next位置
		int i = 2;
		// 始终记录next[i+1]的值
		int lastVal = next[1];
		while (i < len) {
			if (pattern[len - i] == pattern[len - 1 - lastVal]) {
				next[i++] = ++lastVal;
			} else if (lastVal == 0) {
				next[i++] = 0;
			} else {
				lastVal = next[lastVal];
			}
		}
		return next;
	}

}
