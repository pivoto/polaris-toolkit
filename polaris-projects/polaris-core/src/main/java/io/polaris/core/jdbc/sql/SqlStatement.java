package io.polaris.core.jdbc.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Aug 12, 2023
 */
public class SqlStatement {
	private final List<String> tables = new ArrayList<>();
	private final List<String> columns = new ArrayList<>();
	private final List<String> values = new ArrayList<>();
	private final List<String> sets = new ArrayList<>();
	private final List<String> select = new ArrayList<>();
	private final List<Join> joins = new ArrayList<>();
	private final List<String> groupBy = new ArrayList<>();
	private final List<String> orderBy = new ArrayList<>();
	private final And<SqlStatement> where = new And<>(this);
	private final And<SqlStatement> having = new And<>(this);
	private StatementType statementType;
	private boolean distinct;

	public static SqlStatement of() {
		return new SqlStatement();
	}

	public SqlStatement update(String table) {
		this.statementType = SqlStatement.StatementType.UPDATE;
		this.tables.add(table);
		return this;
	}

	public SqlStatement set(String sets) {
		this.sets.add(sets);
		return this;
	}

	public SqlStatement set(String... sets) {
		this.sets.addAll(Arrays.asList(sets));
		return this;
	}

	public SqlStatement insert(String tableName) {
		this.statementType = SqlStatement.StatementType.INSERT;
		this.tables.add(tableName);
		return this;
	}

	public SqlStatement columnAndValue(String columns, String values) {
		this.columns.add(columns);
		this.values.add(values);
		return this;
	}

	public SqlStatement columns(String... columns) {
		this.columns.addAll(Arrays.asList(columns));
		return this;
	}

	public SqlStatement values(String... values) {
		this.values.addAll(Arrays.asList(values));
		return this;
	}

	public SqlStatement select(String columns) {
		this.statementType = SqlStatement.StatementType.SELECT;
		this.select.add(columns);
		return this;
	}

	public SqlStatement select(String... columns) {
		this.statementType = SqlStatement.StatementType.SELECT;
		this.select.addAll(Arrays.asList(columns));
		return this;
	}

	public SqlStatement selectDistinct(String columns) {
		this.distinct = true;
		select(columns);
		return this;
	}

	public SqlStatement selectDistinct(String... columns) {
		this.distinct = true;
		select(columns);
		return this;
	}

	public SqlStatement delete(String table) {
		this.statementType = SqlStatement.StatementType.DELETE;
		this.tables.add(table);
		return this;
	}

	public SqlStatement from(String table) {
		this.tables.add(table);
		return this;
	}

	public SqlStatement from(String... tables) {
		this.tables.addAll(Arrays.asList(tables));
		return this;
	}

	public Join join(String table) {
		Join join = new Join(this, "JOIN", table);
		this.joins.add(join);
		return join;
	}


	public Join innerJoin(String table) {
		Join join = new Join(this, "INNER JOIN", table);
		this.joins.add(join);
		return join;
	}

	public Join leftOuterJoin(String table) {
		Join join = new Join(this, "LEFT OUTER JOIN", table);
		this.joins.add(join);
		return join;
	}

	public Join rightOuterJoin(String table) {
		Join join = new Join(this, "RIGHT OUTER JOIN", table);
		this.joins.add(join);
		return join;
	}

	public Join outerJoin(String table) {
		Join join = new Join(this, "OUTER JOIN", table);
		this.joins.add(join);
		return join;
	}

	public And<SqlStatement> where() {
		return where;
	}

	public SqlStatement where(String conditions) {
		this.where.add(conditions);
		return this;
	}

	public SqlStatement where(String... conditions) {
		this.where.add(conditions);
		return this;
	}

	public SqlStatement groupBy(String columns) {
		this.groupBy.add(columns);
		return this;
	}

	public SqlStatement groupBy(String... columns) {
		this.groupBy.addAll(Arrays.asList(columns));
		return this;
	}

	public And<SqlStatement> having() {
		return having;
	}

	public SqlStatement having(String conditions) {
		this.having.add(conditions);
		return this;
	}

	public SqlStatement having(String... conditions) {
		this.having.add(conditions);
		return this;
	}

	public SqlStatement orderBy(String columns) {
		this.orderBy.add(columns);
		return this;
	}

	public SqlStatement orderBy(String... columns) {
		this.orderBy.addAll(Arrays.asList(columns));
		return this;
	}

	@Override
	public String toString() {
		return toSqlString();
	}

	public String toSqlString() throws IllegalStateException {
		StringBuilder builder = new StringBuilder();
		if (statementType == null) {
			return null;
		}
		switch (statementType) {
			case DELETE:
				return deleteSQL(builder);
			case INSERT:
				return insertSQL(builder);
			case SELECT:
				return selectSQL(builder);
			case UPDATE:
				return updateSQL(builder);
			default:
				throw new IllegalArgumentException("未知Sql类型：" + statementType);
		}
	}

	private String selectSQL(StringBuilder builder) {
		sqlSelect(builder);
		sqlFrom(builder);
		sqlJoins(builder);
		sqlWhere(builder);
		sqlGroupBy(builder);
		sqlHaving(builder);
		sqlOrderBy(builder);
		return builder.toString();
	}

	private String insertSQL(StringBuilder builder) {
		sqlInsert(builder);
		sqlColumns(builder);
		sqlValues(builder);
		return builder.toString();
	}

	private String deleteSQL(StringBuilder builder) {
		sqlDelete(builder);
		sqlWhere(builder);
		return builder.toString();
	}

	private String updateSQL(StringBuilder builder) {
		sqlUpdate(builder);
		sqlJoins(builder);
		sqlSet(builder);
		sqlWhere(builder);
		return builder.toString();
	}


	private void sqlInsert(StringBuilder builder) {
		if (!tables.isEmpty()) {
			if (builder.length() > 0) {
				builder.append("\n");
			}
			builder.append("INSERT INTO ");
			for (int i = 0; i < tables.size(); i++) {
				String c = tables.get(i);
				builder.append(c);
			}
		}
	}

	private void sqlColumns(StringBuilder builder) {
		if (!columns.isEmpty()) {
			if (builder.length() > 0) {
				builder.append("\n");
			}
			builder.append("(");
			for (int i = 0; i < columns.size(); i++) {
				String c = columns.get(i);
				if (i > 0) {
					builder.append(", ");
				}
				builder.append(c);
			}
			builder.append(")");
		}
	}

	private void sqlValues(StringBuilder builder) {
		if (!values.isEmpty()) {
			if (builder.length() > 0) {
				builder.append("\n");
			}
			builder.append("VALUES (");
			for (int i = 0; i < values.size(); i++) {
				String c = values.get(i);
				if (i > 0) {
					builder.append(", ");
				}
				builder.append(c);
			}
			builder.append(")");
		}
	}

	private void sqlDelete(StringBuilder builder) {
		if (!tables.isEmpty()) {
			if (builder.length() > 0) {
				builder.append("\n");
			}
			builder.append("DELETE FROM ");
			for (int i = 0; i < tables.size(); i++) {
				String c = tables.get(i);
				builder.append(c);
			}
		}
	}

	private void sqlUpdate(StringBuilder builder) {
		if (!tables.isEmpty()) {
			if (builder.length() > 0) {
				builder.append("\n");
			}
			builder.append("UPDATE ");
			for (int i = 0; i < tables.size(); i++) {
				String c = tables.get(i);
				builder.append(c);
			}
		}
	}

	private void sqlSet(StringBuilder builder) {
		if (!sets.isEmpty()) {
			if (builder.length() > 0) {
				builder.append("\n");
			}
			builder.append("SET");
			for (int i = 0; i < sets.size(); i++) {
				String c = sets.get(i);
				if (i > 0) {
					builder.append(",");
				}
				builder.append(" ").append(c);
			}
		}
	}

	private void sqlSelect(StringBuilder builder) {
		if (!select.isEmpty()) {
			if (builder.length() > 0) {
				builder.append("\n");
			}
			builder.append("SELECT");
			if (distinct) {
				builder.append(" DISTINCT");
			}
			for (int i = 0; i < select.size(); i++) {
				String c = select.get(i);
				if (i > 0) {
					builder.append(",");
				}
				builder.append(" ").append(c);
			}
		}
	}

	private void sqlFrom(StringBuilder builder) {
		if (!tables.isEmpty()) {
			if (builder.length() > 0) {
				builder.append("\n");
			}
			builder.append("FROM");
			for (int i = 0; i < tables.size(); i++) {
				String c = tables.get(i);
				if (i > 0) {
					builder.append(",");
				}
				builder.append(" ").append(c);
			}
		}
	}

	private void sqlJoins(StringBuilder builder) {
		if (!joins.isEmpty()) {
			for (Join join : joins) {
				if (builder.length() > 0) {
					builder.append("\n");
				}
				builder.append(join.toSqlString());
			}
		}
	}


	private void sqlWhere(StringBuilder builder) {
		String whereString = where.toSqlString();
		if (whereString.length() > 0) {
			if (builder.length() > 0) {
				builder.append("\n");
			}
			builder.append("WHERE ").append(whereString);
		}
	}

	private void sqlGroupBy(StringBuilder builder) {
		if (!groupBy.isEmpty()) {
			if (builder.length() > 0) {
				builder.append("\n");
			}
			builder.append("GROUP BY");
			for (int i = 0; i < groupBy.size(); i++) {
				String c = groupBy.get(i);
				if (i > 0) {
					builder.append(",");
				}
				builder.append(" ").append(c);
			}
		}
	}

	private void sqlHaving(StringBuilder builder) {
		String whereString = having.toSqlString();
		if (whereString.length() > 0) {
			if (builder.length() > 0) {
				builder.append("\n");
			}
			builder.append("HAVING ").append(whereString);
		}
	}

	private void sqlOrderBy(StringBuilder builder) {
		if (!orderBy.isEmpty()) {
			if (builder.length() > 0) {
				builder.append("\n");
			}
			builder.append("ORDER BY");
			for (int i = 0; i < orderBy.size(); i++) {
				String c = orderBy.get(i);
				if (i > 0) {
					builder.append(",");
				}
				builder.append(" ").append(c);
			}
		}
	}

	@SuppressWarnings("all")
	public enum StatementType {
		INSERT, SELECT, UPDATE, DELETE,
	}

	public static class Join {
		private final SqlStatement owner;
		private final String conjunction;
		private final String table;
		private final And<SqlStatement> on;

		public Join(SqlStatement owner, String conjunction, String table) {
			this.owner = owner;
			this.conjunction = Strings.isBlank(conjunction) ? "JOIN" : conjunction;
			this.table = table;
			this.on = new And<>(owner);
		}

		public And<SqlStatement> on() {
			return on;
		}

		public SqlStatement end() {
			return owner;
		}

		public String toSqlString() {
			if (Strings.isBlank(table)) {
				return SymbolConsts.EMPTY;
			}
			StringBuilder sb = new StringBuilder();
			sb.append(conjunction).append(" ").append(table);
			String onSqlString = on.toSqlString();
			if (onSqlString.length() > 0) {
				sb.append(" ON ").append(onSqlString);
			}
			return sb.toString();
		}
	}

	public static class And<T> {
		private final T owner;
		private final List<Object> conditions = new ArrayList<>();

		private And(T owner) {
			this.owner = owner;
		}

		public T end() {
			return owner;
		}

		public And<T> add(String condition) {
			this.conditions.add(condition);
			return this;
		}

		public And<T> add(String... conditions) {
			this.conditions.addAll(Arrays.asList(conditions));
			return this;
		}

		public Or<And<T>> or() {
			Or<And<T>> or = new Or<>(this);
			this.conditions.add(or);
			return or;
		}

		public String toSqlString() {
			if (this.conditions.isEmpty()) {
				return SymbolConsts.EMPTY;
			}
			StringBuilder sb = new StringBuilder();
			for (final Object condition : conditions) {
				if (sb.length() > 0) {
					sb.append(")\nAND (");
				} else {
					sb.append("(");
				}
				if (condition instanceof Or) {
					sb.append(((Or<?>) condition).toSqlString());
				} else if (condition instanceof String) {
					sb.append(condition);
				}
			}
			sb.append(")");
			return sb.toString();
		}

		public boolean hasConditions() {
			return !conditions.isEmpty();
		}
	}


	public static class Or<T> {
		private final T owner;
		private final List<Object> conditions = new ArrayList<>();

		private Or(T owner) {
			this.owner = owner;
		}

		public T end() {
			return owner;
		}

		public Or<T> add(String condition) {
			this.conditions.add(condition);
			return this;
		}

		public Or<T> add(String... conditions) {
			this.conditions.addAll(Arrays.asList(conditions));
			return this;
		}

		public And<Or<T>> and() {
			And<Or<T>> and = new And<>(this);
			this.conditions.add(and);
			return and;
		}

		public String toSqlString() {
			if (this.conditions.isEmpty()) {
				return SymbolConsts.EMPTY;
			}
			StringBuilder sb = new StringBuilder();
			for (final Object condition : conditions) {
				if (sb.length() > 0) {
					sb.append(")\nOR (");
				} else {
					sb.append("(");
				}
				if (condition instanceof And) {
					sb.append(((And<?>) condition).toSqlString());
				} else if (condition instanceof String) {
					sb.append(condition);
				}
			}
			sb.append(")");
			return sb.toString();
		}

		public boolean hasConditions() {
			return !conditions.isEmpty();
		}
	}


}
