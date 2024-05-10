package io.polaris.core.jdbc.sql.statement.segment;

import io.polaris.core.annotation.AnnotationProcessing;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.statement.Segment;

/**
 * @author Qt
 * @since  Aug 20, 2023
 */
@AnnotationProcessing
public class AndSegment<O extends Segment<O>, S extends AndSegment<O, S>> extends WhereSegment<O,S> {

	@AnnotationProcessing
	public <T extends TableSegment<?>> AndSegment(O owner, T table) {
		super(owner, table, SqlNodes.AND);
	}

}
