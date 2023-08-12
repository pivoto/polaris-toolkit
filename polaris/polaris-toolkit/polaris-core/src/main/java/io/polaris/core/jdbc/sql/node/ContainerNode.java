package io.polaris.core.jdbc.sql.node;

import io.polaris.core.jdbc.sql.BoundSql;
import io.polaris.core.jdbc.sql.PreparedSql;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Qt
 * @since 1.8,  Aug 11, 2023
 */
public class ContainerNode implements SqlNode {
	/** 是否跳过语句块 */
	private boolean skip = false;
	private List<SqlNode> subset = new ArrayList<>();

	@Override
	public boolean isContainerNode() {
		return true;
	}


	public boolean skip() {
		return skip;
	}

	public void skip(boolean skip) {
		this.skip = skip;
	}

	@Override
	public PreparedSql asPreparedSql() {
		if (skip()) {
			return PreparedSql.EMPTY;
		}
		StringBuilder text = new StringBuilder();
		List<Object> list = new ArrayList<>();
		for (SqlNode node : subset) {
			if (node instanceof ContainerNode) {
				if (((ContainerNode) node).skip()) {
					continue;
				}
			}
			PreparedSql sql = node.asPreparedSql();
			text.append(sql.getText());
			List<Object> bindings = sql.getBindings();
			if (bindings != null) {
				list.addAll(bindings);
			}
		}
		return new PreparedSql(text.toString(), list);
	}

	@Override
	public BoundSql asBoundSql(VarNameGenerator generator, String openVarToken, String closeVarToken) {
		if (skip()) {
			return BoundSql.EMPTY;
		}
		StringBuilder text = new StringBuilder();
		Map<String, Object> map = new LinkedHashMap<>();
		for (SqlNode node : subset) {
			if (node instanceof ContainerNode) {
				if (((ContainerNode) node).skip()) {
					continue;
				}
			}
			BoundSql sql = node.asBoundSql(generator, openVarToken, closeVarToken);
			text.append(sql.getText());
			Map<String, Object> bindings = sql.getBindings();
			if (bindings != null) {
				map.putAll(bindings);
			}
		}
		return new BoundSql(text.toString(), map);
	}

	@Override
	public List<SqlNode> subset() {
		return subset;
	}

	@Override
	public void addNode(SqlNode sqlNode) {
		addNode(-1, sqlNode);
	}

	@Override
	public void addNode(int i, SqlNode sqlNode) {
		if (i < 0 || i > subset.size()) {
			subset.add(sqlNode);
		} else {
			subset.add(i, sqlNode);
		}
	}

	@Override
	public void addNodes(List<SqlNode> sqlNodes) {
		if (sqlNodes instanceof RandomAccess) {
			for (int i = 0, n = sqlNodes.size(); i < n; i++) {
				addNode(sqlNodes.get(i));
			}
		} else {
			for (SqlNode node : sqlNodes) {
				addNode(node);
			}
		}
	}

	@Override
	public void addNodes(SqlNode... sqlNodes) {
		for (SqlNode node : sqlNodes) {
			addNode(node);
		}
	}

	@Override
	public boolean removeFirstNode(Predicate<SqlNode> predicate) {
		for (int i = subset.size() - 1; i >= 0; i--) {
			SqlNode node = subset.get(i);
			if (predicate.test(node)) {
				subset.remove(i);
				return true;
			} else {
				if (node instanceof ContainerNode) {
					boolean rs = ((ContainerNode) node).removeFirstNode(predicate);
					if (rs) {
						return true;
					}

				}
			}
		}
		return false;
	}

	@Override
	public boolean removeAllNodes(Predicate<SqlNode> predicate) {
		int count = 0;
		for (int i = subset.size() - 1; i >= 0; i--) {
			SqlNode node = subset.get(i);

			if (predicate.test(node)) {
				subset.remove(i);
				count++;
			} else {
				if (node instanceof ContainerNode) {
					((ContainerNode) node).removeAllNodes(predicate);
					count++;
				}
			}
		}
		return count > 0;
	}

	@Override
	public void clearSkippedNodes() {
		removeAllNodes(node -> node instanceof ContainerNode && ((ContainerNode) node).skip);
	}

	@Override
	public boolean containsVarName(String key) {
		for (int i = 0, n = subset.size(); i < n; i++) {
			SqlNode node = subset.get(i);
			if (node instanceof VarNode) {
				if (Objects.equals(((VarNode) node).getVarName(), key)) {
					return true;
				}
			} else if (node instanceof ContainerNode) {
				if (((ContainerNode) node).containsVarName(key)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void visitNode(Consumer<SqlNode> visitor) {
		for (int i = 0, n = subset.size(); i < n; i++) {
			SqlNode node = subset.get(i);
			if (node instanceof ContainerNode) {
				((ContainerNode) node).visitNode(visitor);
			} else {
				visitor.accept(node);
			}
		}
	}

	@Override
	public void bindVarValues(Map<String, Object> params) {
		bindVarValues(params, true);
	}

	@Override
	public void bindVarValues(Map<String, Object> params, boolean ignoreNull) {
		visitNode(node -> {
			if (node instanceof VarNode) {
				VarNode varNode = (VarNode) node;
				Object param = params.get(varNode.getVarName());
				if (param != null || !ignoreNull) {
					varNode.bindVarValue(param);
				}
			}
		});
	}

	@Override
	public void skipIfMissingVarParameter() {
		for (int i = subset.size() - 1; i >= 0; i--) {
			SqlNode node = subset.get(i);
			if (node.isVarNode()) {
				if (node.getVarValue() == null) {
					this.skip = true;
					break;
				}
			} else if (node instanceof ContainerNode) {
				((ContainerNode) node).skipIfMissingVarParameter();
			}
		}
	}
}
