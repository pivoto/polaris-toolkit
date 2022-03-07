package com.jcfc.app;

import io.polaris.toolkit.spring.annotation.EnableCryptoProperties;
import io.polaris.toolkit.spring.annotation.EnableDynamicTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Qt
 * @version Oct 29, 2021
 * @since 1.8
 */
@SpringBootApplication(
//		exclude = {TransactionAutoConfiguration.class}
)
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableCryptoProperties
@EnableDynamicTransaction
//@EnableTransactionManagement
public class ToolkitDemoApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(ToolkitDemoApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(ToolkitDemoApplication.class);
	}
}
