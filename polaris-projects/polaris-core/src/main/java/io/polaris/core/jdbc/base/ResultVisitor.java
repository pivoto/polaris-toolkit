package io.polaris.core.jdbc.base;

/**
 * @author Qt
 * @since  Feb 07, 2024
 */
@FunctionalInterface
public interface ResultVisitor<T> {

	void visit(T t);
}
