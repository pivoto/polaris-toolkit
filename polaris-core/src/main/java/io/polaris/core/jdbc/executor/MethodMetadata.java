package io.polaris.core.jdbc.executor;

import java.util.Map;
import java.util.function.Function;

import io.polaris.core.jdbc.base.ResultExtractor;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.lang.JavaType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Qt
 * @since  Feb 07, 2024
 */
@Getter
public class MethodMetadata {
	private final JavaType<?> returnType;
	private final boolean select;
	private final Function<Object[], MethodArgs> argsBuilder;
	private final Function<Map<String, Object>, SqlNode> sqlBuilder;
	private final ResultExtractor<?> extractor;

	protected MethodMetadata(JavaType<?> returnType, boolean select, Function<Object[], MethodArgs> argsBuilder, Function<Map<String, Object>, SqlNode> sqlBuilder, ResultExtractor<?> extractor) {
		this.returnType = returnType;
		this.select = select;
		this.argsBuilder = argsBuilder;
		this.sqlBuilder = sqlBuilder;
		this.extractor = extractor;
	}
}
