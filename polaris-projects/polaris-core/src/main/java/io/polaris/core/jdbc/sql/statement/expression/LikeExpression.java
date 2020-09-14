package io.polaris.core.jdbc.sql.statement.expression;

import io.polaris.core.function.TernaryFunction;
import io.polaris.core.jdbc.sql.node.*;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8,  Aug 22, 2023
 */
public class LikeExpression extends BaseExpression {
	private boolean leftFuzzy = true;
	private boolean rightFuzzy = true;

	public LikeExpression(boolean leftFuzzy, boolean rightFuzzy) {
		this.leftFuzzy = leftFuzzy;
		this.rightFuzzy = rightFuzzy;
	}

	private ContainerNode bind(SqlNode baseSource, SqlNode[] extSources, Supplier<CharSequence> getter) {
		ContainerNode container = new ContainerNode();
		container.addNode(baseSource);
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
	protected TernaryFunction<SqlNode, SqlNode[], Object[], ContainerNode> buildArrayFunction() {
		return (baseSource, extSources, bindings) -> bind(baseSource, extSources,
			() -> (CharSequence) (bindings == null || bindings.length == 0 ? null : bindings[0]));
	}

	@Override
	protected TernaryFunction<SqlNode, SqlNode[], Map<String, Object>, ContainerNode> buildMapFunction() {
		return (baseSource, extSources, bindings) -> bind(baseSource, extSources,
			() -> (CharSequence) (bindings == null || bindings.isEmpty() ? null : bindings.values().iterator().next()));
	}
}
