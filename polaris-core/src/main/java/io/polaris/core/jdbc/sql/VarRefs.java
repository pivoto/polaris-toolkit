package io.polaris.core.jdbc.sql;

import java.util.Map;

import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.ExpressionMeta;

/**
 * @author Qt
 * @since Jul 08, 2026
 */
public class VarRefs {


	@SuppressWarnings("unchecked")
	public static <V> VarRef<V> wrap(ColumnMeta meta, V value) {
		if (value instanceof VarRef) {
			Map<String, String> props = ((VarRef<?>) value).getProps();
			if (!props.isEmpty()) {
				return (VarRef<V>) value;
			}
		}
		return VarRef.of(value, meta.getProps());
	}

	@SuppressWarnings("unchecked")
	public static <V> VarRef<V> wrap(ColumnMeta meta, V value, Map<String, String> varProps) {
		if (value instanceof VarRef) {
			Map<String, String> props = ((VarRef<?>) value).getProps();
			if (!props.isEmpty()) {
				return (VarRef<V>) value;
			}
		}
		if (!varProps.isEmpty()) {
			return VarRef.of(value, varProps);
		}
		return VarRef.of(value, meta.getProps());
	}

	@SuppressWarnings("unchecked")
	public static <V> VarRef<V> wrap(ExpressionMeta meta, V value) {
		if (value instanceof VarRef) {
			Map<String, String> props = ((VarRef<?>) value).getProps();
			if (!props.isEmpty()) {
				return (VarRef<V>) value;
			}
		}
		return VarRef.of(value, meta.getProps());
	}

	@SuppressWarnings("unchecked")
	public <V> VarRef<V> wrap(ExpressionMeta meta, V value, Map<String, String> varProps) {
		if (value instanceof VarRef) {
			Map<String, String> props = ((VarRef<?>) value).getProps();
			if (!props.isEmpty()) {
				return (VarRef<V>) value;
			}
		}
		if (!varProps.isEmpty()) {
			return VarRef.of(value, varProps);
		}
		return VarRef.of(value, meta.getProps());
	}

}
