package io.polaris.core.jdbc.sql.statement;

/**
 * @author Qt
 * @since 1.8,  Aug 20, 2023
 */
public interface Statement<S extends Statement<S>> extends Segment<S>, SqlNodeBuilder {

}
