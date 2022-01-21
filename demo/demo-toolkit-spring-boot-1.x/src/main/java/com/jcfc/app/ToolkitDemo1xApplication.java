package com.jcfc.app;

import io.polaris.toolkit.spring.annotation.EnableCryptoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Qt
 * @version Oct 29, 2021
 * @since 1.8
 */
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableCryptoProperties
public class ToolkitDemo1xApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(ToolkitDemo1xApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(ToolkitDemo1xApplication.class);
	}
}
