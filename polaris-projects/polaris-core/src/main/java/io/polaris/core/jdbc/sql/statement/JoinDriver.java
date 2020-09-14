package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.statement.segment.JoinBuilder;
import io.polaris.core.jdbc.sql.statement.segment.JoinSegment;

import java.util.function.Consumer;

/**
 * @author Qt
 * @since 1.8,  Aug 23, 2023
 */
public class JoinDriver<O extends SelectStatement<O>, J extends JoinSegment<O, J>> extends BaseSegment<JoinDriver<O, J>> {
	private final O owner;
	private final JoinBuilder<O, J> builder;
	private final Consumer<J> consumer;
	private TextNode conj = SqlNodes.JOIN;

	JoinDriver(O owner, Consumer<J> consumer, JoinBuilder<O, J> builder) {
		this.owner = owner;
		this.consumer = consumer;
		this.builder = builder;
	}

	public J alias(String alias) {
		J join = builder.build(owner, conj, alias);
		consumer.accept(join);
		return join;
	}

	public JoinDriver<O, J> inner() {
		this.conj = SqlNodes.INNER_JOIN;
		return getThis();
	}

	public JoinDriver<O, J> left() {
		this.conj = SqlNodes.LEFT_JOIN;
		return getThis();
	}

	public JoinDriver<O, J> right() {
		this.conj = SqlNodes.RIGHT_JOIN;
		return getThis();
	}

	public JoinDriver<O, J> outer() {
		this.conj = SqlNodes.OUTER_JOIN;
		return getThis();
	}


}
