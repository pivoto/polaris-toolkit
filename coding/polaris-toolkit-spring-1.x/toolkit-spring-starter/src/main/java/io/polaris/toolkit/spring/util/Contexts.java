package io.polaris.toolkit.spring.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Qt
 * @version Nov 01, 2021
 * @since 1.8
 */
public class Contexts {
	@Setter
	@Getter
	private static ApplicationContext applicationContext;
	@Setter
	@Getter
	private static Environment environment;

	public static Optional<Environment> environment() {
		return Optional.ofNullable(environment);
	}

	public static Optional<ApplicationContext> applicationContext() {
		return Optional.ofNullable(applicationContext);
	}

	public static Optional<ConfigurableEnvironment> configurableEnvironment() {
		return Optional.ofNullable(getConfigurableEnvironment());
	}

	public static Optional<ConfigurableApplicationContext> configurableApplicationContext() {
		return Optional.ofNullable(getConfigurableApplicationContext());
	}

	public static Optional<ConfigurableListableBeanFactory> configurableListableBeanFactory() {
		return Optional.ofNullable(getConfigurableListableBeanFactory());
	}

	public static Optional<BeanDefinitionRegistry> beanDefinitionRegistry() {
		return Optional.ofNullable(getBeanDefinitionRegistry());
	}

	public static List<String> getBasePackages() {
		return getBasePackages(Contexts.applicationContext);
	}

	public static List<String> getBasePackages(BeanFactory beanFactory) {
		if (beanFactory != null && AutoConfigurationPackages.has(beanFactory)) {
			return AutoConfigurationPackages.get(beanFactory);
		}
		return Collections.emptyList();
	}


	public static ConfigurableEnvironment getConfigurableEnvironment() {
		if (environment != null && environment instanceof ConfigurableEnvironment) {
			return (ConfigurableEnvironment) environment;
		}
		return null;
	}

	public static ConfigurableApplicationContext getConfigurableApplicationContext() {
		if (applicationContext != null && applicationContext instanceof ConfigurableApplicationContext) {
			return (ConfigurableApplicationContext) applicationContext;
		}
		return null;
	}

	public static ConfigurableListableBeanFactory getConfigurableListableBeanFactory() {
		if (applicationContext != null && applicationContext instanceof ConfigurableApplicationContext) {
			return ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
		}
		return null;
	}

	public static BeanDefinitionRegistry getBeanDefinitionRegistry() {
		if (applicationContext != null && applicationContext instanceof ConfigurableApplicationContext) {
			ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
			return beanFactory instanceof BeanDefinitionRegistry ? (BeanDefinitionRegistry) beanFactory : null;
		}
		return null;
	}
}
