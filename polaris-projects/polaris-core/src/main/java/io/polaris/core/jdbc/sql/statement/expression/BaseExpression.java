package io.polaris.core.jdbc.sql.statement.expression;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import io.polaris.core.function.TernaryFunction;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.string.Hex;

/**
 * @author Qt
 * @since 1.8,  Aug 21, 2023
 */
public abstract class BaseExpression implements Expression {
	private static final AtomicLong seq = new AtomicLong();
	private final TernaryFunction<SqlNode, SqlNode[], Object[], ContainerNode> arrayFunc;
	private final TernaryFunction<SqlNode, SqlNode[], Map<String, Object>, ContainerNode> mapFunc;

	public BaseExpression() {
		this.arrayFunc = buildArrayFunction();
		this.mapFunc = buildMapFunction();
	}

	protected abstract TernaryFunction<SqlNode, SqlNode[], Object[], ContainerNode> buildArrayFunction();

	protected abstract TernaryFunction<SqlNode, SqlNode[], Map<String, Object>, ContainerNode> buildMapFunction();

	protected String nextVarName() {
		return "_var" + Hex.formatHex(seq.incrementAndGet());
	}

	@Override
	public SqlNode toSqlNode(SqlNode baseSource, SqlNode[] extSources, Map<String, Object> bindings) {
		return mapFunc.apply(baseSource, extSources, bindings);
	}

	@Override
	public SqlNode toSqlNode(SqlNode baseSource, SqlNode[] extSources, Object[] bindings) {
		return arrayFunc.apply(baseSource, extSources, bindings);
	}
}
