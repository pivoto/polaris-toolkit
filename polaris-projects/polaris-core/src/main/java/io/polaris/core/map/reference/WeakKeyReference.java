package io.polaris.core.map.reference;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * @author Qt
 * @since 1.8
 */
public class WeakKeyReference<K> extends WeakReference<K> {
	private final int hashCode;

	public WeakKeyReference(K key, ReferenceQueue< K> queue) {
		super(key, queue);
		hashCode = key.hashCode();
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
		if (o == null || !(o instanceof WeakKeyReference)) {
			return false;
		}
		WeakKeyReference<?> that = (WeakKeyReference<?>) o;
		return Objects.equals(that.get(), this.get());
	}
}
