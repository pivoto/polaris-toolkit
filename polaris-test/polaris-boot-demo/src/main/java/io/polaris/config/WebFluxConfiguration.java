package io.polaris.config;

import io.polaris.demo.mybatis.handler.MyUserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


/**
 * @author Qt
 * @since Apr 17, 2026
 */
@Configuration(proxyBeanMethods = false)
public class WebFluxConfiguration {

	private static final RequestPredicate ACCEPT_JSON = RequestPredicates.accept(MediaType.APPLICATION_JSON);

	@Bean
	public RouterFunction<ServerResponse> monoRouterFunction(MyUserHandler userHandler) {
		return RouterFunctions.route().path("/webflux", builder -> {
				builder
					.GET("/{user}", ACCEPT_JSON, userHandler::getUser)
					.GET("/{user}/customers", ACCEPT_JSON, userHandler::getUserCustomers)
					.DELETE("/{user}", ACCEPT_JSON, userHandler::deleteUser)
				;
			})
			.build();
	}


}
