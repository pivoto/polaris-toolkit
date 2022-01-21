package com.jcfc.app.config;

import com.jcfc.app.aop.DispatcherServletSpy;
import com.jcfc.app.aop.StdHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author Qt
 * @version Oct 29, 2021
 * @since 1.8
 */
@Configuration
public class MvcConfig extends WebMvcConfigurationSupport {

	@Bean
	public DispatcherServletSpy dispatcherServletSpy(@Autowired DispatcherServlet delegate) {
		return new DispatcherServletSpy(delegate);
	}

	@Bean
	public StdHandlerInterceptor stdHandlerInterceptor() {
		return new StdHandlerInterceptor();
	}

	@Override
	protected void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(stdHandlerInterceptor());
	}
}
