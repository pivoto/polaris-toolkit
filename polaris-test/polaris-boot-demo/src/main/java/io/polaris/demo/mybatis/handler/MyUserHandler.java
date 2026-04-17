package io.polaris.demo.mybatis.handler;

import java.util.HashMap;
import java.util.Map;

import groovyjarjarantlr.collections.List;
import io.polaris.core.collection.Lists;
import io.polaris.core.map.Maps;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author Qt
 * @since Apr 17, 2026
 */
@Component
public class MyUserHandler {
	public Mono<ServerResponse> getUser(ServerRequest request) {
		return ServerResponse.ok()
			.body(Mono.just(Maps.asMap(new HashMap<>(), "id", 1L)), Map.class);
	}

	public Mono<ServerResponse> getUserCustomers(ServerRequest request) {
		return ServerResponse.ok()
			.body(Mono.just(Lists.asList(Maps.asMap(new HashMap<>(), "id", 2L))), List.class);
	}

	public Mono<ServerResponse> deleteUser(ServerRequest request) {
		return ServerResponse.ok()
			.body(Mono.just("ok"), String.class);
	}
}
