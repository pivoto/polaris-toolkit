package io.polaris.toolkit.spring.crypto;

import io.polaris.toolkit.spring.annotation.EnableCryptoProperties;
import io.polaris.toolkit.spring.constants.ToolkitConstants;
import io.polaris.toolkit.spring.util.Binders;
import io.polaris.toolkit.spring.util.Contexts;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * @author Qt
 * @version Nov 04, 2021
 * @since 1.8
 */
public class CryptoPropertiesBeanHelper {

	public static void determineCryptoCapability(ConfigurableEnvironment environment, SpringApplication springApplication) {
		Boolean enabled = environment.getProperty(ToolkitConstants.TOOLKIT_CRYPTO_ENABLED, Boolean.class, false);
		if (enabled.booleanValue()) {
			buildEarly(environment);

			// add initializer
			springApplication.addInitializers(new CryptoApplicationContextInitializer());
		}
	}

	public static void buildEarly(ConfigurableEnvironment environment) {
		CryptoPropertySource propertySource = attachPropertySource(environment);
		if (!propertySource.hasResolver()) {
			CryptoPropertyResolver resolver = buildResolverEarly(environment);
			propertySource.bindResolver(resolver);
		}
	}

	private static CryptoPropertySource attachPropertySource(ConfigurableEnvironment environment) {
		MutablePropertySources mutablePropertySources = environment.getPropertySources();
		String name = ToolkitConstants.CRYPTO_PROPERTY_SOURCE_NAME;
		PropertySource<?> propertySource = mutablePropertySources.get(name);
		if (propertySource != null) {
			mutablePropertySources.remove(name);
			if (!(propertySource instanceof CryptoPropertySource)) {
				propertySource = null;
			}
		}
		if (propertySource == null) {
			propertySource = new CryptoPropertySource(name, new CryptoPropertySource.EnvHolder(environment));
		}
		mutablePropertySources.addFirst(propertySource);
		return (CryptoPropertySource) propertySource;
	}

	private static CryptoPropertyResolver buildResolverEarly(ConfigurableEnvironment environment) {
		CryptoConfigurationProperties properties = Binders.bind(environment,
				CryptoConfigurationProperties.class, ToolkitConstants.TOOLKIT_CRYPTO);
		if (properties == null) {
			properties = new CryptoConfigurationProperties();
			properties.setEnabled(true);
			String algorithmStr = environment.getProperty(ToolkitConstants.TOOLKIT_CRYPTO_ALGORITHM);
			String decryptKey = environment.getProperty(ToolkitConstants.TOOLKIT_CRYPTO_DECRYPT_KEY);
			if (decryptKey == null) {
				decryptKey = environment.getProperty(ToolkitConstants.TOOLKIT_CRYPTO_DECRYPT_KEY2);
			}
			String decryptKeyLocation = environment.getProperty(ToolkitConstants.TOOLKIT_CRYPTO_DECRYPT_KEY_LOCATION);
			if (decryptKeyLocation == null) {
				decryptKeyLocation = environment.getProperty(ToolkitConstants.TOOLKIT_CRYPTO_DECRYPT_KEY_LOCATION2);
			}
			String encryptKey = environment.getProperty(ToolkitConstants.TOOLKIT_CRYPTO_ENCRYPT_KEY);
			if (encryptKey == null) {
				encryptKey = environment.getProperty(ToolkitConstants.TOOLKIT_CRYPTO_ENCRYPT_KEY2);
			}
			String encryptKeyLocation = environment.getProperty(ToolkitConstants.TOOLKIT_CRYPTO_ENCRYPT_KEY_LOCATION);
			if (encryptKeyLocation == null) {
				encryptKeyLocation = environment.getProperty(ToolkitConstants.TOOLKIT_CRYPTO_ENCRYPT_KEY_LOCATION2);
			}
			String prefix = environment.getProperty(ToolkitConstants.TOOLKIT_CRYPTO_PROPERTY_PREFIX);
			String suffix = environment.getProperty(ToolkitConstants.TOOLKIT_CRYPTO_PROPERTY_SUFFIX);
			if (algorithmStr != null) {
				properties.setAlgorithm(CryptoAlgorithm.valueOf(algorithmStr.toUpperCase()));
			}
			properties.setDecryptKey(decryptKey);
			properties.setDecryptKeyLocation(decryptKeyLocation);
			properties.setEncryptKey(encryptKey);
			properties.setEncryptKeyLocation(encryptKeyLocation);
			if (prefix != null) {
				properties.getProperty().setPrefix(prefix);
			}
			if (suffix != null) {
				properties.getProperty().setSuffix(suffix);
			}
		}

		CryptoPropertyResolver resolver = new CryptoPropertyResolver(properties);
		resolver.init();
		return resolver;
	}

	public static void buildEarlyInRegistrarIfNecessary(AnnotationMetadata importingClassMetadata) {
		Contexts.configurableEnvironment().ifPresent(environment -> {
			MutablePropertySources mutablePropertySources = environment.getPropertySources();
			String name = ToolkitConstants.CRYPTO_PROPERTY_SOURCE_NAME;
			PropertySource<?> propertySource = mutablePropertySources.get(name);
			if (propertySource != null) {
				return;
			}
			Boolean enabled = environment.getProperty(ToolkitConstants.TOOLKIT_CRYPTO_ENABLED, Boolean.class, false);
			if (!enabled.booleanValue()) {
				Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(
						EnableCryptoProperties.class.getName(), false);
				enabled = attributes != null;
			}
			if (enabled.booleanValue()) {
				buildEarly(environment);
			}
		});
	}

	public static CryptoPropertiesBeanFactoryPostProcessor bindInitializer(ConfigurableApplicationContext applicationContext) {
		CryptoPropertiesBeanFactoryPostProcessor postProcessor = new CryptoPropertiesBeanFactoryPostProcessor();
		postProcessor.setApplicationContext(applicationContext);
		applicationContext.addBeanFactoryPostProcessor(postProcessor);
		return postProcessor;
	}

	public static void bindResolver(ConfigurableApplicationContext applicationContext) {
		ConfigurableEnvironment environment = applicationContext.getEnvironment();
		CryptoPropertySource propertySource = attachPropertySource(environment);
		CryptoPropertyResolver resolver = applicationContext.getBean(CryptoPropertyResolver.class);
		propertySource.bindResolver(resolver);
	}
}
