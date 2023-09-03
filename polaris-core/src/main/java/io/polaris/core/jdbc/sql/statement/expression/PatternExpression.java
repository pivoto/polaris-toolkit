package io.polaris.core.jdbc.sql.statement.expression;

import io.polaris.core.function.TernaryFunction;
import io.polaris.core.jdbc.sql.SqlParser;
import io.polaris.core.jdbc.sql.node.*;
import io.polaris.core.regex.Patterns;
import io.polaris.core.string.Strings;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Qt
 * @since 1.8,  Aug 22, 2023
 */
public class PatternExpression extends BaseExpression {
	public static final String REF_PREFIX = "ref";
	public static final Pattern REF_PATTERN = Patterns.getPattern("^ref(\\d*)$");

	/** 模板 */
	private final ContainerNode templateSqlNode;
	/** 需要的绑定变量个数 */
	private final int argSize;
	/** 需要的扩展SQL块的个数 */
	private final int refExtSize;
	private final boolean hasRefBase;
	private final Map<String, Integer> refNodes = new HashMap<>();

	public PatternExpression(String pattern) {
		ContainerNode sqlNode = SqlParser.parse(pattern);
		int[] argSize = new int[]{0};
		int[] refMaxIdx = new int[]{-1};
		boolean[] hasRef0 = new boolean[]{false};
		sqlNode.visitSubsetWritable(op -> {
			SqlNode n = op.getSqlNode();
			if (n.isVarNode()) {
				String varName = n.getVarName();
				int idx = parseRefNode(varName);
				if (idx >= 0) {
					// 原SqlNode占位符
					refMaxIdx[0] = Integer.max(refMaxIdx[0], idx);
					if (idx == 0) {
						hasRef0[0] = true;
					}
				} else {
					argSize[0]++;
					if (n instanceof DynamicNode) {
						op.replace(new DynamicNode(String.valueOf(argSize[0] - 1)));
					} else {
						op.replace(new MixedNode(String.valueOf(argSize[0] - 1)));
					}
				}
			}
		});
		this.hasRefBase = hasRef0[0];
		this.argSize = argSize[0];
		this.refExtSize = refMaxIdx[0];
		this.templateSqlNode = sqlNode;
	}


	public static PatternExpression of(String pattern) {
		return new PatternExpression(pattern);
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
				refNodes.put(varName, idx);
				return idx;
			}
		}
		return -1;
	}

	@Override
	protected TernaryFunction<SqlNode, SqlNode[], Object[], ContainerNode> buildFunction() {
		return (baseSource, extSources, bindings) -> {
			if (hasRefBase && baseSource == null) {
				// 缺Sql块
				return SqlNodes.EMPTY;
			}
			if (refExtSize > 0 && (extSources == null || extSources.length < refExtSize)) {
				// 缺Sql块
				return SqlNodes.EMPTY;
			}
			if (argSize > 0 && (bindings == null || bindings.length < argSize)) {
				// 缺参数
				return SqlNodes.EMPTY;
			}
			ContainerNode rs = createPatternSqlNode();
			if (hasRefBase || refExtSize > 0 || argSize > 0) {
				rs.visitSubsetWritable(op -> {
					SqlNode n = op.getSqlNode();
					if (n.isVarNode()) {
						String varName = n.getVarName();
						Integer index = refNodes.get(varName);
						if (index != null) {
							int i = index.intValue();
							SqlNode source = i == 0 ? baseSource : extSources[i - 1];
							op.replace(source);
						} else {
							int i = Integer.parseInt(varName);
							n.bindVarValue(wrapBinding(bindings[i]));
						}
					}
				});
			}
			return rs;
		};
	}

	protected ContainerNode createPatternSqlNode() {
		return templateSqlNode.copy();
	}

	protected Object wrapBinding(Object v) {
		return v;
	}


}
