package io.polaris.core.jdbc.sql;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.lang.Copyable;

/**
 * @author Qt
 * @since Oct 10, 2025
 */
public class VarRef<T> implements Copyable<VarRef<T>> {

	@Nullable
	private final T value;
	@Nonnull
	private final Map<String, String> props;

	private VarRef(@Nullable T value, @Nullable Map<String, String> props) {
		// assert !(value instanceof VarRef);
		this.value = value;
		this.props = props == null ? Collections.emptyMap() : props;
	}

	@SuppressWarnings("unchecked")
	public static <T> VarRef<T> of(T value) {
		if (value instanceof VarRef) {
			T v = (T) (((VarRef<?>) value).value);
			Map<String, String> props = ((VarRef<?>) value).props;
			return of(v, props);
		}
		return of(value, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> VarRef<T> of(T value, Map<String, String> props) {
		if (value instanceof VarRef) {
			value = (T) (((VarRef<?>) value).getValue());
			return of(value, props);
		}
		return new VarRef<>(value, props);
	}

	@Nullable
	public T getValue() {
		return value;
	}

	@Nonnull
	public Map<String, String> getProps() {
		return props;
	}

	@Nullable
	public Map<String, String> getPropsIfNotEmpty() {
		return props.isEmpty() ? null : props;
	}

	@Nullable
	public Map<String, String> getPropsIfNotEmpty(Predicate<String> filter) {
		Map<String, String> props = getProps(filter);
		return props.isEmpty() ? null : props;
	}

	@Nonnull
	public Map<String, String> getProps(Predicate<String> filter) {
		if (filter == null) {
			return props;
		}
		Map<String, String> rs = new LinkedHashMap<>();
		props.forEach((k, v) -> {
			if (filter.test(k)) {
				rs.put(k, v);
			}
		});
		return rs;
	}

	public String getPropString() {
		return getPropString(null);
	}


	public String getPropString(Predicate<String> filter) {
		if (props.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		if (filter == null) {
			props.forEach((k, v) -> sb.append(k).append("=").append(v).append(","));
		} else {
			props.forEach((k, v) -> {
				if (filter.test(k)) {
					sb.append(k).append("=").append(v).append(",");
				}
			});
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
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
