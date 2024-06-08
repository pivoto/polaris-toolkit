package io.polaris.demo.mybatis.service;

import io.polaris.demo.mybatis.mapper.DemoUserEntityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Qt
 * @since  Aug 25, 2023
 */
@Service
public class DemoUserService {

	private final DemoUserEntityMapper demoUserEntityMapper;

	public DemoUserService(DemoUserEntityMapper demoUserEntityMapper) {
		this.demoUserEntityMapper = demoUserEntityMapper;
	}

	@Transactional
	public <R> R doTransaction(Function<DemoUserEntityMapper, R> function) {
		return function.apply(demoUserEntityMapper);
	}

	@Transactional
	public void doTransaction(Consumer<DemoUserEntityMapper> function) {
		function.accept(demoUserEntityMapper);
	}
}
