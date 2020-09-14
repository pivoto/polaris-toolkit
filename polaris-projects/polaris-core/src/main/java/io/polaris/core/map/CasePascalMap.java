package io.polaris.core.map;

import io.polaris.core.string.StringCases;

import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class CasePascalMap<K, V> extends TransformMap<K, V> {

	public CasePascalMap(Map<K, V> raw) {
		super(raw, key -> (K) (key instanceof CharSequence ? StringCases.underlineToPascalCase((CharSequence) key) : key));
	}

}
