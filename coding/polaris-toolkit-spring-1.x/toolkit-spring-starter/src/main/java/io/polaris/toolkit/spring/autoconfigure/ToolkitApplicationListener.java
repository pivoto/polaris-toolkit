package io.polaris.toolkit.spring.autoconfigure;

import io.polaris.toolkit.spring.crypto.CryptoPropertiesBeanHelper;
import io.polaris.toolkit.spring.util.Contexts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author Qt
 * @version Oct 29, 2021
 * @since 1.8
 */
@Slf4j
public class ToolkitApplicationListener implements ApplicationListener<ApplicationEvent>, Ordered {
	@Override
	public void onApplicationEvent(ApplicationEvent event) {

		if (event instanceof ApplicationStartingEvent) {
			System.out.println("****** application starting ...");
			return;
		}
		if (event instanceof ApplicationEnvironmentPreparedEvent) {
			log.info("****** on environment prepared ");
			onEnvironmentPrepared((ApplicationEnvironmentPreparedEvent) event);
			return;
		}

		if (event instanceof ApplicationPreparedEvent) {
			log.info("****** on application prepared ");
			onApplicationPrepare((ApplicationPreparedEvent) event);
			return;
		}

		if (event instanceof ApplicationReadyEvent) {
			log.info("****** on application ready ");
			onApplicationReady((ApplicationReadyEvent) event);
			return;
		}

		if (event instanceof ApplicationFailedEvent) {
			log.info("****** on application failed ");
			onApplicationFailed((ApplicationFailedEvent) event);
			return;
		}

		log.info("****** on event: {}", event.getClass().getSimpleName());
	}


	private void onEnvironmentPrepared(ApplicationEnvironmentPreparedEvent event) {
		ConfigurableEnvironment environment = event.getEnvironment();
		SpringApplication springApplication = event.getSpringApplication();
		// bind environment
		Contexts.setEnvironment(environment);

		// banner
		DefaultBanner.attachToIfNecessary(springApplication);

		// crypto properties
		CryptoPropertiesBeanHelper.determineCryptoCapability(environment, springApplication);
	}


	private void onApplicationPrepare(ApplicationPreparedEvent event) {
		// bind application context
		Contexts.setApplicationContext(event.getApplicationContext());
	}

	private void onApplicationReady(ApplicationReadyEvent event) {
	}

	private void onApplicationFailed(ApplicationFailedEvent event) {
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
}
