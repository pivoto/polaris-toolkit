package io.polaris.mybatis.util;

import io.polaris.core.string.Strings;
import lombok.Getter;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Qt
 * @since  Aug 28, 2023
 */
public class SqlParsers {

	public static String visitSelect(String sql, Function<Collection<Table>, String> conditionAppender) throws JSQLParserException {
		return visitSelect(sql, conditionAppender, null);
	}

	public static String visitSelect(String sql, BiFunction<Table,String,Boolean> columnFilter) throws JSQLParserException {
		return visitSelect(sql, null, columnFilter);
	}

	public static String visitSelect(String sql, Function<Collection<Table>, String> conditionSupplier, BiFunction<Table,String,Boolean> columnFilter) throws JSQLParserException {
		Statement stmt = CCJSqlParserUtil.parse(sql);
		Select select = (Select) stmt;
		SelectBody selectBody = select.getSelectBody();
		visit(selectBody, conditionSupplier, columnFilter);
		return select.toString();
	}

	private static void visit(SelectBody selectBody, Function<Collection<Table>, String> conditionSupplier, BiFunction<Table,String,Boolean> columnFilter) throws JSQLParserException {
		if (selectBody instanceof PlainSelect) {
			PlainSelect plainSelect = (PlainSelect) selectBody;
			visit(plainSelect, conditionSupplier, columnFilter);
		} else if (selectBody instanceof WithItem) {
			WithItem withItem = (WithItem) selectBody;
			if (withItem.getSubSelect() != null) {
				visit(withItem.getSubSelect().getSelectBody(), conditionSupplier, columnFilter);
			}
		} else if (selectBody instanceof SetOperationList) {
			SetOperationList operationList = (SetOperationList) selectBody;
			if (operationList.getSelects() != null && operationList.getSelects().size() > 0) {
				List<SelectBody> plainSelects = operationList.getSelects();
				for (SelectBody plainSelect : plainSelects) {
					visit(plainSelect, conditionSupplier, columnFilter);
				}
			}
		}
	}

	private static void visit(PlainSelect plainSelect, Function<Collection<Table>, String> conditionSupplier,
							  BiFunction<Table,String,Boolean> columnFilter) throws JSQLParserException {
		Map<String, Table> tables = new LinkedHashMap<>();
		{
			FromItem fromItem = plainSelect.getFromItem();
			fetchTables(tables, fromItem);
		}
		List<Join> joins = plainSelect.getJoins();
		if (joins != null) {
			for (Join join : joins) {
				FromItem item = join.getRightItem();
				fetchTables(tables, item);
			}
		}
		if (conditionSupplier != null) {
			String conditionSql = Strings.trimToNull(conditionSupplier.apply(tables.values()));
			if (conditionSql != null) {
				Expression condition = CCJSqlParserUtil.parseCondExpression(conditionSql);
				Expression where = plainSelect.getWhere();
				if (where == null) {
					plainSelect.setWhere(condition);
				} else {
					if (!(where instanceof Parenthesis)) {
						where = new Parenthesis(where);
					}
					if (!(condition instanceof Parenthesis)) {
						condition = new Parenthesis(condition);
					}
					AndExpression andExpression = new AndExpression(where, condition);
					plainSelect.setWhere(andExpression);
				}
			}
		}
		if (columnFilter != null) {
			List<SelectItem> selectItems = plainSelect.getSelectItems();
			Iterator<SelectItem> iter = selectItems.iterator();
			while (iter.hasNext()) {
				SelectItem selectItem = iter.next();
				if (selectItem instanceof SelectExpressionItem) {
					((SelectExpressionItem) selectItem).getAlias();
					Expression expression = ((SelectExpressionItem) selectItem).getExpression();
					if (expression instanceof Column) {
						Table colTable = ((Column) expression).getTable();
						if (colTable != null) {
							String tableAlias = colTable.getName();
							Table table = tables.get(tableAlias);
							if (table != null) {
								Boolean filtered = columnFilter.apply(table, ((Column) expression).getColumnName());
								if (Boolean.FALSE.equals(filtered)) {
									iter.remove();
								}
							}
						}
					} else {
						// 其他表达式忽略
					}
				} else if (selectItem instanceof AllTableColumns) {
					// 其他表达式忽略
				} else if (selectItem instanceof AllColumns) {
					// 其他表达式忽略
				}
			}
		}
	}

	private static void fetchTables(Map<String, Table> tables, FromItem fromItem) {
		if (fromItem instanceof Table) {
			Alias alias = ((Table) fromItem).getAlias();
			if (alias != null) {
				tables.put(alias.getName(), (Table) fromItem);
			} else {
				tables.put(((Table) fromItem).getName(), (Table) fromItem);
			}
		}
	}

	public static class SelectColumn {
		@Getter
		private final Table table;
		@Getter
		private final String columnName;
		private boolean skip = false;

		SelectColumn(Table table, String columnName) {
			this.table = table;
			this.columnName = Strings.trimToNull(columnName);
			Assert.notNull(this.columnName, "column name is required" );
		}

		public void skip() {
			this.skip = true;
		}

		public boolean isSkip() {
			return skip;
		}
	}

}
