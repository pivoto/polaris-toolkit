package io.polaris.core.tuple;


import java.io.Serializable;
import java.util.Objects;
import java.util.function.Supplier;

import io.polaris.core.lang.Copyable;
import io.polaris.core.lang.Objs;
import lombok.Getter;

/**
 * @author Qt
 * @since 1.8
 */
public class LazyRef<V> implements Ref<V>, Serializable, Copyable<LazyRef<V>> {
	private static final long serialVersionUID = 1L;
	private final Supplier<V> supplier;
	@Getter
	private volatile boolean initialized = false;
	private V value;

	public LazyRef(Supplier<V> supplier) {
		this.supplier = supplier;
	}

	public static <E> LazyRef<E> of(final Supplier<E> supplier) {
		return new LazyRef<>(supplier);
	}

	@Override
	public LazyRef<V> copy() {
		return new LazyRef<>(supplier);
	}

	@Override
	public V get() {
		if (!initialized) {
			synchronized (this) {
				if (!initialized) {
					value = supplier.get();
					initialized = value != null;
				}
			}
		}
		return value;
	}

	@Override
	public String toString() {
		return "LazyRef{supplier=" + supplier + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		LazyRef<?> valueRef = (LazyRef<?>) o;
		return Objects.equals(supplier, valueRef.supplier);
	}

	@Override
	public int hashCode() {
		return Objects.hash(supplier);
	}
}
