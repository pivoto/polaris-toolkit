package io.polaris.toolkit.spring.crypto;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Qt
 * @version Nov 01, 2021
 * @since 1.8
 */
public class CryptoPropertiesBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	private ConfigurableApplicationContext applicationContext;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		/*
		构建Bean操作需要在 `AbstractApplicationContext.refresh` 内部注册完`BeanPostProcessor`之后执行，
		在`AbstractApplicationContext.prepareRefresh`执行后，会初始化`earlyApplicationEvents`事件集合，
		此处代码在`BeanFactoryPostProcessor`执行阶段向上下文注册特定事件监听，并向事件集合添加此事件对象，
		由`AbstractApplicationContext.registerListeners`方法执行阶段触发，触发时机恰好在常规Bean对象初始化之前
		 */
		applicationContext.addApplicationListener(new CryptoPropertiesBeanPrepareListener());
		applicationContext.publishEvent(new CryptoPropertiesBeanPreparedEvent(applicationContext));
	}

	public void setApplicationContext(ConfigurableApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
