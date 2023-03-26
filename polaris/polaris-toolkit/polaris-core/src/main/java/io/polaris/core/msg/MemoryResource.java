package io.polaris.core.msg;

import io.polaris.core.collection.Iterables;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Qt
 * @since 1.8
 */
public class MemoryResource extends ResourceBundle {
	private static final Map<String, MemoryResource> cache = new ConcurrentHashMap<>();
	private final String baseName;
	private final Map<String, Map<Locale, String>> MEMORY = new ConcurrentHashMap<>();

	private MemoryResource(String baseName) {
		this.baseName = baseName;
	}

	public static MemoryResource getInstance(String baseName) {
		return cache.computeIfAbsent(baseName, k -> new MemoryResource(baseName));
	}

	public String getBaseName() {
		return baseName;
	}

	@Override
	public Enumeration<String> getKeys() {
		return Iterables.enumeration(MEMORY.keySet());
	}

	@Override
	public Locale getLocale() {
		return Locale.ROOT;
	}

	@Override
	protected Object handleGetObject(String key) {
		return handleGetObject(key, getLocale());
	}

	protected Object handleGetObject(String key, Locale locale) {
		Map<Locale, String> map = MEMORY.get(key);
		if (map == null) {
			return null;
		}
		String val = map.get(locale);
		while (val == null && !Locale.ROOT.equals(locale)) {
			locale = getParent(locale);
			val = map.get(locale);
		}
		return val;
	}

	private Locale getParent(Locale locale) {
		if (locale.getVariant() != null && locale.getVariant().length() > 0) {
			return new Locale(locale.getLanguage(), locale.getCountry());
		}
		if (locale.getCountry() != null && locale.getCountry().length() > 0) {
			return new Locale(locale.getLanguage());
		}
		if (locale.getLanguage() != null && locale.getLanguage().length() > 0) {
			return Locale.ROOT;
		}
		return Locale.ROOT;
	}

	public void addResources(Locale locale, Map<String, String> map) {
		map.forEach((k, v) -> addResource(locale, k, v));
	}

	public void addResource(Locale locale, String key, String value) {
		if (locale != null) {
			Map<Locale, String> map = MEMORY.computeIfAbsent(key, k -> new ConcurrentHashMap<>());
			map.put(locale, value);
		}
	}
}
