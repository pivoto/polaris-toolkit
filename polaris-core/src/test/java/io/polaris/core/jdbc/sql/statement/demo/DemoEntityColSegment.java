package io.polaris.core.jdbc.sql.statement.demo;

import io.polaris.core.jdbc.sql.statement.Segment;
import io.polaris.core.jdbc.sql.statement.segment.SelectSegment;
import io.polaris.core.jdbc.sql.statement.segment.TableSegment;

/**
 * @author Qt
 * @since  Aug 23, 2023
 */
public class DemoEntityColSegment<O extends Segment<O>> extends SelectSegment<O, DemoEntityColSegment<O>> {

	public DemoEntityColSegment(O owner, TableSegment<? extends TableSegment<?>> table) {
		super(owner, table);
	}


	public DemoEntityColSegment<O> id(){
		return column("id");
	}
}
