package io.polaris.toolkit.spring.logging.plugin;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.config.plugins.processor.PluginCache;
import org.apache.logging.log4j.core.config.plugins.processor.PluginEntry;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;

/**
 * @author Qt
 * @version Aug 05, 2021
 * @since 1.8
 */
public class PluginDatWriter {

	PluginCache cache = new PluginCache();

	public PluginDatWriter() {
	}

	public void collect() {
		collect(SpringContextLookup.class);
	}

	public void write(OutputStream out) throws IOException {
		cache.writeCache(out);
		out.flush();
	}

	public void collect(Class<?> clazz) {
		// PluginProcessor
		Plugin plugin = clazz.getAnnotation(Plugin.class);
		PluginEntry entry = new PluginEntry();
		entry.setKey(plugin.name().toLowerCase(Locale.US));
		entry.setClassName(clazz.getName());
		entry.setName(Plugin.EMPTY.equals(plugin.elementType()) ? plugin.name() : plugin.elementType());
		entry.setPrintable(plugin.printObject());
		entry.setDefer(plugin.deferChildren());
		entry.setCategory(plugin.category());

		Map<String, PluginEntry> category = cache.getCategory(plugin.category());
		category.put(entry.getKey(), entry);

		collectAlias(clazz);
	}

	private void collectAlias(Class<?> clazz) {
		PluginAliases aliases = clazz.getAnnotation(PluginAliases.class);
		if (aliases == null) {
			return;
		}
		for (String alias : aliases.value()) {
			Plugin plugin = clazz.getAnnotation(Plugin.class);
			PluginEntry entry = new PluginEntry();
			entry.setKey(alias.toLowerCase(Locale.US));
			entry.setClassName(clazz.getName());
			entry.setName(Plugin.EMPTY.equals(plugin.elementType()) ? plugin.name() : plugin.elementType());
			entry.setPrintable(plugin.printObject());
			entry.setDefer(plugin.deferChildren());
			entry.setCategory(plugin.category());

			Map<String, PluginEntry> category = cache.getCategory(plugin.category());
			category.put(entry.getKey(), entry);

		}
	}
}
