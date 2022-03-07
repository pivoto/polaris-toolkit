package io.polaris.toolkit.spring.jdbc.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * @see org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
 * @see org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerPostProcessor
 * @see org.springframework.boot.autoconfigure.jdbc.DataSourceInitializer
 * @author Qt
 * @version Mar 04, 2022
 * @since 1.8
 */
@Slf4j
public class DynamicDataSourceInitializerPostProcessor  implements BeanDefinitionRegistryPostProcessor {

	public static String getInitializerClassName() {
		return "org.springframework.boot.autoconfigure.jdbc.DataSourceInitializer";
	}

	public static String getInitializerPostProcessorClassName() {
		return "org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerPostProcessor";
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		// 移除自带的初始化器
		for (String name : registry.getBeanDefinitionNames()) {
			BeanDefinition beanDefinition = registry.getBeanDefinition(name);
			if (getInitializerClassName().equals(beanDefinition.getBeanClassName())
					|| getInitializerPostProcessorClassName().equals(beanDefinition.getBeanClassName())
			) {
				log.info("移除Bean定义: {} -> {}", name, beanDefinition.getBeanClassName());
				registry.removeBeanDefinition(name);
			}
		}
		registry.registerBeanDefinition(DataSourceInitializerPostProcessor.class.getName(),
				BeanDefinitionBuilder.genericBeanDefinition(DataSourceInitializerPostProcessor.class)
						.setRole(BeanDefinition.ROLE_SUPPORT)
						.getBeanDefinition());
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

}
