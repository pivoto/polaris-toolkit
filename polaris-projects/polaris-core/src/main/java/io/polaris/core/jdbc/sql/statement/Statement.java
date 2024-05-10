package io.polaris.core.jdbc.sql.statement;

/**
 * @author Qt
 * @since  Aug 20, 2023
 */
public interface Statement<S extends Statement<S>> extends Segment<S>, SqlNodeBuilder {

}
