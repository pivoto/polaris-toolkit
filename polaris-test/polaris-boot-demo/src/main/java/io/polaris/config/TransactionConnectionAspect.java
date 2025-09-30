package io.polaris.config;

import java.sql.Connection;

import javax.sql.DataSource;

import io.polaris.core.jdbc.executor.JdbcExecutors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

/**
 * @author Qt
 * @since  Feb 08, 2024
 */
@Slf4j
@Component
@Aspect
public class TransactionConnectionAspect {
	private final DataSource dataSource;

	public TransactionConnectionAspect(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	@Around(
		"@within(org.springframework.stereotype.Service) " +
			"|| @annotation(org.springframework.transaction.annotation.Transactional)"
	)
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		log.info("TransactionConnectionAspect");
		Connection conn = null;
		try {
			conn = DataSourceUtils.getConnection(dataSource);
			JdbcExecutors.setCurrentConnection(conn);
			return joinPoint.proceed();
		} finally {
			JdbcExecutors.clearCurrentConnection();
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

}
