package io.polaris.core.jdbc.sql.statement.demo;

import io.polaris.core.jdbc.sql.statement.Segment;
import io.polaris.core.jdbc.sql.statement.segment.GroupBySegment;
import io.polaris.core.jdbc.sql.statement.segment.TableSegment;

/**
 * @author Qt
 * @since  Aug 23, 2023
 */
public class DemoEntityGroupBySegment<O extends Segment<O>> extends GroupBySegment<O, DemoEntityGroupBySegment<O>> {

	public DemoEntityGroupBySegment(O owner, TableSegment<?> table) {
		super(owner, table);
	}


	public DemoEntityGroupBySegment<O> id() {
		return super.column("id");
	}
}
