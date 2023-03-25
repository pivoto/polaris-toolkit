package io.polaris.toolkit.spring.crypto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author Qt
 * @version Nov 04, 2021
 * @since 1.8
 */
@Slf4j
public class CryptoPropertiesServletContextInitializer implements ServletContextInitializer, Ordered {
	private final ConfigurableApplicationContext applicationContext;

	public CryptoPropertiesServletContextInitializer(ConfigurableApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@PostConstruct
	void init() {
		// 对于`ServletWebServerApplicationContext`会在`onRefresh`阶段初始化部署特殊Bean，所以注册此初始化器以及时绑定Resolver
		CryptoPropertiesBeanHelper.bindResolver(this.applicationContext);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		// nothing
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}
