package io.polaris.toolkit.spring.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author Qt
 * @version Oct 29, 2021
 * @since 1.8
 */
@Slf4j
public class ToolkitEnvironmentPostProcessor implements EnvironmentPostProcessor {
	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		////log.info("****** on environment post processor ");
		System.out.println("****** environment post processor ...");
	}

}
