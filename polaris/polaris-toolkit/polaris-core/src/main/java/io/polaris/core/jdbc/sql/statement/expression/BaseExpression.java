package io.polaris.core.jdbc.sql.statement.expression;

import io.polaris.core.function.TernaryFunction;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.string.Hex;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

/**
 * @author Qt
 * @since 1.8,  Aug 21, 2023
 */
public abstract class BaseExpression implements Expression {
	private static final AtomicLong seq = new AtomicLong();
	private final TernaryFunction<SqlNode,SqlNode[], Object[], ContainerNode> builder;

	public BaseExpression(TernaryFunction<SqlNode,SqlNode[], Object[], ContainerNode> builder) {
		this.builder = builder;
	}

	public BaseExpression() {
		this.builder = newBuilder();
	}

	protected TernaryFunction<SqlNode,SqlNode[], Object[], ContainerNode> newBuilder() {
		throw new UnsupportedOperationException();
	}

	protected String nextVarName() {
		return "_var" + Hex.formatHex(seq.incrementAndGet());
	}

	@Override
	public SqlNode toSqlNode(SqlNode baseSource, SqlNode[] extSources, Object... bindings) {
		return builder.apply(baseSource, extSources, bindings);
	}
}
