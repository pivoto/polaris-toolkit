package io.polaris.toolkit.spring.crypto;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Qt
 * @version Nov 02, 2021
 * @since 1.8
 */
public class CryptoPropertiesBeanPreparedEvent extends ApplicationEvent {
	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the object on which the event initially occurred (never {@code null})
	 */
	public CryptoPropertiesBeanPreparedEvent(Object source) {
		super(source);
	}

	public ConfigurableApplicationContext getApplicationContext() {
		return (ConfigurableApplicationContext) source;
	}

	public ConfigurableListableBeanFactory getBeanFactory() {
		return getApplicationContext().getBeanFactory();
	}

}

