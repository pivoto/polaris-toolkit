package io.polaris.core.collection.comparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

/**
 * 字符串比较器，支持null值优先和长度优先排序
 *
 * @author Qt
 * @since Dec 21, 2025
 */
public class StringComparator implements Comparator<String>, Serializable {

	private static volatile StringComparator nullsFirstShortFirst;
	private static volatile StringComparator nullsLastShortFirst;
	private static volatile StringComparator nullsFirstShortLast;
	private static volatile StringComparator nullsLastShortLast;

	private static final long serialVersionUID = 1L;
	private final boolean nullsFirst;
	private final boolean shortFirst;

	public StringComparator(boolean nullsFirst, boolean shortFirst) {
		this.nullsFirst = nullsFirst;
		this.shortFirst = shortFirst;
	}

	public static StringComparator nullsFirstShortFirst() {
		if (nullsFirstShortFirst == null) {
			synchronized (StringComparator.class) {
				if (nullsFirstShortFirst == null) {
					nullsFirstShortFirst = new StringComparator(true, true);
				}
			}
		}
		return nullsFirstShortFirst;
	}

	public static StringComparator nullsLastShortFirst() {
		if (nullsLastShortFirst == null) {
			synchronized (StringComparator.class) {
				if (nullsLastShortFirst == null) {
					nullsLastShortFirst = new StringComparator(false, true);
				}
			}
		}
		return nullsLastShortFirst;
	}

	public static StringComparator nullsFirstShortLast() {
		if (nullsFirstShortLast == null) {
			synchronized (StringComparator.class) {
				if (nullsFirstShortLast == null) {
					nullsFirstShortLast = new StringComparator(true, false);
				}
			}
		}
		return nullsFirstShortLast;
	}

	public static StringComparator nullsLastShortLast() {
		if (nullsLastShortLast == null) {
			synchronized (StringComparator.class) {
				if (nullsLastShortLast == null) {
					nullsLastShortLast = new StringComparator(false, false);
				}
			}
		}
		return nullsLastShortLast;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof StringComparator)) return false;
		StringComparator that = (StringComparator) object;
		return nullsFirst == that.nullsFirst && shortFirst == that.shortFirst;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nullsFirst, shortFirst);
	}

	/**
	 * 比较两个字符串，支持null值优先和长度优先排序
	 *
	 * @param a 第一个字符串
	 * @param b 第二个字符串
	 * @return 比较结果：负数表示a小于b，0表示a等于b，正数表示a大于b
	 */
	@Override
	public int compare(String a, String b) {
		if (a == null) {
			return (b == null) ? 0 : (nullsFirst ? -1 : 1);
		}
		if (b == null) {
			return nullsFirst ? 1 : -1;
		}
		int len1 = a.length();
		int len2 = b.length();
		if (len1 != len2) {
			if (shortFirst) {
				return len1 - len2;
			}
			return len2 - len1;
		}
		return a.compareTo(b);
	}

}
