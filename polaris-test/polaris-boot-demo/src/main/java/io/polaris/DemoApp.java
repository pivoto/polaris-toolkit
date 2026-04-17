package io.polaris;

import io.polaris.core.reflect.Reflects;
import io.polaris.demo.aop.SourceBean;
import io.polaris.mybatis.support.MapperHelperContext;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.reactive.DispatcherHandler;

/**
 * @author Qt
 * @since Aug 24, 2023
 */
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableTransactionManagement(proxyTargetClass = true)
public class DemoApp {

	public static void main(String[] args) {
		MapperHelperContext.enableEnhanceBoundSql(true);

		SpringApplication springApplication = new SpringApplication(DemoApp.class);
		// 同时添加 spring-boot-starter-web 和 spring-boot-starter-webflux 模块会导致Spring Boot自动配置Spring MVC，而不是WebFlux
		// 这样通过添加依赖来使用响应式 WebClient
		// 这里可手动设置 SpringApplication.setWebApplicationType(WebApplicationType.REACTIVE) 启用WebFlux。
		if (ClassUtils.isPresent("org.springframework.web.reactive.DispatcherHandler", DemoApp.class.getClassLoader())) {
			springApplication.setWebApplicationType(WebApplicationType.REACTIVE);
		}
		ConfigurableApplicationContext ctx = springApplication.run(args);

		SourceBean bean = ctx.getBean(SourceBean.class);
		bean.func1();
		bean.call1();
	}

}
