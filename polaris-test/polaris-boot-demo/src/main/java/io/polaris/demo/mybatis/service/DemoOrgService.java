package io.polaris.demo.mybatis.service;

import java.util.function.Consumer;
import java.util.function.Function;

import io.polaris.demo.mybatis.mapper.DemoOrgAutoMapper;
import io.polaris.demo.mybatis.mapper.DemoUserEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
@Service
@RequiredArgsConstructor
public class DemoOrgService {

	private final DemoOrgAutoMapper demoOrgAutoMapper;

	@Transactional
	public <R> R doTransaction(Function<DemoOrgAutoMapper, R> function) {
		return function.apply(demoOrgAutoMapper);
	}

	@Transactional
	public void doTransaction(Consumer<DemoOrgAutoMapper> function) {
		function.accept(demoOrgAutoMapper);
	}
}
