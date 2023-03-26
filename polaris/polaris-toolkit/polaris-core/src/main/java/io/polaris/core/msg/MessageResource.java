package io.polaris.core.msg;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Qt
 * @since 1.8
 */
public class MessageResource {
	private final String baseName;
	private final Map<Locale, MemoryResourceBundle> cache = new ConcurrentHashMap<>();

	public MessageResource(String... baseNames) {
		this(AggregateResourceBundles.aggregateNames(baseNames));
	}

	public MessageResource(String baseName) {
		this.baseName = baseName;
	}

	public MemoryResourceBundle getBundle(Locale locale) {
		return cache.computeIfAbsent(locale, k -> {
			MemoryResourceBundle bundle = new MemoryResourceBundle(this.baseName, locale);
			bundle.setParent(AggregateResourceBundles.getBundle(this.baseName, locale));
			return bundle;
		});
	}

	public String getMessageOrDefault(String code, String defaults, Locale locale, Object... params) {
		MemoryResourceBundle bundle = getBundle(locale);
		String val = null;
		try {
			val = bundle.getString(code);
		} catch (MissingResourceException e) {
			val = defaults;
		}
		if (val == null) {
			return val;
		}
		if (params.length == 0) {
			return val;
		}
		return MessageFormat.format(val, params);
	}

	public String getMessage(String code, Object... params) {
		return getMessageOrDefault(code, "", Locale.getDefault(), params);
	}

	public String getMessageOrDefault(String code, String defaults, Object... params) {
		return getMessageOrDefault(code, defaults, Locale.getDefault(), params);
	}

}
