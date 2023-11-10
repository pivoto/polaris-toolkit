package io.polaris.core.jdbc.sql.statement.any;

import io.polaris.core.jdbc.sql.statement.SelectStatement;

/**
 * @author Qt
 * @since 1.8,  Aug 22, 2023
 */
public class AnySelectStatement extends SelectStatement<AnySelectStatement> {
	public AnySelectStatement(Class<?> entityClass) {
		super(entityClass);
	}

	public AnySelectStatement(Class<?> entityClass, String alias) {
		super(entityClass, alias);
	}

	public AnySelectStatement(SelectStatement<?> select, String alias) {
		super(select, alias);
	}

	public static AnySelectStatement of(Class<?> entityClass) {
		return new AnySelectStatement(entityClass);
	}

	public static AnySelectStatement of(Class<?> entityClass, String alias) {
		return new AnySelectStatement(entityClass, alias);
	}

	public static AnySelectStatement of(SelectStatement<?> select, String alias) {
		return new AnySelectStatement(select, alias);
	}
}
