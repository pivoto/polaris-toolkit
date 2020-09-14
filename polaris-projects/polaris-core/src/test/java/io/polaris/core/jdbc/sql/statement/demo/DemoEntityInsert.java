package io.polaris.core.jdbc.sql.statement.demo;

import io.polaris.core.jdbc.sql.statement.DemoEntity;
import io.polaris.core.jdbc.sql.statement.InsertStatement;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8,  Aug 23, 2023
 */
public class DemoEntityInsert extends InsertStatement<DemoEntityInsert> {
	public DemoEntityInsert() {
		super(DemoEntity.class);
	}

//	@Override
//	protected DemoEntityTableSegment buildTable(Class<?> entityClass, String alias) {
//		return new DemoEntityTableSegment(alias);
//	}


	public DemoEntityInsert id(Object value) {
		return super.column("id", value);
	}

	public DemoEntityInsert id(Object value, BiPredicate<String, Object> predicate) {
		return super.column("id", value, predicate);
	}
	public DemoEntityInsert id(Object value, Supplier<Boolean> predicate) {
		return super.column("id", value, predicate);
	}
}
