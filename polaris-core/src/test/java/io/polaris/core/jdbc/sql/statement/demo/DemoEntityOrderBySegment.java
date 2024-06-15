package io.polaris.core.jdbc.sql.statement.demo;

import io.polaris.core.jdbc.sql.statement.Segment;
import io.polaris.core.jdbc.sql.statement.segment.OrderBySegment;
import io.polaris.core.jdbc.sql.statement.segment.TableSegment;

/**
 * @author Qt
 * @since  Aug 23, 2023
 */
public class DemoEntityOrderBySegment<O extends Segment<O>> extends OrderBySegment<O,DemoEntityOrderBySegment<O>> {
	public DemoEntityOrderBySegment(O owner, TableSegment<?> table) {
		super(owner, table);
	}

	public DemoEntityOrderBySegment<O> id() {
		return column("id");
	}
}
