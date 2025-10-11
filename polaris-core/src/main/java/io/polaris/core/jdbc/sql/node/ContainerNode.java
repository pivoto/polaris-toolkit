package io.polaris.core.jdbc.sql.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.polaris.core.jdbc.sql.BoundSql;
import io.polaris.core.jdbc.sql.PreparedSql;

/**
 * @author Qt
 * @since Aug 11, 2023
 */
public class ContainerNode implements SqlNode, Cloneable {
	public static final ContainerNode EMPTY = new ContainerNode(Collections.emptyList());
	/** 是否跳过语句块 */
	private boolean skip = false;
	private List<SqlNode> subset = new ArrayList<>();
	private final SqlNode delimiter;
	private final SqlNode prefix;
	private final SqlNode suffix;

	public ContainerNode(SqlNode delimiter, SqlNode prefix, SqlNode suffix) {
		this.delimiter = delimiter;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	public ContainerNode(SqlNode delimiter, SqlNode prefix) {
		this(delimiter, prefix, null);
	}

	public ContainerNode(SqlNode delimiter) {
		this(delimiter, null, null);
	}

	public ContainerNode() {
		this(null, null, null);
	}

	private ContainerNode(List<SqlNode> subset) {
		this(null, null, null);
		this.subset = subset;
	}

	@Override
	public String toString() {
		return asPreparedSql().getText();
	}

	@Override
	public boolean isContainerNode() {
		return true;
	}

	@Override
	public boolean isSkipped() {
		return skip || subset.isEmpty();
	}

	@Override
	public void skip(boolean skip) {
		this.skip = skip;
	}

	@Override
	public PreparedSql asPreparedSql() {
		if (isSkipped()) {
			return PreparedSql.EMPTY;
		}
		boolean first = true;
		StringBuilder text = new StringBuilder();
		List<Object> list = new ArrayList<>();
		for (SqlNode node : subset) {
			if (node.isSkipped()) {
				continue;
			}
			if (first) {
				first = false;
				addToPreparedSql(text, list, prefix);
			} else {
				addToPreparedSql(text, list, delimiter);
			}
			addToPreparedSql(text, list, node);
		}
		if (!first) {
			addToPreparedSql(text, list, suffix);
		}
		return new PreparedSql(text.toString(), list);
	}

	private void addToPreparedSql(StringBuilder sb, List<Object> list, SqlNode node) {
		if (node == null) {
			return;
		}
		PreparedSql sql = node.asPreparedSql();
		String text = sql.getText();
		if (text != null) {
			sb.append(text);
		}
		List<Object> bindings = sql.getBindings();
		if (bindings != null) {
			list.addAll(bindings);
		}
	}

	@Override
	public BoundSql asBoundSql(Predicate<String> varPropFilter, VarNameGenerator generator, String openVarToken, String closeVarToken) {
		if (isSkipped()) {
			return BoundSql.EMPTY;
		}
		boolean first = true;
		StringBuilder text = new StringBuilder();
		Map<String, Object> map = new LinkedHashMap<>();
		for (SqlNode node : subset) {
			if (node.isSkipped()) {
				continue;
			}
			if (first) {
				first = false;
				addToBoundSql(varPropFilter, generator, openVarToken, closeVarToken, text, map, prefix);
			} else {
				addToBoundSql(varPropFilter, generator, openVarToken, closeVarToken, text, map, delimiter);
			}
			addToBoundSql(varPropFilter, generator, openVarToken, closeVarToken, text, map, node);
		}
		if (!first) {
			addToBoundSql(varPropFilter, generator, openVarToken, closeVarToken, text, map, suffix);
		}
		return new BoundSql(text.toString(), map);
	}

	private static void addToBoundSql(Predicate<String> varPropFilter, VarNameGenerator generator, String openVarToken, String closeVarToken, StringBuilder sb, Map<String, Object> map, SqlNode node) {
		if (node == null) {
			return;
		}
		BoundSql sql = node.asBoundSql(varPropFilter, generator, openVarToken, closeVarToken);
		String text = sql.getText();
		if (text != null) {
			sb.append(text);
		}
		Map<String, Object> bindings = sql.getBindings();
		if (bindings != null) {
			map.putAll(bindings);
		}
	}

	@Override
	public ContainerNode copy() {
		return copy(true);
	}

	@Override
	public ContainerNode copy(boolean withVarValue) {
		ContainerNode clone = new ContainerNode();
		clone.skip = this.skip;
		for (int i = 0; i < this.subset.size(); i++) {
			clone.subset.add(this.subset.get(i).copy(withVarValue));
		}
		return clone;
	}

	@Override
	public ContainerNode clone() {
		return copy(true);
	}

	@Override
	public boolean isEmpty() {
		return subset.isEmpty();
	}

	@Override
	public List<SqlNode> subset() {
		return Collections.unmodifiableList(subset);
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
	public void visitSubsetWritable(Consumer<SqlNodeOps> visitor) {
		int size = subset.size();
		for (int i = 0; i < size; ) {
			SqlNode node = subset.get(i);
			SqlNodeOps op = new SqlNodeOps(node);

			if (node instanceof ContainerNode) {
				boolean empty = ((ContainerNode) node).subset.isEmpty();
				if (!empty) {
					((ContainerNode) node).visitSubsetWritable(visitor);
					if (((ContainerNode) node).subset.isEmpty()) {
						// 被清空子项
						subset.remove(i);
						size--;
						continue;
					}
				}
			} else {
				visitor.accept(op);
				if (op.isDeleted()) {
					subset.remove(i);
					size--;
					continue;
				} else if (op.isReplaced()) {
					subset.set(i, op.getReplaced());
				}
			}
			i++;
		}
	}

	@Override
	public void visitSubset(Consumer<SqlNode> visitor) {
		for (int i = 0, n = subset.size(); i < n; i++) {
			SqlNode node = subset.get(i);
			if (node instanceof ContainerNode) {
				((ContainerNode) node).visitSubset(visitor);
			} else {
				visitor.accept(node);
			}
		}
	}

	@Override
	public boolean replaceFirstSub(Predicate<SqlNode> predicate, Supplier<SqlNode> supplier) {
		for (int i = 0, n = subset.size(); i < n; i++) {
			SqlNode node = subset.get(i);
			if (predicate.test(node)) {
				subset.set(i, supplier.get());
				return true;
			} else {
				if (node instanceof ContainerNode) {
					boolean rs = node.replaceFirstSub(predicate, supplier);
					if (rs) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public int replaceAllSubs(Predicate<SqlNode> predicate, Supplier<SqlNode> supplier) {
		int count = 0;
		for (int i = 0, n = subset.size(); i < n; ) {
			SqlNode node = subset.get(i);
			if (predicate.test(node)) {
				subset.set(i, supplier.get());
				count++;
				n--;
			} else {
				if (node instanceof ContainerNode) {
					count += ((ContainerNode) node).replaceAllSubs(predicate, supplier);
				}
				i++;
			}
		}
		return count;
	}

	@Override
	public boolean removeFirstSub(Predicate<SqlNode> predicate) {
		for (int i = subset.size() - 1; i >= 0; i--) {
			SqlNode node = subset.get(i);
			if (predicate.test(node)) {
				subset.remove(i);
				return true;
			} else {
				if (node instanceof ContainerNode) {
					boolean rs = ((ContainerNode) node).removeFirstSub(predicate);
					if (rs) {
						return true;
					}

				}
			}
		}
		return false;
	}

	@Override
	public int removeAllSubs(Predicate<SqlNode> predicate) {
		int count = 0;
		for (int i = 0, n = subset.size(); i < n; ) {
			SqlNode node = subset.get(i);
			if (predicate.test(node)) {
				subset.remove(i);
				count++;
				n--;
			} else {
				if (node instanceof ContainerNode) {
					((ContainerNode) node).removeAllSubs(predicate);
					count++;
				}
				i++;
			}
		}
		return count;
	}

	@Override
	public void clearSkippedSubs() {
		removeAllSubs(node -> node instanceof ContainerNode && ((ContainerNode) node).skip);
	}

	@Override
	public boolean containsVarName(String key) {
		for (int i = 0, n = subset.size(); i < n; i++) {
			SqlNode node = subset.get(i);
			if (node.isVarNode()) {
				if (Objects.equals(node.getVarName(), key)) {
					return true;
				}
			} else if (node.isContainerNode()) {
				if (node.containsVarName(key)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void bindSubsetVarValues(Map<String, Object> params, boolean ignoreNull) {
		visitSubset(node -> {
			if (node.isVarNode()) {
				Object param = params.get(node.getVarName());
				if (param != null || !ignoreNull) {
					node.bindVarValue(param);
				}
			}
		});
	}

	@Override
	public void bindSubsetVarValue(String varName, Object varValue, boolean ignoreNull) {
		if (ignoreNull && varValue == null) {
			return;
		}
		visitSubset(node -> {
			if (node.isVarNode()) {
				if (Objects.equals(node.getVarName(), varName)) {
					node.bindVarValue(varValue);
				}
			}
		});
	}

	@Override
	public void removeVarValue(String varName) {
		visitSubset(node -> {
			if (node.isVarNode()) {
				if (Objects.equals(node.getVarName(), varName)) {
					node.removeVarValue();
				}
			}
		});
	}

	@Override
	public void skipIfMissingVarValue() {
		for (int i = subset.size() - 1; i >= 0; i--) {
			SqlNode node = subset.get(i);
			if (node.isVarNode()) {
				if (node.getVarValue() == null) {
					this.skip = true;
					break;
				}
			} else if (node instanceof ContainerNode) {
				((ContainerNode) node).skipIfMissingVarValue();
			}
		}
	}
}
