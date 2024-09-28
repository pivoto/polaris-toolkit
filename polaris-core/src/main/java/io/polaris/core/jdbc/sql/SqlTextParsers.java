package io.polaris.core.jdbc.sql;

import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.DynamicNode;
import io.polaris.core.jdbc.sql.node.MixedNode;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.statement.segment.TableAccessible;
import io.polaris.core.jdbc.sql.statement.segment.TableSegment;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since  Aug 11, 2023
 */
public class SqlTextParsers {

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

	/**
	 * 解析实体表与字段的引用表达式，支持格式：
	 * <ul>
	 *   <li>`&{tableAlias}`
	 *   </li>
	 *   <li>`&{tableAlias.tableField}`
	 *   </li>
	 *   <li>`&{tableAlias?.tableField}`
	 *   </li>
	 *   <li>`&{#tableIndex}`
	 *   </li>
	 *   <li>`&{#tableIndex.tableField}`
	 *   </li>
	 *   <li>`&{#tableIndex?.tableField}`
	 *   </li>
	 * </ul>
	 */
	public static String resolveTableRef(String sql, TableAccessible tableAccessible) {
		if (Strings.isBlank(sql)) {
			return sql;
		}
		if (tableAccessible == null) {
			return sql;
		}
		ContainerNode containerNode = SqlTextParsers.parse(sql, '&', (char) -1, '{', '}');
		containerNode.visitSubset(node -> {
			if (node.isVarNode()) {
				String varName = node.getVarName();
				String[] paths = Strings.tokenizeToArray(varName, ".");
				if (paths.length == 0 || paths.length > 2) {
					throw new IllegalArgumentException("实体表字段的引用表达式错误: " + varName);
				}
				String tableAlias = paths[0].trim();
				if (Strings.isBlank(tableAlias)) {
					throw new IllegalArgumentException("实体表字段的引用表达式错误: " + varName);
				}
				boolean excludeAlias = tableAlias.charAt(tableAlias.length() - 1) == '?';
				if (excludeAlias) {
					tableAlias = tableAlias.substring(0, tableAlias.length() - 1);
				}

				TableSegment<?> table;
				// 取序号
				if (tableAlias.startsWith(SymbolConsts.HASH_MARK)) {
					table = tableAccessible.getTable(Integer.parseInt(tableAlias.substring(1)));
				} else {
					table = tableAccessible.getTable(tableAlias);
				}
				if (table == null) {
					throw new IllegalArgumentException("表别名不存在: " + tableAlias);
				}

				String tableField = paths.length == 1 ? null : paths[1].trim();
				if (Strings.isBlank(tableField)) {
					TableMeta tableMeta = table.getTableMeta();
					// 无直接实体表，可能是子查询等
					if (tableMeta == null) {
						node.bindVarValue(table.getTableAlias());
					} else {
						if (excludeAlias) {
							node.bindVarValue(tableMeta.getTable());
						} else {
							node.bindVarValue(tableMeta.getTable() + " " + table.getTableAlias());
						}
					}
				} else if (SymbolConsts.ASTERISK.equals(tableField)) {
					node.bindVarValue(table.getAllColumnExpression(!excludeAlias, false));
				} else {
					node.bindVarValue(table.getColumnExpression(tableField, !excludeAlias));
				}
			}
		});
		return containerNode.toString();
	}


	/**
	 * 解析实体表与字段的引用表达式，支持格式：
	 * <ul>
	 *   <li>`&{tableAlias(entityClassName)}`
	 *   </li>
	 *   <li>`&{tableAlias(entityClassName).tableField}`
	 *   </li>
	 *   <li>`&{tableAlias(entityClassName)?.tableField}`
	 *   </li>
	 * </ul>
	 */
	public static String resolveTableRef(String sql) {
		if (Strings.isBlank(sql)) {
			return sql;
		}
		ContainerNode containerNode = SqlTextParsers.parse(sql, '&', (char) -1, '{', '}');
		containerNode.visitSubset(node -> {
			if (node.isVarNode()) {
				String varName = node.getVarName();
				int iLeftParenthesis = varName.indexOf('(');
				if (iLeftParenthesis < 1) {
					throw new IllegalArgumentException("实体表字段的引用表达式错误: " + varName);
				}
				int iRightParenthesis = varName.indexOf(')', iLeftParenthesis);
				if (iRightParenthesis <= iLeftParenthesis + 1) {
					throw new IllegalArgumentException("实体表字段的引用表达式错误: " + varName);
				}
				String tableAlias = varName.substring(0, iLeftParenthesis).trim();
				String entityClassName = varName.substring(iLeftParenthesis + 1, iRightParenthesis).trim();
				TableSegment<?> table;
				try {
					Class<?> type = Class.forName(entityClassName);
					table = TableSegment.fromEntity(type, tableAlias);
				} catch (ClassNotFoundException e) {
					throw new IllegalArgumentException("实体类不存在: " + entityClassName);
				}
				TableMeta tableMeta = table.getTableMeta();
				if (tableMeta == null) {
					throw new IllegalArgumentException("实体类不存在: " + entityClassName);
				}
				boolean excludeAlias = false;
				String tableField = varName.substring(iRightParenthesis + 1).trim();
				if (!tableField.isEmpty()) {
					int idx = 0;
					while (idx < tableField.length()) {
						if (Character.isWhitespace(tableField.charAt(idx))) {
							idx++;
							continue;
						}
						if (tableField.charAt(idx) == '?') {
							excludeAlias = true;
							idx++;
							break;
						} else {
							break;
						}
					}
					while (idx < tableField.length()) {
						if (Character.isWhitespace(tableField.charAt(idx))) {
							idx++;
							continue;
						}
						if (tableField.charAt(idx) == '.') {
							idx++;
							break;
						} else {
							break;
						}
					}
					tableField = tableField.substring(idx);
				}

				if (Strings.isBlank(tableField)) {
					if (excludeAlias) {
						node.bindVarValue(tableMeta.getTable());
					} else {
						node.bindVarValue(tableMeta.getTable() + " " + table.getTableAlias());
					}
				} else if (SymbolConsts.ASTERISK.equals(tableField)) {
					if (excludeAlias) {
						node.bindVarValue(Strings.join(", ", table.getAllColumnNames()));
					} else {
						node.bindVarValue(table.getAllColumnExpression(false));
					}
				} else {
					node.bindVarValue(table.getColumnExpression(tableField,!excludeAlias));
				}
			}
		});
		return containerNode.toString();
	}

}
