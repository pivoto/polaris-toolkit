package io.polaris.core.map.reference;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Objects;

/**
 * @author Qt
 * @since 1.8
 */
public class SoftValueReference<K, V> extends SoftReference<V> implements ValueReference<K, V> {
	private final K key;
	private final int hashCode;

	public SoftValueReference(K k, V v, ReferenceQueue<V> q) {
		super(v, q);
		this.key = k;
		this.hashCode = v.hashCode();
	}

	@Override
	public K key() {
		return key;
	}

	@Override
	public V value() {
		return super.get();
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || !(o instanceof SoftValueReference)) {
			return false;
		}
		SoftValueReference<?, ?> that = (SoftValueReference<?, ?>) o;
		return Objects.equals(that.get(), this.get());
	}
}
