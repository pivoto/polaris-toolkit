package io.polaris.toolkit.spring.autoconfigure;

import io.polaris.toolkit.spring.constants.ToolkitConstants;
import io.polaris.toolkit.spring.crypto.CryptoConfigurationProperties;
import io.polaris.toolkit.spring.crypto.CryptoPropertiesServletContextInitializer;
import io.polaris.toolkit.spring.crypto.CryptoPropertyResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @author Qt
 * @version Oct 29, 2021
 * @since 1.8
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(CryptoConfigurationProperties.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class CryptoPropertyAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(name = ToolkitConstants.TOOLKIT_CRYPTO_ENABLED, havingValue = "true")
	CryptoPropertyResolver cryptoPropertyResolver(CryptoConfigurationProperties properties) {
		return new CryptoPropertyResolver(properties);
	}

	@Bean
	@ConditionalOnClass(ServletContextInitializer.class)
	@ConditionalOnBean(CryptoPropertyResolver.class)
	ServletContextInitializer cryptoPropertiesServletContextInitializer(ConfigurableApplicationContext applicationContext) {
		return new CryptoPropertiesServletContextInitializer(applicationContext);
	}

}
