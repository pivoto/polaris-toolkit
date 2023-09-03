package io.polaris.core.jdbc.sql;

import io.polaris.core.string.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8,  Aug 11, 2023
 */
public class SqlBuilder {
	private final StringBuilder builder = new StringBuilder();
	private List<Object> bindings;

	public static SqlBuilder of() {
		return new SqlBuilder();
	}

	public static SqlBuilder of(String s) {
		return new SqlBuilder().append(s);
	}

	public static SqlBuilder of(SqlBuilder s) {
		return new SqlBuilder().append(s);
	}

	public boolean isNotBlank() {
		return Strings.isNotBlank(builder);
	}

	public SqlBuilder visit(Consumer<SqlBuilder> visitor) {
		visitor.accept(this);
		return this;
	}

	public SqlBuilder map(Function<SqlBuilder, SqlBuilder> function) {
		return function.apply(this);
	}


	public SqlBuilder append(SqlBuilder sql) {
		return this.append(sql.toSqlString()).bind(sql.toSqlBindings());
	}

	public SqlBuilder append(Object o) {
		builder.append(o);
		return this;
	}

	public SqlBuilder append(String s) {
		builder.append(s);
		return this;
	}

	public SqlBuilder append(Object... args) {
		for (Object o : args) {
			builder.append(o);
		}
		return this;
	}

	public SqlBuilder append(String... args) {
		for (String text : args) {
			builder.append(text);
		}
		return this;
	}

	private List<Object> bindings() {
		if (this.bindings == null) {
			this.bindings = new ArrayList<>();
		}
		return bindings;
	}

	public SqlBuilder bind(Iterable<?> params) {
		if (params != null) {
			List<Object> bindings = bindings();
			for (Object param : params) {
				bindings.add(param);
			}
		}
		return this;
	}

	public SqlBuilder bind(Object... params) {
		if (params != null && params.length > 0) {
			List<Object> bindings = bindings();
			for (Object param : params) {
				bindings.add(param);
			}
		}
		return this;
	}

	public Object[] toSqlBindings() {
		if (bindings == null) {
			return new Object[0];
		}
		return bindings.toArray(new Object[0]);
	}

	public String toSqlString() {
		return builder.toString();
	}

	@Override
	public String toString() {
		return toSqlString();
	}

}
