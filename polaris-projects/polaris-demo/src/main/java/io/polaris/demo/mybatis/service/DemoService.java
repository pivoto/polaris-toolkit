package io.polaris.demo.mybatis.service;

import java.sql.Connection;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.sql.DataSource;

import io.polaris.demo.mybatis.mapper.DemoMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
@Service
public class DemoService {

	private final DataSource dataSource;
	private final DemoMapper demoMapper;

	public DemoService(DataSource dataSource, DemoMapper demoMapper) {
		this.dataSource = dataSource;
		this.demoMapper = demoMapper;
	}

	public Connection getConnection() {
		return DataSourceUtils.getConnection(dataSource);
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
