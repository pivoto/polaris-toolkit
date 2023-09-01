package io.polaris.core.jdbc.sql.statement.any;

import io.polaris.core.jdbc.sql.statement.UpdateStatement;

/**
 * @author Qt
 * @since 1.8,  Aug 23, 2023
 */
public class AnyUpdateStatement extends UpdateStatement<AnyUpdateStatement> {
	public AnyUpdateStatement(Class<?> entityClass) {
		super(entityClass);
	}

	public AnyUpdateStatement(Class<?> entityClass, String alias) {
		super(entityClass, alias);
	}


	public static AnyUpdateStatement of(Class<?> entityClass) {
		return new AnyUpdateStatement(entityClass);
	}

	public static AnyUpdateStatement of(Class<?> entityClass, String alias) {
		return new AnyUpdateStatement(entityClass, alias);
	}
}
