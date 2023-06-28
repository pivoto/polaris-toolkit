package io.polaris.core.map.reference;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Objects;

/**
 * @author Qt
 * @since 1.8
 */
public class SoftKeyReference<K> extends SoftReference<K> {
	private final int hashCode;

	public SoftKeyReference(K key, ReferenceQueue< K> queue) {
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
		if (o == null || !(o instanceof SoftKeyReference)) {
			return false;
		}
		SoftKeyReference<?> that = (SoftKeyReference<?>) o;
		return Objects.equals(that.get(), this.get());
	}
}
