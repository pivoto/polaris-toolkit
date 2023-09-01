package io.polaris.core.jdbc.sql.statement.segment;

import io.polaris.core.annotation.AnnotationProcessing;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.statement.Segment;

/**
 * @author Qt
 * @since 1.8,  Aug 20, 2023
 */
@AnnotationProcessing
public class OrSegment<O extends Segment<O>, S extends OrSegment<O, S>> extends WhereSegment<O, S> {

	@AnnotationProcessing
	public <T extends TableSegment<?>> OrSegment(O owner, T table) {
		super(owner, table, SqlNodes.OR);
	}

}
