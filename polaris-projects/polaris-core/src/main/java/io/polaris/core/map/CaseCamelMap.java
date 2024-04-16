package io.polaris.core.map;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import io.polaris.core.string.StringCases;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CaseCamelMap<K, V> extends TransformMap<K, V> {
	private static final Function TRANSFORMER = key -> (key instanceof CharSequence ? StringCases.underlineToCamelCase((CharSequence) key) : key);

	public CaseCamelMap() {
		super(new LinkedHashMap<>(), TRANSFORMER);
	}

	public CaseCamelMap(Map<K, V> raw) {
		this();
		this.putAll(raw);
	}

}
