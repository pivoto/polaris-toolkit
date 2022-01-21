package io.polaris.toolkit.spring.autoconfigure;

import io.polaris.toolkit.spring.util.Contexts;
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
public class ToolkitApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		log.info("****** application context initialize ");
		Contexts.setApplicationContext(applicationContext);
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

}
