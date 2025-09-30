package io.polaris.config;

import io.polaris.mybatis.interceptor.DataAuthInterceptor;
import io.polaris.mybatis.interceptor.DynamicUseGeneratedKeysInterceptor;
import io.polaris.mybatis.interceptor.MybatisLogInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.github.pagehelper.PageInterceptor;

/**
 * @author Qt
 * @since Jul 04, 2024
 */
@Configuration
public class DemoConfiguration {

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
	public DynamicUseGeneratedKeysInterceptor dynamicUseGeneratedKeysInterceptor() {
		return new DynamicUseGeneratedKeysInterceptor();
	}

}
