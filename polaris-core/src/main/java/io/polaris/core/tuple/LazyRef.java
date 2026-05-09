package io.polaris.core.tuple;


import java.io.Serializable;
import java.util.Objects;
import java.util.function.Supplier;

import io.polaris.core.lang.Copyable;
import lombok.Getter;

/**
 * @author Qt
 */
public class LazyRef<V> implements Ref<V>, Serializable, Copyable<LazyRef<V>> {
	private static final long serialVersionUID = 1L;
	private final Supplier<InitializedValue<V>> supplier;
	@Getter
	private volatile boolean initialized = false;
	private V value;

	private LazyRef(Supplier<InitializedValue<V>> supplier, boolean initialized, V value) {
		this.supplier = supplier;
		this.initialized = initialized;
		this.value = value;
	}

	public LazyRef(boolean nullable, Supplier<V> supplier) {
		this.supplier = () -> {
			V val = supplier.get();
			if (val != null) {
				return InitializedValue.initialized(val);
			} else if (nullable) {
				return InitializedValue.initialized(null);
			} else {
				return InitializedValue.uninitialized();
			}
		};
	}

	public LazyRef(Supplier<InitializedValue<V>> supplier) {
		this.supplier = supplier;
	}


	public static <E> LazyRef<E> of(final Supplier<E> supplier) {
		return new LazyRef<>(false, supplier);
	}

	public static <E> LazyRef<E> of(boolean nullable, final Supplier<E> supplier) {
		return new LazyRef<>(nullable, supplier);
	}

	public static <E> LazyRef<E> ofNullable(final Supplier<E> supplier) {
		return new LazyRef<>(true, supplier);
	}

	public static <E> LazyRef<E> ofConfigurable(final Supplier<InitializedValue<E>> supplier) {
		return new LazyRef<>(supplier);
	}

	@Override
	public LazyRef<V> copy() {
		return new LazyRef<>(supplier, initialized, value);
	}

	@Override
	public V get() {
		if (!initialized) {
			synchronized (this) {
				if (!initialized) {
					InitializedValue<V> initializedValue = supplier.get();
					this.value = initializedValue.getValue();
					this.initialized = initializedValue.isInitialized();
				}
			}
		}
		return value;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("LazyRef{");
		sb.append("supplier=").append(supplier);
		sb.append('}');
		return sb.toString();
	}


	@Override
	public boolean equals(Object o) {
		if (!(o instanceof LazyRef)) return false;
		LazyRef<?> lazyRef = (LazyRef<?>) o;
		return Objects.equals(supplier, lazyRef.supplier);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(supplier);
	}

}
