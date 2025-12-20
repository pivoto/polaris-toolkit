package io.polaris.core.jdbc.sql.statement.expression;

import java.util.Map;
import java.util.function.Supplier;

import io.polaris.core.function.FunctionWithArgs3;
import io.polaris.core.jdbc.sql.VarRef;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.DynamicNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.map.Maps;

/**
 * @author Qt
 * @since Aug 22, 2023
 */
public class LikeExpression extends BaseExpression {
	public static final LikeExpression LIKE = new LikeExpression(false, false, false);
	public static final LikeExpression NOT_LIKE = new LikeExpression(false, false, true);
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

	private ContainerNode bind(SqlNode baseSource, SqlNode[] extSources, Supplier<Binding> getter) {
		ContainerNode container = new ContainerNode();
		container.addNode(baseSource);
		if (not) {
			container.addNode(SqlNodes.NOT);
		}
		container.addNode(SqlNodes.LIKE);

		DynamicNode varNode = new DynamicNode(nextVarName());

		StringBuilder varStr = new StringBuilder();
		Binding bindings = getter.get();
		Character escape = bindings.escape;

		Map<String, String> varProps = null;
		Object obj = bindings.value;
		if (obj instanceof VarRef) {
			varProps = ((VarRef<?>) obj).getProps();
			varStr.append(((VarRef<?>) obj).getValue());
		} else {
			varStr.append(obj);
		}
		if (leftFuzzy && (varStr.length() == 0 || varStr.charAt(0) != '%')) {
			varStr.insert(0, '%');
		}
		if (rightFuzzy && (varStr.length() == 0 || varStr.charAt(varStr.length() - 1) != '%')) {
			varStr.append('%');
		}
		if (Maps.isNotEmpty(varProps)) {
			varNode.bindVarValue(VarRef.of(varStr.toString(), varProps));
		} else {
			varNode.bindVarValue(varStr.toString());
		}
		container.addNode(varNode);
		if (escape != null) {
			container.addNode(SqlNodes.ESCAPE);
			container.addNode(SqlNodes.text("'" + escape + "'"));
		}
		return container;
	}

	@Override
	protected FunctionWithArgs3<SqlNode, SqlNode[], Object[], ContainerNode> buildArrayFunction() {
		return (baseSource, extSources, bindings) -> bind(baseSource, extSources,
			() -> {
				Binding target = new Binding();
				if (bindings != null) {
					if (bindings.length >= 1) {
						target.value = bindings[0];
					}
					if (bindings.length >= 2) {
						if (bindings[1] instanceof Character) {
							target.escape = (Character) bindings[1];
						}
					}
				}
				return target;
			});
	}

	@Override
	protected FunctionWithArgs3<SqlNode, SqlNode[], Map<String, Object>, ContainerNode> buildMapFunction() {
		return (baseSource, extSources, bindings) -> bind(baseSource, extSources,
			() -> {
				Binding target = new Binding();
				if (bindings != null) {
					int size = bindings.size();
					if (size == 1) {
						target.value = bindings.values().iterator().next();
					} else {
						int i = 0;
						for (Object o : bindings.values()) {
							if (i >= 2) {
								break;
							}
							if (o instanceof Character) {
								if (target.escape == null) {
									target.escape = (Character) o;
									i++;
								}
							} else {
								if (target.value == null) {
									target.value = o;
									i++;
								}
							}
						}
					}
				}
				return target;
			});
	}

	private static class Binding {
		private Object value;
		private Character escape;
	}
}
