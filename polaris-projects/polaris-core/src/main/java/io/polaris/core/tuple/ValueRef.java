package io.polaris.core.tuple;


import java.io.Serializable;
import java.util.Objects;

/**
 * @author Qt
 * @since 1.8
 */
public class ValueRef<V> implements Ref<V>, Serializable {
	private static final long serialVersionUID = 1L;
	private final V value;

	public ValueRef(V value) {
		this.value = value;
	}

	@Override
	public V get() {
		return value;
	}

	public static <E> ValueRef<E> of(final E value) {
		return new ValueRef<>(value);
	}

	@Override
	public String toString() {
		return "ValueRef{value=" + value + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ValueRef<?> valueRef = (ValueRef<?>) o;
		return Objects.equals(value, valueRef.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
