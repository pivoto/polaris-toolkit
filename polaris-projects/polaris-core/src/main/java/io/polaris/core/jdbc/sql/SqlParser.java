package io.polaris.core.jdbc.sql;

import io.polaris.core.consts.StdConsts;
import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.sql.node.*;
import io.polaris.core.jdbc.sql.statement.segment.TableAccessible;
import io.polaris.core.jdbc.sql.statement.segment.TableSegment;
import io.polaris.core.lang.bean.BeanMap;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.string.StringCases;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author Qt
 * @since 1.8,  Aug 11, 2023
 */
public class SqlParser {

	public static ContainerNode parse(String sql) {
		return parse(sql, '$', '#', '{', '}');
	}

	public static ContainerNode parse(String sql, char directSymbol, char preparedSymbol
		, char openSymbol, char closeSymbol) {
		ContainerNode root = new ContainerNode();

		char[] src = sql.toCharArray();
		int len = sql.length();

		StringBuilder text = new StringBuilder(len);
		boolean inQuotes = false;
		for (int i = 0; i < len; i++) {
			char c = src[i];
			if (c == '\'') {
				if (inQuotes) {
					text.append(c);
					if (i + 1 < len && src[i + 1] == '\'') {
						// 引号转义
						text.append('\'');
						i++;
					} else {
						inQuotes = false;
					}
				} else {
					inQuotes = true;
					text.append(c);
				}
			} else if (c == directSymbol) {
				if (i + 1 < len && src[i + 1] == openSymbol) {
					int idx = sql.indexOf(closeSymbol, i + 2);
					if (idx == -1) {
						text.append(c);
					} else {
						if (text.length() > 0) {
							root.addNode(new TextNode(text.toString()));
							text.setLength(0);
						}
						text.append(src, i, idx - i + 1);
						root.addNode(new MixedNode(text.substring(2, text.length() - 1).trim()));
						text.setLength(0);
						i = idx;
					}
				} else {
					text.append(c);
				}
			} else if (c == preparedSymbol) {
				if (i + 1 < len && src[i + 1] == openSymbol) {
					int idx = sql.indexOf(closeSymbol, i + 2);
					if (idx == -1) {
						text.append(c);
					} else {
						if (text.length() > 0) {
							root.addNode(new TextNode(text.toString()));
							text.setLength(0);
						}
						text.append(src, i, idx - i + 1);
						root.addNode(new DynamicNode(text.substring(2, text.length() - 1).trim()));
						text.setLength(0);
						i = idx;
					}
				} else {
					text.append(c);
				}
			} else {
				text.append(c);
			}
		}
		if (text.length() > 0) {
			root.addNode(new TextNode(text.toString()));
			text.setLength(0);
		}
		return root;
	}



	public static String resolveRefTableField(String sql, TableAccessible tableAccessible) {
		ContainerNode containerNode = SqlParser.parse(sql, '&', (char) -1, '{', '}');
		containerNode.visitSubset(node -> {
			if (node.isVarNode()) {
				String varName = node.getVarName();
				Deque<String> path = Beans.parseProperty(varName);
				int size = path.size();
				if (size > 2) {
					throw new IllegalArgumentException("表达式错误(&{tableAlias.tableField}): " + varName);
				}
				String tableAlias = path.pollFirst();
				TableSegment<?> table = tableAccessible.getTable(tableAlias);
				if (table == null) {
					throw new IllegalArgumentException("表别名不存在: " + tableAlias);
				}
				String tableField = path.pollFirst();
				if (tableField == null) {
					TableMeta tableMeta = table.getTableMeta();
					if (tableMeta == null) {
						node.bindVarValue(tableAlias);
					} else {
						node.bindVarValue(tableMeta.getTable() + " " + tableAlias);
					}
				} else if (SymbolConsts.ASTERISK.equals(tableField)) {
					node.bindVarValue(table.getAllColumnExpression(false));
				} else {
					node.bindVarValue(table.getColumnExpression(tableField));
				}
			}
		});

		return containerNode.toString();
	}

	/**
	 * 是否基本数据类型(基本类型,枚举,数组,字符串,数值,日期)
	 *
	 * @param clazz
	 * @return
	 */
	private static boolean isBasicDataType(Class clazz) {
		return clazz.isPrimitive()
			|| clazz.isEnum()
			|| clazz.isArray()
			|| String.class == clazz
			|| Integer.class == clazz
			|| BigDecimal.class == clazz
			|| Double.class == clazz
			|| BigInteger.class == clazz
			|| Float.class == clazz
			|| Short.class == clazz
			|| Byte.class == clazz
			|| Character.class == clazz
			|| Boolean.class == clazz
			|| Date.class.isAssignableFrom(clazz)
			;
	}

	public static Map<String, Object> asMap(Object o) {
		Map<String, Object> params = new HashMap<>();
		// 兼容小写驼峰转小写下划线
		Map<String, Object> compatible = new HashMap<>();
		if (o == null) {
			params.put(StdConsts.VALUE, null);
		} else if (isBasicDataType(o.getClass())) {
			params.put(StdConsts.VALUE, o);
		} else if (o instanceof Map) {
			// 遍历KeySet, 不使用entrySet, 以适应某些特殊形式的Map支持
			Set keys = ((Map) o).keySet();
			for (Object key : keys) {
				String s = Objects.toString(key, null);
				if (s != null) {
					Object val = ((Map) o).get(key);
					params.put(s, val);
					String underlineKey = StringCases.camelToUnderlineCase(s);
					if (!s.equals(underlineKey)) {
						compatible.put(underlineKey, val);
					}
				}
			}
		} else if (o.getClass() != Object.class) {
			try {
				BeanMap<Object> map = Beans.newBeanMap(o);
				for (String key : map.keySet()) {
					Object val = map.get(key);
					params.put(key, val);
					String underlineKey = StringCases.camelToUnderlineCase(key);
					if (!key.equals(underlineKey)) {
						compatible.put(underlineKey, val);
					}
				}
			} catch (Exception e) {
			}
		} else {
			params.put(StdConsts.VALUE, o);
		}
		if (!compatible.isEmpty()) {
			for (Map.Entry<String, Object> entry : compatible.entrySet()) {
				params.putIfAbsent(entry.getKey(), entry.getValue());
			}
		}
		return params;
	}
}
