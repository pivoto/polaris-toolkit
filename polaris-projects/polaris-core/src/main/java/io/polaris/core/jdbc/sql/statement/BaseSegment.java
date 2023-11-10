package io.polaris.core.jdbc.sql.statement;

/**
 * @author Qt
 * @since 1.8,  Aug 20, 2023
 */
public abstract class BaseSegment<S extends BaseSegment<S>> implements Segment<S> {

	@SuppressWarnings("unchecked")
	protected S getThis() {
		return (S) this;
	}

}
