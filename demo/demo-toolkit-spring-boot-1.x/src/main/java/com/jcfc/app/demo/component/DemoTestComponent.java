package com.jcfc.app.demo.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * @author Qt
 * @version Nov 02, 2021
 * @since 1.8
 */
@Component
@Slf4j
@EnableConfigurationProperties(DemoProperties.class)
public class DemoTestComponent implements EnvironmentAware {

	@Autowired
	private DemoProperties demoProperties;
	private Environment environment;

	@PostConstruct
	public void init() {
		List<String> keys = Arrays.asList("toolkit.crypto.password",
				"toolkit.crypto1.password", "demo.plain.text",
				"demo.text01", "demo.text02");
		for (String key : keys) {
			log.warn(key + " : {}", environment.getProperty(key));
		}
		log.warn("demoProperties: {}", demoProperties);
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}


	@Transactional
	public void doSth() {
		log.info("test transaction...");
	}

}
