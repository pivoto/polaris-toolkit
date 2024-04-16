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
public class CasePascalMap<K, V> extends TransformMap<K, V> {
	private static final Function TRANSFORMER = key -> (key instanceof CharSequence ? StringCases.underlineToPascalCase((CharSequence) key) : key);

	public CasePascalMap() {
		super(new LinkedHashMap<>(), TRANSFORMER);
	}

	public CasePascalMap(Map<K, V> raw) {
		this();
		this.putAll(raw);
	}

}
