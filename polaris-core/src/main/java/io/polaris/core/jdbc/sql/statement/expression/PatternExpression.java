package io.polaris.core.jdbc.sql.statement.expression;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.polaris.core.function.FunctionWithArgs3;
import io.polaris.core.jdbc.sql.BindingValues;
import io.polaris.core.jdbc.sql.SqlTextParsers;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.regex.Patterns;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Aug 22, 2023
 */
public class PatternExpression extends BaseExpression {
	public static final String REF_PREFIX = "ref";
	public static final Pattern REF_PATTERN = Patterns.getPattern("^ref(\\d*)$");
	private static final Map<String, PatternExpression> cache = new ConcurrentHashMap<>();

	/** 模板 */
	private final ContainerNode templateSqlNode;
	/** 需要的绑定变量个数 */
	private final int argSize;
	private final Map<String, Integer> argsIndices = new HashMap<>();
	/** 需要的扩展SQL块的个数 */
	private final int refExtSize;
	private final boolean refBaseExisted;
	private final Map<String, Integer> refIndices = new HashMap<>();

	public PatternExpression(String pattern) {
		ContainerNode sqlNode = SqlTextParsers.parse(pattern);
		int[] argSize = new int[]{0};
		int[] refExtIdx = new int[]{-1};
		boolean[] hasRef0 = new boolean[]{false};
		sqlNode.visitSubsetWritable(op -> {
			SqlNode n = op.getSqlNode();
			if (n.isVarNode()) {
				String varName = n.getVarName();
				int idx = parseRefNode(varName);
				if (idx >= 0) {
					// 原SqlNode占位符
					refExtIdx[0] = Integer.max(refExtIdx[0], idx);
					if (idx == 0) {
						hasRef0[0] = true;
					}
				} else {
					// 存在同名变量时复用其位置
					if (!argsIndices.containsKey(varName)) {
						argSize[0]++;
						argsIndices.put(varName, argSize[0] - 1);
					}
				}
			}
		});
		this.refBaseExisted = hasRef0[0];
		this.argSize = argSize[0];
		this.refExtSize = refExtIdx[0];
		this.templateSqlNode = sqlNode;
	}


	public static PatternExpression of(String pattern) {
		return cache.computeIfAbsent(pattern, k -> new PatternExpression(pattern));
	}


	private int parseRefNode(String varName) {
		if (varName.startsWith(REF_PREFIX)) {
			Matcher matcher = REF_PATTERN.matcher(varName);
			if (matcher.matches()) {
				String group = matcher.group(1);
				int idx = 0;
				if (Strings.isNotBlank(group)) {
					idx = Integer.parseInt(group);
				}
				refIndices.put(varName, idx);
				return idx;
			}
		}
		return -1;
	}


	private ContainerNode bind(SqlNode baseSource, SqlNode[] extSources, Function<String, Object> getter) {
		if (refBaseExisted && baseSource == null) {
			// 缺Sql块
			return SqlNodes.EMPTY;
		}
		if (refExtSize > 0 && (extSources == null || extSources.length < refExtSize)) {
			// 缺Sql块
			return SqlNodes.EMPTY;
		}
		ContainerNode rs = createPatternSqlNode();
		if (refBaseExisted || refExtSize > 0 || argSize > 0) {
			rs.visitSubsetWritable(op -> {
				SqlNode n = op.getSqlNode();
				if (n.isVarNode()) {
					String varName = n.getVarName();
					Integer index = refIndices.get(varName);
					if (index != null) {
						int i = index.intValue();
						SqlNode source = i == 0 ? baseSource : extSources[i - 1];
						op.replace(source);
					} else {
						n.bindVarValue(wrapBinding(getter.apply(varName)));
					}
				}
			});
		}
		return rs;
	}

	@Override
	protected FunctionWithArgs3<SqlNode, SqlNode[], Object[], ContainerNode> buildArrayFunction() {
		return (baseSource, extSources, bindings) -> bind(baseSource, extSources, varName -> {
			if (bindings == null) {
				return null;
			}
			Integer i = argsIndices.get(varName);
			return bindings.length <= i ? null : bindings[i];
		});
	}

	@Override
	protected FunctionWithArgs3<SqlNode, SqlNode[], Map<String, Object>, ContainerNode> buildMapFunction() {
		return (baseSource, extSources, bindings) -> bind(baseSource, extSources, varName -> bindings == null ? null : BindingValues.getBindingValueOrDefault(bindings, varName, null));
	}

	protected ContainerNode createPatternSqlNode() {
		return templateSqlNode.copy();
	}

	protected Object wrapBinding(Object v) {
		return v;
	}

	@Override
	public String toString() {
		return templateSqlNode.toString();
	}
}
