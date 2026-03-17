package io.polaris.config;

import io.polaris.json.Jacksons;
import io.polaris.mybatis.interceptor.DataAuthInterceptor;
import io.polaris.mybatis.interceptor.DynamicResultMappingInterceptor;
import io.polaris.mybatis.interceptor.MybatisLogInterceptor;
import io.polaris.mybatis.interceptor.BoundSqlEnhanceInterceptor;
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
	public ConfigurationCustomizer configurationCustomizer() {
		return configuration -> {
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			TypeHandlers.registerAll(configuration);
			// 按 org.apache.ibatis.plugin.InterceptorChain 代理规则，插件执行顺序与添加顺序相反
			configuration.addInterceptor(new DynamicResultMappingInterceptor());
			configuration.addInterceptor(new MybatisLogInterceptor());
			configuration.addInterceptor(new PageInterceptor());
			configuration.addInterceptor(new DataAuthInterceptor());
			configuration.addInterceptor(new BoundSqlEnhanceInterceptor());
		};
	}
}
