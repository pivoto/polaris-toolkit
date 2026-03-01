package io.polaris.config;

import io.polaris.json.Jacksons;
import io.polaris.mybatis.interceptor.DataAuthInterceptor;
import io.polaris.mybatis.interceptor.DynamicResultMappingInterceptor;
import io.polaris.mybatis.interceptor.MybatisLogInterceptor;
import io.polaris.mybatis.type.TypeHandlers;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInterceptor;

/**
 * @author Qt
 * @since Jul 04, 2024
 */
@Configuration
public class DemoConfiguration {

	@Bean
	@Primary
	public ObjectMapper objectMapper() {
		// 覆盖默认的ObjectMapper
		return Jacksons.defaultObjectMapper();
	}

	@Bean
	@Order(1000)
	public DataAuthInterceptor dataAuthInterceptor() {
		return new DataAuthInterceptor();
	}

	@Bean
	@Order(500)
	public PageInterceptor pageInterceptor() {
		return new PageInterceptor();
	}

	@Bean
	@Order(100)
	public MybatisLogInterceptor mybatisLogInterceptor() {
		return new MybatisLogInterceptor();
	}


	@Bean
	@Order(Integer.MAX_VALUE)
	public DynamicResultMappingInterceptor dynamicResultMappingInterceptor() {
		return new DynamicResultMappingInterceptor();
	}


	@Bean
	public ConfigurationCustomizer configurationCustomizer() {
		return configuration -> {
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			TypeHandlers.registerAll(configuration);
		};
	}
}
