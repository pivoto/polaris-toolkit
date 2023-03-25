package io.polaris.toolkit.spring.jdbc.init;

import io.polaris.toolkit.spring.jdbc.DynamicDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

/**
 * @author Qt
 * @version Mar 04, 2022
 * @since 1.8
 */
class DataSourceInitializerPostProcessor implements BeanPostProcessor, Ordered {

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 1;
	}

	@Autowired
	private BeanFactory beanFactory;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof DynamicDataSource) {
			// force initialization of this bean as soon as we see a DataSource
			this.beanFactory.getBean(DataSourceInitializerInvoker.class);
		}
		return bean;
	}
}
