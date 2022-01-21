package io.polaris.toolkit.spring.crypto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

/**
 * @author Qt
 * @version Nov 01, 2021
 * @since 1.8
 */
@Slf4j
public class CryptoPropertySource
		//extends PropertySource<CryptoPropertySource.EnvHolder>
		extends EnumerablePropertySource<CryptoPropertySource.EnvHolder> {
	private final ThreadLocal<String> resolvedKey = new ThreadLocal<>();
	private CryptoPropertyResolver resolver;

	public CryptoPropertySource(String name, EnvHolder source) {
		super(name, source);
	}

	@Override
	public boolean containsProperty(String name) {
		return (getProperty(name) != null);
	}

	@Override
	public String[] getPropertyNames() {
		return resolver != null ? resolver.cachedKeys() : new String[0];
	}

	@Override
	public Object getProperty(String name) {
		if (resolver == null) {
			return null;
		}
		/*
		由于 ConfigurationPropertySourcesPropertySource 对象在获取属性时
		会遍历除StubPropertySource、ConfigurationPropertySourcesPropertySource之外的所有PropertySource,
		规则见`org.springframework.boot.context.properties.source.SpringConfigurationPropertySources.SourcesIterator.isIgnored`,
		添加本类后将导致死循环，需要特殊处理
		 */
		if (name.equals(resolvedKey.get())) {
			return null;
		}
		try {
			resolvedKey.set(name);
			ConfigurableEnvironment env = getSource().environment;
			MutablePropertySources propertySources = env.getPropertySources();
			for (PropertySource<?> propertySource : propertySources) {
				if (propertySource == this) {
					continue;
				}
				Object value = propertySource.getProperty(name);
				if (value != null) {
					if (value instanceof String) {
						value = resolver.decrypt(this, name, (String) value);
					}
					return value;
				}
			}
		} finally {
			resolvedKey.remove();
		}
		return null;
	}

	public boolean hasResolver() {
		return this.resolver != null;
	}

	public void bindResolver(CryptoPropertyResolver resolver) {
		if (resolver != null) {
			if (this.resolver != resolver) {
				log.debug("****** bind resolver: {}", resolver);
				if (this.resolver != null) {
					resolver.merge(this.resolver);
				}
				this.resolver = resolver;
				visitEnumerablePropertySource();
			}
		}
	}

	private void visitEnumerablePropertySource() {
		ConfigurableEnvironment env = getSource().environment;
		MutablePropertySources propertySources = env.getPropertySources();
		for (PropertySource<?> propertySource : propertySources) {
			if (propertySource == this) {
				continue;
			}
			if (propertySource instanceof EnumerablePropertySource) {
				String[] propertyNames = ((EnumerablePropertySource<?>) propertySource).getPropertyNames();
				if (propertyNames == null) {
					continue;
				}
				for (String name : propertyNames) {
					// visit and cache encrypted key
					this.getProperty(name);
				}
			}
		}
	}

	public static class EnvHolder {
		ConfigurableEnvironment environment;

		public EnvHolder(ConfigurableEnvironment environment) {
			this.environment = environment;
		}
	}
}
