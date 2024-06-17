package io.polaris.core.map;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class CaseInsensitiveMap<K, V> extends TransformMap<K, V> {
	private static final Function TRANSFORMER_UPPER_CASE = key -> (key instanceof CharSequence ? key.toString().toUpperCase() : key);
	private static final Function TRANSFORMER_LOWER_CASE = key -> (key instanceof CharSequence ? key.toString().toLowerCase() : key);

	public CaseInsensitiveMap(Supplier<Map<K, V>> supplier) {
		super(supplier, TRANSFORMER_UPPER_CASE);
	}

	public CaseInsensitiveMap() {
		super(new LinkedHashMap<>(), TRANSFORMER_UPPER_CASE);
	}

	public CaseInsensitiveMap(Supplier<Map<K, V>> supplier, boolean upperCase) {
		super(supplier, upperCase ? TRANSFORMER_UPPER_CASE : TRANSFORMER_LOWER_CASE);
	}


	public CaseInsensitiveMap(boolean upperCase) {
		super(new LinkedHashMap<>(), upperCase ? TRANSFORMER_UPPER_CASE : TRANSFORMER_LOWER_CASE);
	}

	public CaseInsensitiveMap(Supplier<Map<K, V>> supplier, Map<K, V> raw) {
		this(supplier);
		addRawData(raw);
	}

	public CaseInsensitiveMap(Map<K, V> raw) {
		this();
		addRawData(raw);
	}

	public CaseInsensitiveMap(Supplier<Map<K, V>> supplier, Map<K, V> raw, boolean upperCase) {
		this(supplier, upperCase);
		addRawData(raw);
	}

	public CaseInsensitiveMap(Map<K, V> raw, boolean upperCase) {
		this(upperCase);
		addRawData(raw);
	}

	private void addRawData(Map<K, V> raw) {
		for (Entry<K, V> entry : raw.entrySet()) {
			putIfAbsent(entry.getKey(), entry.getValue());
		}
	}

}
