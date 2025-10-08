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
 * SQL模式表达式类，用于处理包含变量和SQL块引用的SQL模板
 * <pre>
 * 该类支持两种类型的占位符：
 * 1. 变量占位符：#{varName} 或 ${varName}，用于绑定参数值
 * 2. SQL块引用占位符：${ref} 或 ${refN} (N为数字)，用于动态插入SQL引用块, ref0等同于ref，表示基础引入块，通常是主列名或表达式
 * <pre>
 *
 * @author Qt
 * @since Aug 22, 2023
 */
public class PatternExpression extends BaseExpression {
	/** SQL块引用前缀 */
	public static final String REF_PREFIX = "ref";
	/** SQL块引用模式，匹配ref或ref加数字的格式 */
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

	/**
	 * 构造一个新的PatternExpression实例
	 * <p>
	 * 解析给定的SQL模式字符串，提取变量占位符和SQL块引用，并构建内部结构以支持后续的绑定操作
	 *
	 * @param pattern SQL模式字符串，可以包含变量占位符(#{var}或${var})和SQL块引用(${ref}或${refN})
	 */
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
					// 存在同名变量时复用其位置，否则使用新的位置
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


	/**
	 * 获取PatternExpression实例，使用缓存机制避免重复创建相同模式的实例
	 *
	 * @param pattern SQL模式字符串
	 * @return PatternExpression实例
	 */
	public static PatternExpression of(String pattern) {
		return cache.computeIfAbsent(pattern, k -> new PatternExpression(pattern));
	}


	/**
	 * 解析SQL块引用节点
	 *
	 * @param varName 变量名称
	 * @return 引用索引，如果不符合引用格式则返回-1
	 */
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


	/**
	 * 绑定SQL块和变量值到模板中
	 *
	 * @param baseSource 基础SQL源（对应ref0）
	 * @param extSources 扩展SQL源数组（对应ref1, ref2, ...）
	 * @param getter     变量值获取函数
	 * @return 绑定后的SQL节点容器
	 */
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

	/**
	 * 构建使用数组参数的绑定函数
	 *
	 * @return 三参数函数，接受基础SQL源、扩展SQL源数组和绑定值数组，返回绑定后的SQL节点容器
	 */
	@Override
	protected FunctionWithArgs3<SqlNode, SqlNode[], Object[], ContainerNode> buildArrayFunction() {
		return (baseSource, extSources, bindings) ->
			bind(baseSource, extSources, varName -> {
				if (bindings == null) {
					return null;
				}
				Integer i = argsIndices.get(varName);
				return bindings.length <= i ? null : bindings[i];
			});
	}

	/**
	 * 构建使用Map参数的绑定函数
	 *
	 * @return 三参数函数，接受基础SQL源、扩展SQL源数组和绑定值Map，返回绑定后的SQL节点容器
	 */
	@Override
	protected FunctionWithArgs3<SqlNode, SqlNode[], Map<String, Object>, ContainerNode> buildMapFunction() {
		return (baseSource, extSources, bindings) ->
			bind(baseSource, extSources, varName ->
				bindings == null ? null : BindingValues.getBindingValueOrDefault(bindings, varName, null)
			);
	}

	/**
	 * 创建模式SQL节点的副本
	 *
	 * @return 模板SQL节点的副本
	 */
	protected ContainerNode createPatternSqlNode() {
		return templateSqlNode.copy();
	}

	/**
	 * 包装绑定值
	 *
	 * @param v 原始绑定值
	 * @return 包装后的绑定值
	 */
	protected Object wrapBinding(Object v) {
		return v;
	}

	/**
	 * 返回该模式表达式的字符串表示形式
	 *
	 * @return 模板SQL节点的字符串表示
	 */
	@Override
	public String toString() {
		return templateSqlNode.toString();
	}
}
