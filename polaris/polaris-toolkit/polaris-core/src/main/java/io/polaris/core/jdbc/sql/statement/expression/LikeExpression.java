package io.polaris.core.jdbc.sql.statement.expression;

import io.polaris.core.function.TernaryFunction;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.DynamicNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.TextNode;

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

	@Override
	protected TernaryFunction<SqlNode,SqlNode[], Object[], ContainerNode> newBuilder() {
		return (baseSource,extSources, bindings) -> {
			ContainerNode container = new ContainerNode();
			container.addNode(baseSource);
			container.addNode(new TextNode(" LIKE "));
			DynamicNode varNode = new DynamicNode(nextVarName());
			StringBuilder val = new StringBuilder((CharSequence) bindings[0]);
			if (rightFuzzy && (val.length() == 0 || val.charAt(val.length() - 1) != '%')) {
				val.append('%');
			}
			if (leftFuzzy && (val.length() == 0 || val.charAt(0) != '%')) {
				val.insert(0, '%');
			}
			varNode.bindVarValue(val.toString());
			container.addNode(varNode);
			return container;
		};
	}
}
