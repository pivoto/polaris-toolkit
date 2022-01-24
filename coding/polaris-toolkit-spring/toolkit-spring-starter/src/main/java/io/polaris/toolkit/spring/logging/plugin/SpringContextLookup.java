package io.polaris.toolkit.spring.logging.plugin;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.lookup.AbstractLookup;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author Qt
 * @version Aug 05, 2021
 * @since 1.8
 */
@PluginAliases({"springEnv", "springCtx"})
@Plugin(name = "ex", category = StrLookup.CATEGORY)
public class SpringContextLookup extends AbstractLookup implements Ordered, ApplicationListener<ApplicationEnvironmentPreparedEvent> {
	private static ConfigurableEnvironment environment;

	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
		environment = event.getEnvironment();
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	@Override
	public String lookup(LogEvent event, String key) {
		String val = null;
		if (environment != null) {
			val = environment.getProperty(key);
		}
		if (val == null) {
			val = System.getProperty(key);
		}
		if (val == null) {
			val = System.getenv(key);
		}
		if (val == null) {
			final int len = key.length();
			StringBuilder sb = new StringBuilder(len + 16);
			sb.append(Character.toUpperCase(key.charAt(0)));
			for (int i = 1; i < len; i++) {
				final char c = key.charAt(i);
				if (c == '.') {
					sb.append("_");
				} else if (Character.isUpperCase(c)) {
					sb.append("_").append(c);
				} else {
					sb.append(Character.toUpperCase(c));
				}
			}
			String envKey = sb.toString();
			val = System.getenv(envKey);
		}
		return val;
	}

}
