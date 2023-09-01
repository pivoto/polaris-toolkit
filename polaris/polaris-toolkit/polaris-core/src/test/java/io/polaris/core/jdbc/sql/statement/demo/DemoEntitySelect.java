package io.polaris.core.jdbc.sql.statement.demo;

import io.polaris.core.jdbc.sql.statement.DemoEntity;
import io.polaris.core.jdbc.sql.statement.SelectStatement;

/**
 * @author Qt
 * @since 1.8,  Aug 23, 2023
 */
public class DemoEntitySelect extends SelectStatement<DemoEntitySelect> {

	public DemoEntitySelect() {
		super(DemoEntity.class);
	}

	public DemoEntitySelect(String alias) {
		super(DemoEntity.class, alias);
	}

//	@Override
//	protected DemoEntityTableSegment buildTable(Class<?> entityClass, String alias) {
//		return new DemoEntityTableSegment(alias);
//	}

	@Override
	protected DemoEntityColSegment<DemoEntitySelect> buildSelect() {
		return new DemoEntityColSegment<>(getThis(), getTable());
	}


	@Override
	protected DemoEntityAndSegment<DemoEntitySelect> buildWhere() {
		return new DemoEntityAndSegment<>(getThis(), getTable());
	}

	@Override
	protected DemoEntityGroupBySegment<DemoEntitySelect> buildGroupBy() {
		return new DemoEntityGroupBySegment<>(getThis(), getTable());
	}

	@Override
	protected DemoEntityOrderBySegment<DemoEntitySelect> buildOrderBy() {
		return new DemoEntityOrderBySegment<>(getThis(), getTable());
	}

	@SuppressWarnings("unchecked")
	@Override
	public DemoEntityColSegment<DemoEntitySelect> select() {
		return super.select();
	}


	@SuppressWarnings("unchecked")
	@Override
	public DemoEntityAndSegment<DemoEntitySelect> where() {
		return super.where();
	}

	@SuppressWarnings("unchecked")
	@Override
	public DemoEntityGroupBySegment<DemoEntitySelect> groupBy() {
		return super.groupBy();
	}

	@SuppressWarnings("unchecked")
	@Override
	public DemoEntityAndSegment<DemoEntitySelect> having() {
		return super.having();
	}

	@SuppressWarnings("unchecked")
	@Override
	public DemoEntityOrderBySegment<DemoEntitySelect> orderBy() {
		return super.orderBy();
	}

	public DemoEntitySelect id() {
		return super.select("id");
	}

	public DemoEntitySelect id(String alias) {
		return super.select("id", alias);
	}

}
