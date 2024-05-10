package io.polaris.core.jdbc.sql.statement.expression;

import java.util.Map;
import java.util.function.Supplier;

import io.polaris.core.function.FunctionWithArgs3;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.DynamicNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;

/**
 * @author Qt
 * @since  Aug 22, 2023
 */
public class LikeExpression extends BaseExpression {
	public static final LikeExpression CONTAINS = new LikeExpression(true, true, false);
	public static final LikeExpression STARTS_WITH = new LikeExpression(true, false, false);
	public static final LikeExpression ENDS_WITH = new LikeExpression(false, true, false);
	public static final LikeExpression NOT_CONTAINS = new LikeExpression(true, true, true);
	public static final LikeExpression NOT_STARTS_WITH = new LikeExpression(true, false, true);
	public static final LikeExpression NOT_ENDS_WITH = new LikeExpression(false, true, true);
	private final boolean leftFuzzy;
	private final boolean rightFuzzy;
	private final boolean not;

	public LikeExpression(boolean leftFuzzy, boolean rightFuzzy, boolean not) {
		this.leftFuzzy = leftFuzzy;
		this.rightFuzzy = rightFuzzy;
		this.not = not;
	}

	private ContainerNode bind(SqlNode baseSource, SqlNode[] extSources, Supplier<CharSequence> getter) {
		ContainerNode container = new ContainerNode();
		container.addNode(baseSource);
		if (not) {
			container.addNode(SqlNodes.NOT);
		}
		container.addNode(SqlNodes.LIKE);
		DynamicNode varNode = new DynamicNode(nextVarName());

		StringBuilder val = new StringBuilder(getter.get());
		if (rightFuzzy && (val.length() == 0 || val.charAt(val.length() - 1) != '%')) {
			val.append('%');
		}
		if (leftFuzzy && (val.length() == 0 || val.charAt(0) != '%')) {
			val.insert(0, '%');
		}
		varNode.bindVarValue(val.toString());
		container.addNode(varNode);
		return container;
	}

	@Override
	protected FunctionWithArgs3<SqlNode, SqlNode[], Object[], ContainerNode> buildArrayFunction() {
		return (baseSource, extSources, bindings) -> bind(baseSource, extSources,
			() -> (CharSequence) (bindings == null || bindings.length == 0 ? null : bindings[0]));
	}

	@Override
	protected FunctionWithArgs3<SqlNode, SqlNode[], Map<String, Object>, ContainerNode> buildMapFunction() {
		return (baseSource, extSources, bindings) -> bind(baseSource, extSources,
			() -> (CharSequence) (bindings == null || bindings.isEmpty() ? null : bindings.values().iterator().next()));
	}
}
