package io.polaris.core.map.reference;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * @author Qt
 * @since 1.8
 */
public class WeakValueReference<K, V> extends WeakReference<V> implements ValueReference<K, V> {
	private final K key;
	private final int hashCode;

	public WeakValueReference(K k, V v, ReferenceQueue<V> q) {
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
		if (o == null || !(o instanceof WeakValueReference)) {
			return false;
		}
		WeakValueReference<?, ?> that = (WeakValueReference<?, ?>) o;
		return Objects.equals(that.get(), this.get());
	}
}
