package io.polaris.core.jdbc.sql.statement.segment;

import io.polaris.core.annotation.AnnotationProcessing;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.statement.SelectStatement;

/**
 * @author Qt
 * @since  Aug 23, 2023
 */
@FunctionalInterface
@AnnotationProcessing
public interface JoinBuilder<O extends SelectStatement<O>, J extends JoinSegment<O, J>> {

	@AnnotationProcessing
	J build(O statement, TextNode conj, String alias);
}
