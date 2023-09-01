package io.polaris.core.jdbc.sql.statement.demo;

import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.statement.DemoEntity;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.core.jdbc.sql.statement.segment.JoinBuilder;
import io.polaris.core.jdbc.sql.statement.segment.JoinSegment;

/**
 * @author Qt
 * @since 1.8,  Aug 23, 2023
 */
public class DemoEntityJoinSegment<O extends SelectStatement<O>>
	extends JoinSegment<O, DemoEntityJoinSegment<O>> {

	public DemoEntityJoinSegment(O owner, TextNode conj, Class<?> entityClass, String alias) {
		super(owner, conj, entityClass, alias);
	}

	public DemoEntityJoinSegment(O owner, TextNode conj, SelectStatement<?> select, String alias) {
		super(owner, conj, select, alias);
	}

	public static <O extends SelectStatement<O>> JoinBuilder<O, DemoEntityJoinSegment<O>> builder() {
		return (statement, conj, alias) -> new DemoEntityJoinSegment<>(statement, conj, DemoEntity.class, alias);
	}


	@Override
	protected DemoEntityColSegment<DemoEntityJoinSegment<O>> buildSelect() {
		return super.buildSelect();
	}

	@Override
	protected DemoEntityAndSegment<DemoEntityJoinSegment<O>> buildWhere() {
		return super.buildWhere();
	}

	@Override
	public DemoEntityAndSegment<DemoEntityJoinSegment<O>> on() {
		return super.on();
	}

	@Override
	public DemoEntityAndSegment<DemoEntityJoinSegment<O>> where() {
		return super.where();
	}

	@Override
	public DemoEntityColSegment<DemoEntityJoinSegment<O>> select() {
		return super.select();
	}

	public DemoEntityJoinSegment<O> id() {
		return select("id");
	}

	public DemoEntityJoinSegment<O> id(String alias) {
		return select("id", alias);
	}
}
