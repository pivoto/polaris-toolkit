package io.polaris.core.jdbc.sql.statement.any;

import io.polaris.core.jdbc.sql.statement.InsertStatement;

/**
 * @author Qt
 * @since 1.8,  Aug 23, 2023
 */
public class AnyInsertStatement extends InsertStatement<AnyInsertStatement> {
	public AnyInsertStatement(Class<?> entityClass) {
		super(entityClass);
	}
	public static AnyInsertStatement of(Class<?> entityClass) {
		return new AnyInsertStatement(entityClass);
	}
}
