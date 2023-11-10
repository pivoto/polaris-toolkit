package io.polaris.core.jdbc.sql.statement.any;

import io.polaris.core.jdbc.sql.statement.DeleteStatement;

/**
 * @author Qt
 * @since 1.8,  Aug 23, 2023
 */
public class AnyDeleteStatement extends DeleteStatement<AnyDeleteStatement> {
	public AnyDeleteStatement(Class<?> entityClass) {
		super(entityClass);
	}

	public AnyDeleteStatement(Class<?> entityClass, String alias) {
		super(entityClass, alias);
	}

	public static AnyDeleteStatement of(Class<?> entityClass) {
		return new AnyDeleteStatement(entityClass);
	}

	public static AnyDeleteStatement of(Class<?> entityClass, String alias) {
		return new AnyDeleteStatement(entityClass, alias);
	}
}
