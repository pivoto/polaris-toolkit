package io.polaris.core.jdbc.sql.statement.demo;

import io.polaris.core.jdbc.sql.statement.DeleteStatement;
import io.polaris.core.jdbc.sql.statement.DemoEntity;

/**
 * @author Qt
 * @since 1.8,  Aug 23, 2023
 */
public class DemoEntityDelete extends DeleteStatement<DemoEntityDelete> {
	public DemoEntityDelete() {
		super(DemoEntity.class);
	}

	public DemoEntityDelete(String alias) {
		super(DemoEntity.class, alias);
	}


	@Override
	protected DemoEntityAndSegment<DemoEntityDelete> buildWhere() {
		return new DemoEntityAndSegment<>(getThis(), getTable());
	}

	@SuppressWarnings("unchecked")
	@Override
	public DemoEntityAndSegment<DemoEntityDelete> where() {
		return super.where();
	}

}
