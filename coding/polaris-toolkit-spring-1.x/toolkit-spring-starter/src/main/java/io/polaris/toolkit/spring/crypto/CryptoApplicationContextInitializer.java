package io.polaris.toolkit.spring.crypto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * @author Qt
 * @version Oct 29, 2021
 * @since 1.8
 */
@Slf4j
public class CryptoApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		// add BeanFactoryPostProcessor
		CryptoPropertiesBeanHelper.bindInitializer(applicationContext);
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
