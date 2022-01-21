package io.polaris.toolkit.spring.annotation;

import io.polaris.toolkit.spring.annotation.EnableCryptoProperties;
import io.polaris.toolkit.spring.crypto.CryptoPropertiesBeanHelper;
import io.polaris.toolkit.spring.crypto.CryptoPropertyResolver;
import io.polaris.toolkit.spring.util.Contexts;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Qt
 * @version Oct 29, 2021
 * @since 1.8
 */
public class CryptoPropertiesConfigurationRegistrar
		implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware {
	private ConfigurableEnvironment environment;
	private ResourceLoader resourceLoader;

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		AnnotationAttributes attributes =
				AnnotationAttributes.fromMap(
						importingClassMetadata.getAnnotationAttributes(EnableCryptoProperties.class.getName()));
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CryptoPropertyResolver.class);
		AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
		BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);

		// bind early
		Contexts.configurableEnvironment().ifPresent(CryptoPropertiesBeanHelper::buildEarly);
		// bind initializer
		Contexts.configurableApplicationContext().ifPresent(CryptoPropertiesBeanHelper::bindInitializer);
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = (ConfigurableEnvironment) environment;
	}
}
