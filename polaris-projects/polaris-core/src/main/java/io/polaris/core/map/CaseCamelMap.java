package io.polaris.core.map;

import io.polaris.core.string.StringCases;

import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class CaseCamelMap<K, V> extends TransformMap<K, V> {

	public CaseCamelMap(Map<K, V> raw) {
		super(raw, key -> (K) (key instanceof CharSequence ? StringCases.underlineToCamelCase((CharSequence) key) : key));
	}

}
