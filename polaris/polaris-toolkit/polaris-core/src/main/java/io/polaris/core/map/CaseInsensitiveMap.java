package io.polaris.core.map;

import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class CaseInsensitiveMap<K, V> extends TransformMap<K, V> {

	public CaseInsensitiveMap(Map<K, V> raw) {
		super(raw, key -> (K) (key instanceof CharSequence ? key.toString().toLowerCase() : key));
	}

	public CaseInsensitiveMap(Map<K, V> raw, boolean upperCase) {
		super(raw, upperCase ? key -> (K) (key instanceof CharSequence ? key.toString().toUpperCase() : key)
			: key -> (K) (key instanceof CharSequence ? key.toString().toLowerCase() : key));
	}

}
