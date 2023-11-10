package io.polaris.core.jdbc.sql.statement.demo;

import io.polaris.core.jdbc.sql.statement.DemoEntity;
import io.polaris.core.jdbc.sql.statement.UpdateStatement;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8,  Aug 23, 2023
 */
public class DemoEntityUpdate extends UpdateStatement<DemoEntityUpdate> {
	public DemoEntityUpdate() {
		super(DemoEntity.class);
	}

	public DemoEntityUpdate(String alias) {
		super(DemoEntity.class, alias);
	}

//	@Override
//	protected DemoEntityTableSegment buildTable(Class<?> entityClass, String alias) {
//		return new DemoEntityTableSegment(alias);
//	}

	@Override
	protected DemoEntityAndSegment<DemoEntityUpdate> buildWhere() {
		return new DemoEntityAndSegment<>(getThis(), getTable());
	}

	@SuppressWarnings("unchecked")
	@Override
	public DemoEntityAndSegment<DemoEntityUpdate> where() {
		return super.where();
	}



	public DemoEntityUpdate id(Object value) {
		return super.column("id", value);
	}
	public DemoEntityUpdate id(Object value, BiPredicate<String, Object> predicate) {
		return super.column("id", value, predicate);
	}
	public DemoEntityUpdate id(Object value, Supplier<Boolean> predicate) {
		return super.column("id", value, predicate);
	}
}
