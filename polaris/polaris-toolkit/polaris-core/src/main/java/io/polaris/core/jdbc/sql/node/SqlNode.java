package io.polaris.core.jdbc.sql.node;

import io.polaris.core.jdbc.sql.BoundSql;
import io.polaris.core.jdbc.sql.PreparedSql;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Qt
 * @since 1.8,  Aug 11, 2023
 */
public interface SqlNode {

	PreparedSql asPreparedSql();

	BoundSql asBoundSql(VarNameGenerator generator, String openVarToken, String closeVarToken);


	default BoundSql asBoundSql() {
		return asBoundSql("#{", "}" );
	}

	default BoundSql asBoundSql(String openVarToken, String closeVarToken) {
		AtomicInteger index = new AtomicInteger();
		VarNameGenerator generator = () -> "_" + (index.getAndIncrement());
		return asBoundSql(generator, openVarToken, closeVarToken);
	}

	default boolean isContainerNode() {
		return false;
	}

	default void skipIfMissingVarParameter() {
		throw new UnsupportedOperationException();
	}

	default List<SqlNode> subset() {
		throw new UnsupportedOperationException();
	}

	default void addNode(SqlNode sqlNode) {
		throw new UnsupportedOperationException();
	}


	default void addNode(int i, SqlNode sqlNode) {
		throw new UnsupportedOperationException();
	}

	default void addNodes(List<SqlNode> sqlNodes) {
		throw new UnsupportedOperationException();
	}

	default void addNodes(SqlNode... sqlNodes) {
		throw new UnsupportedOperationException();
	}


	default boolean removeFirstNode(Predicate<SqlNode> predicate) {
		throw new UnsupportedOperationException();
	}


	default boolean removeAllNodes(Predicate<SqlNode> predicate) {
		throw new UnsupportedOperationException();
	}

	default void clearSkippedNodes() {
		throw new UnsupportedOperationException();
	}


	default boolean containsVarName(String key) {
		throw new UnsupportedOperationException();
	}

	default void visitNode(Consumer<SqlNode> visitor) {
		throw new UnsupportedOperationException();
	}

	default void bindVarValues(Map<String, Object> params) {
		bindVarValues(params, true);
	}


	default void bindVarValues(Map<String, Object> params, boolean ignoreNull) {
		throw new UnsupportedOperationException();
	}

	default boolean isVarNode() {
		return false;
	}

	default String getVarName() {
		throw new UnsupportedOperationException();
	}

	default Object getVarValue() {
		throw new UnsupportedOperationException();
	}

	default void removeVarParameter() {
		throw new UnsupportedOperationException();
	}

	default void bindVarValue(Object param) {
		throw new UnsupportedOperationException();
	}

	default boolean isTextNode() {
		return false;
	}

	default String getText() {
		throw new UnsupportedOperationException();
	}


}
