package io.polaris.demo.mybatis.service;

import java.sql.Connection;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.sql.DataSource;

import io.polaris.demo.mybatis.mapper.DemoCustomMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
@Service
public class DemoCustomService {

	private final DataSource dataSource;
	private final DemoCustomMapper demoCustomMapper;

	public DemoCustomService(DataSource dataSource, DemoCustomMapper demoCustomMapper) {
		this.dataSource = dataSource;
		this.demoCustomMapper = demoCustomMapper;
	}

	public Connection getConnection() {
		return DataSourceUtils.getConnection(dataSource);
	}

	@Transactional
	public <R> R doTransaction(Function<DemoCustomMapper, R> function) {
		return function.apply(demoCustomMapper);
	}

	@Transactional
	public void doTransaction(Consumer<DemoCustomMapper> function) {
		function.accept(demoCustomMapper);
	}
}
