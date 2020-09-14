package io.polaris.core.map;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class CaseInsensitiveMap<K, V> extends TransformMap<K, V> {

	public CaseInsensitiveMap() {
		super(new LinkedHashMap<>(), key -> (K) (key instanceof CharSequence ? key.toString().toUpperCase() : key));
	}
	public CaseInsensitiveMap(boolean upperCase) {
		super(new LinkedHashMap<>(), upperCase ? key -> (K) (key instanceof CharSequence ? key.toString().toUpperCase() : key)
			: key -> (K) (key instanceof CharSequence ? key.toString().toLowerCase() : key));
	}

	public CaseInsensitiveMap(Map<K, V> raw) {
		super(raw, key -> (K) (key instanceof CharSequence ? key.toString().toUpperCase() : key));
	}

	public CaseInsensitiveMap(Map<K, V> raw, boolean upperCase) {
		super(raw, upperCase ? key -> (K) (key instanceof CharSequence ? key.toString().toUpperCase() : key)
			: key -> (K) (key instanceof CharSequence ? key.toString().toLowerCase() : key));
	}

}
