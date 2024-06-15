package io.polaris.core.map;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import io.polaris.core.string.StringCases;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CasePascalMap<K, V> extends TransformMap<K, V> {
	private static final Function TRANSFORMER = key -> (key instanceof CharSequence ? StringCases.underlineToPascalCase((CharSequence) key) : key);

	public CasePascalMap(Supplier<Map<K, V>> supplier) {
		super(supplier, TRANSFORMER);
	}

	public CasePascalMap() {
		super(new LinkedHashMap<>(), TRANSFORMER);
	}

	public CasePascalMap(Supplier<Map<K, V>> supplier, Map<K, V> raw) {
		this(supplier);
		this.addRawData(raw);
	}

	public CasePascalMap(Map<K, V> raw) {
		this();
		this.addRawData(raw);
	}

	private void addRawData(Map<K, V> raw) {
		for (Entry<K, V> entry : raw.entrySet()) {
			putIfAbsent(entry.getKey(), entry.getValue());
		}
	}

}
