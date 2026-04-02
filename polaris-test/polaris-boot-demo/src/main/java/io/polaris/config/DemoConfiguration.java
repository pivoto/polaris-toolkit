package io.polaris.config;

import io.polaris.json.Jacksons;
import io.polaris.mybatis.enhance.EnhancedConfiguration;
import io.polaris.mybatis.enhance.log.DebuggableLog;
import io.polaris.mybatis.enhance.log.OriginalLogFactory;
import io.polaris.mybatis.interceptor.DataAuthInterceptor;
import io.polaris.mybatis.interceptor.DynamicEnhancedInterceptor;
import io.polaris.mybatis.interceptor.MybatisLogInterceptor;
import io.polaris.mybatis.interceptor.BoundSqlEnhanceInterceptor;
import io.polaris.mybatis.interceptor.MybatisSqlLogInterceptor;
import io.polaris.mybatis.type.TypeHandlers;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInterceptor;

/**
 * @author Qt
 * @since Jul 04, 2024
 */
@Configuration
@EnableConfigurationProperties(MybatisProperties.class)
@AutoConfigureBefore(org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration.class) // 确保在MybatisAutoConfiguration之前执行，以应用EnhancedConfiguration
public class DemoConfiguration {

	public DemoConfiguration(MybatisProperties properties) throws Throwable {
		org.apache.ibatis.session.Configuration configuration = properties.getConfiguration();
		EnhancedConfiguration enhancedConfiguration = new EnhancedConfiguration(configuration);
		OriginalLogFactory.useOriginalLogging();
		enhancedConfiguration.setLogImpl(DebuggableLog.class);
		properties.setConfiguration(enhancedConfiguration);
	}

	@Bean
	@Primary
	public ObjectMapper objectMapper() {
		// 覆盖默认的ObjectMapper
		return Jacksons.defaultObjectMapper();
	}

	@Bean
	public ConfigurationCustomizer configurationCustomizer() {
		return configuration -> {
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			TypeHandlers.registerAll(configuration);
			// 按 org.apache.ibatis.plugin.InterceptorChain 代理规则，插件执行顺序与添加顺序相反
			configuration.addInterceptor(new DynamicEnhancedInterceptor());
			configuration.addInterceptor(new MybatisSqlLogInterceptor());
			configuration.addInterceptor(new MybatisLogInterceptor());
			configuration.addInterceptor(new PageInterceptor());
			configuration.addInterceptor(new DataAuthInterceptor());
			configuration.addInterceptor(new BoundSqlEnhanceInterceptor());
		};
	}
}
