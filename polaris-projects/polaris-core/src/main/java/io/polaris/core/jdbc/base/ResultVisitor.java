package io.polaris.core.jdbc.base;

/**
 * @author Qt
 * @since 1.8,  Feb 07, 2024
 */
@FunctionalInterface
public interface ResultVisitor<T> {

	void visit(T t);
}
