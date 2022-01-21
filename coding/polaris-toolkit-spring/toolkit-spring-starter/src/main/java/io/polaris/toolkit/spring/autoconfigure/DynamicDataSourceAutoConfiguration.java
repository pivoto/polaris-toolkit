package io.polaris.toolkit.spring.autoconfigure;

import io.polaris.toolkit.spring.annotation.EnableDynamicDataSource;
import io.polaris.toolkit.spring.condition.ConditionalOnEnableDynamicDataSource;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Qt
 * @version Dec 29, 2021
 * @since 1.8
 */
@ConditionalOnEnableDynamicDataSource
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore({JndiDataSourceAutoConfiguration.class, XADataSourceAutoConfiguration.class, DataSourceAutoConfiguration.class})
@ConditionalOnMissingBean(DataSource.class)
public class DynamicDataSourceAutoConfiguration {

	@Configuration
	@EnableDynamicDataSource
	public static class ImportConfiguration {
	}

}
