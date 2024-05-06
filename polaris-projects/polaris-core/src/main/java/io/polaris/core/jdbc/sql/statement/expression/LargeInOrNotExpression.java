package io.polaris.core.jdbc.sql.statement.expression;

import io.polaris.core.collection.Iterables;
import io.polaris.core.function.FunctionWithArgs3;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.DynamicNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @author Qt
 * @since 1.8,  Aug 22, 2023
 */
class LargeInOrNotExpression extends BaseExpression {
	private final int limit;
	private final SqlNode logicalNode;
	private final SqlNode conjNode;


	protected LargeInOrNotExpression(int limit, SqlNode logicalNode, SqlNode conjNode) {
		this.limit = limit;
		this.logicalNode = logicalNode;
		this.conjNode = conjNode;
	}


	private List<List<Object>> split(Object varValue) {
		List<List<Object>> list = new ArrayList<>();
		if (varValue instanceof List && varValue instanceof RandomAccess) {
			int size = ((List<?>) varValue).size();
			if (size <= this.limit) {
				list.add((List<Object>) varValue);
				return list;
			}
			for (int i = 0; i < size; i++) {
				int count = Integer.min(size - i, this.limit);
				List<Object> args = new ArrayList<>(count);
				for (int j = 0; j < count; j++) {
					Object o = ((List<?>) varValue).get(i);
					args.add(o);
					i++;
				}
				list.add(args);
			}
			return list;
		}
		if (varValue.getClass().isArray()) {
			int count = 0;
			List<Object> args = new ArrayList<>(count);
			list.add(args);
			int size = Array.getLength(varValue);
			for (int i = 0; i < size; i++) {
				Object o = Array.get(varValue, i);
				count++;
				if (count > this.limit) {
					args = new ArrayList<>(count);
					list.add(args);
					count = 0;
				}
				args.add(o);
			}
			return list;
		}
		if (varValue instanceof Iterable) {
			varValue = ((Iterable<?>) varValue).iterator();
		}
		if (varValue instanceof Map) {
			Collection<?> values = ((Map<?, ?>) varValue).values();
			varValue = values.iterator();
		}
		if (varValue instanceof Iterator) {
			int count = 0;
			List<Object> args = new ArrayList<>(count);
			list.add(args);
			while (((Iterator<?>) varValue).hasNext()) {
				Object o = ((Iterator<?>) varValue).next();
				count++;
				if (count > this.limit) {
					args = new ArrayList<>(count);
					list.add(args);
					count = 0;
				}
				args.add(o);
			}
			return list;
		}

		list.add(Iterables.asList(varValue));
		return list;
	}

	private ContainerNode bind(SqlNode baseSource, SqlNode[] extSources, Object varValue) {
		List<List<Object>> list = split(varValue);
		int size = list.size();
		if (size <= 1) {
			List<Object> args = size == 0 ? Collections.emptyList() : list.get(0);
			ContainerNode container = new ContainerNode();
			container.addNode(baseSource);
			container.addNode(logicalNode);
			container.addNodes(SqlNodes.LEFT_PARENTHESIS);
			DynamicNode varNode = new DynamicNode(nextVarName());
			varNode.bindVarValue(args);
			container.addNode(varNode);
			container.addNodes(SqlNodes.RIGHT_PARENTHESIS);
			return container;
		}
		ContainerNode container = new ContainerNode();
		container.addNodes(SqlNodes.LEFT_PARENTHESIS);
		for (int i = 0; i < list.size(); i++) {
			List<Object> args = list.get(i);
			if (i > 0) {
				container.addNode(conjNode);
			}
			container.addNode(baseSource);
			container.addNode(logicalNode);
			container.addNodes(SqlNodes.LEFT_PARENTHESIS);
			DynamicNode varNode = new DynamicNode(nextVarName());
			varNode.bindVarValue(args);
			container.addNode(varNode);
			container.addNodes(SqlNodes.RIGHT_PARENTHESIS);
		}
		container.addNodes(SqlNodes.RIGHT_PARENTHESIS);
		return container;
	}

	@Override
	protected FunctionWithArgs3<SqlNode, SqlNode[], Object[], ContainerNode> buildArrayFunction() {
		return (baseSource, extSources, bindings) ->
			bind(baseSource, extSources, (bindings == null || bindings.length == 0 ? null : bindings[0]));
	}

	@Override
	protected FunctionWithArgs3<SqlNode, SqlNode[], Map<String, Object>, ContainerNode> buildMapFunction() {
		return (baseSource, extSources, bindings) ->
			bind(baseSource, extSources, (bindings == null || bindings.isEmpty() ? null : bindings.values().iterator().next()));
	}
}
