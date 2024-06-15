package io.polaris.core.msg;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("All")
public class MemoryResourceBundle extends ResourceBundle {

	private final String baseName;
	private final Locale locale;
	private final MemoryResource resource;

	public MemoryResourceBundle(String baseName, Locale locale) {
		this.baseName = baseName;
		this.locale = locale;
		this.resource = MemoryResource.getInstance(baseName);
	}

	public MemoryResourceBundle(String baseName) {
		this(baseName, Locale.getDefault());
	}

	@Override
	public void setParent(ResourceBundle parent) {
		super.setParent(parent);
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	protected Object handleGetObject(String key) {
		return resource.handleGetObject(key, getLocale());
	}

	@Override
	public Enumeration<String> getKeys() {
		return resource.getKeys();
	}

	public void addResources(Map<String, String> map) {
		resource.addResources(getLocale(), map);
	}

	public void addResources(Locale locale, Map<String, String> map) {
		resource.addResources(locale, map);
	}

	public void addResource(String code, String value) {
		resource.addResource(getLocale(), code, value);
	}

	public void addResource(Locale locale, String key, String value) {
		resource.addResource(locale, key, value);
	}
}
