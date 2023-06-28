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

}
