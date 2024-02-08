package io.polaris.core.jdbc.executor;

import java.sql.Connection;
import java.util.Map;

import io.polaris.core.jdbc.base.JdbcOptions;
import io.polaris.core.jdbc.base.ResultExtractor;
import io.polaris.core.jdbc.base.ResultRowMapper;
import io.polaris.core.jdbc.base.ResultVisitor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Qt
 * @since 1.8,  Feb 07, 2024
 */
@Getter
public class MethodArgs {
	private final JdbcOptions options;
	private final Connection connection;
	private final Map<String, Object> bindings;
	private final Object noKeyArg;
	private final ResultExtractor<?> extractor;
	private final ResultVisitor<?> visitor;
	private final ResultRowMapper<?> visitorRowMapper;

	protected MethodArgs(JdbcOptions options, Connection connection, Map<String, Object> bindings, Object noKeyArg, ResultExtractor<?> extractor, ResultVisitor<?> visitor, ResultRowMapper<?> visitorRowMapper) {
		this.options = options;
		this.connection = connection;
		this.bindings = bindings;
		this.noKeyArg = noKeyArg;
		this.extractor = extractor;
		this.visitor = visitor;
		this.visitorRowMapper = visitorRowMapper;
	}
}
