package io.polaris.core.collection.comparator;

import io.polaris.core.assertion.Assertions;
import io.polaris.core.collection.ObjectArrays;

import java.util.Comparator;

/**
 * @author Qt
 * @since 1.8
 */
public class IndexedComparator<T> implements Comparator<T> {
	/** 如果不在列表中是否排在后边 */
	private final boolean atEndIfMiss;
	private final T[] array;

	public IndexedComparator(T... objs) {
		this(false, objs);
	}

	public IndexedComparator(boolean atEndIfMiss, T... objs) {
		Assertions.assertNotNull(objs, "'objs' array must not be null");
		this.atEndIfMiss = atEndIfMiss;
		this.array = objs;
	}

	@Override
	public int compare(T o1, T o2) {
		final int index1 = getOrder(o1);
		final int index2 = getOrder(o2);
		if (index1 == index2) {
			if (index1 < 0 || index1 == this.array.length) {
				// 任意一个元素不在列表中, 返回原顺序
				return 1;
			}
			// 位置一样，认为是同一个元素
			return 0;
		}
		return Integer.compare(index1, index2);
	}

	private int getOrder(T object) {
		int order = ObjectArrays.indexOf(array, object);
		if (order < 0) {
			order = this.atEndIfMiss ? this.array.length : -1;
		}
		return order;
	}
}
