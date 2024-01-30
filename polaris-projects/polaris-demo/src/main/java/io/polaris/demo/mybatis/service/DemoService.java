package io.polaris.demo.mybatis.service;

import java.util.function.Consumer;
import java.util.function.Function;

import io.polaris.demo.mybatis.mapper.DemoMapper;
import io.polaris.demo.mybatis.mapper.DemoUserEntityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
@Service
public class DemoService {

	private final DemoMapper demoMapper;

	public DemoService(DemoMapper demoMapper) {
		this.demoMapper = demoMapper;
	}


	@Transactional
	public <R> R doTransaction(Function<DemoMapper, R> function) {
		return function.apply(demoMapper);
	}

	@Transactional
	public void doTransaction(Consumer<DemoMapper> function) {
		function.accept(demoMapper);
	}
}
