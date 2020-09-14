package io.polaris.core.map.reference;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

/**
 * @author Qt
 * @since 1.8
 */
public enum ReferenceType {
	/** 软引用，在GC报告内存不足时会被GC回收 */
	SOFT,
	/** 弱引用，在GC时发现弱引用会回收其对象 */
	WEAK,
	;


	public <K, V> ValueReference<K, V> buildValueReference(K key, V value, ReferenceQueue<V> queue) {
		if (this == SOFT) {
			return new SoftValueReference<>(key, value, queue);
		} else if (this == WEAK) {
			return new WeakValueReference<>(key, value, queue);
		}
		throw new IllegalStateException();
	}


	public <K> Reference<K> buildKeyReference(K key, ReferenceQueue<K> queue) {
		if (this == SOFT) {
			return new SoftKeyReference<>(key, queue);
		} else if (this == WEAK) {
			return new WeakKeyReference<>(key, queue);
		}
		throw new IllegalStateException();
	}

}
