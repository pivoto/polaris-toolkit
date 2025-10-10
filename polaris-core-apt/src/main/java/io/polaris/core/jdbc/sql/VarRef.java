package io.polaris.core.jdbc.sql;

import java.util.Objects;

import io.polaris.core.lang.Copyable;

/**
 * @author Qt
 * @since Oct 10, 2025
 */
public class VarRef<T> implements Copyable<VarRef<T>> {

	private final T value;
	private final String props;

	private VarRef(T value, String props) {
		// assert !(value instanceof VarRef);
		this.value = value;
		this.props = props == null ? "" : props.trim();
	}

	@SuppressWarnings("unchecked")
	public static <T> VarRef<T> of(T value) {
		if (value instanceof VarRef) {
			T v = (T) (((VarRef<?>) value).getValue());
			String props = ((VarRef<?>) value).getProps();
			return of(v, props);
		}
		return of(value, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> VarRef<T> of(T value, String props) {
		if (value instanceof VarRef) {
			value = (T) (((VarRef<?>) value).getValue());
			return of(value, props);
		}
		return new VarRef<>(value, props);
	}

	public T getValue() {
		return value;
	}

	public String getProps() {
		return props;
	}

	@Override
	public String toString() {
		return Objects.toString(value);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof VarRef)) return false;
		VarRef<?> varRef = (VarRef<?>) o;
		return Objects.equals(getValue(), varRef.getValue()) &&
			Objects.equals(getProps(), varRef.getProps());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getValue(), getProps());
	}

	@Override
	public VarRef<T> copy() {
		return new VarRef<>(value, props);
	}
}
