package io.polaris.core.jdbc.sql.node;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.polaris.core.jdbc.sql.BoundSql;
import io.polaris.core.jdbc.sql.PreparedSql;

/**
 * @author Qt
 * @since Aug 11, 2023
 */
public interface SqlNode {

	PreparedSql asPreparedSql();

	BoundSql asBoundSql(Predicate<String> varPropFilter, VarNameGenerator generator, String openVarToken, String closeVarToken);

	default BoundSql asBoundSql(Predicate<String> varPropFilter) {
		return asBoundSql(varPropFilter, VarNameGenerator.newInstance(), "#{", "}");
	}

	default BoundSql asBoundSql(Predicate<String> varPropFilter, VarNameGenerator generator) {
		return asBoundSql(varPropFilter, generator, "#{", "}");
	}

	default BoundSql asBoundSql(Predicate<String> varPropFilter, String openVarToken, String closeVarToken) {
		return asBoundSql(varPropFilter, VarNameGenerator.newInstance(), openVarToken, closeVarToken);
	}

	default BoundSql asBoundSql(VarNameGenerator generator, String openVarToken, String closeVarToken) {
		return asBoundSql((Predicate<String>) null, generator, openVarToken, closeVarToken);
	}

	default BoundSql asBoundSql() {
		return asBoundSql((Predicate<String>) null, VarNameGenerator.newInstance(), "#{", "}");
	}

	default BoundSql asBoundSql(VarNameGenerator generator) {
		return asBoundSql((Predicate<String>) null, generator, "#{", "}");
	}

	default BoundSql asBoundSql(String openVarToken, String closeVarToken) {
		return asBoundSql((Predicate<String>) null, VarNameGenerator.newInstance(), openVarToken, closeVarToken);
	}


	default SqlNode copy() {
		return copy(true);
	}

	SqlNode copy(boolean withVarValue);

	default boolean isContainerNode() {
		return false;
	}

	default boolean isSkipped() {
		return false;
	}

	default void skip(boolean skip) {
		throw new UnsupportedOperationException();
	}

	default void skipIfMissingVarValue() {
		throw new UnsupportedOperationException();
	}

	default boolean isEmpty() {
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

	default void visitSubsetWritable(Consumer<SqlNodeOps> visitor) {
		throw new UnsupportedOperationException();
	}

	default void visitSubset(Consumer<SqlNode> visitor) {
		throw new UnsupportedOperationException();
	}

	default boolean replaceFirstSub(Predicate<SqlNode> predicate, Supplier<SqlNode> supplier) {
		throw new UnsupportedOperationException();
	}

	default int replaceAllSubs(Predicate<SqlNode> predicate, Supplier<SqlNode> supplier) {
		throw new UnsupportedOperationException();
	}

	default boolean removeFirstSub(Predicate<SqlNode> predicate) {
		throw new UnsupportedOperationException();
	}


	default int removeAllSubs(Predicate<SqlNode> predicate) {
		throw new UnsupportedOperationException();
	}

	default void clearSkippedSubs() {
		throw new UnsupportedOperationException();
	}

	default boolean containsVarName(String key) {
		throw new UnsupportedOperationException();
	}


	default void bindSubsetVarValues(Map<String, Object> params) {
		bindSubsetVarValues(params, true);
	}


	default void bindSubsetVarValues(Map<String, Object> params, boolean ignoreNull) {
		throw new UnsupportedOperationException();
	}

	default void bindSubsetVarValue(String varName, Object varValue) {
		bindSubsetVarValue(varName, varValue, true);
	}

	default void bindSubsetVarValue(String varName, Object varValue, boolean ignoreNull) {
		throw new UnsupportedOperationException();
	}

	default void removeVarValue(String varName) {
		throw new UnsupportedOperationException();
	}


	default boolean isVarNode() {
		return false;
	}

	default boolean isMixedNode() {
		return false;
	}

	default boolean isDynamicNode() {
		return false;
	}

	default String getVarName() {
		throw new UnsupportedOperationException();
	}

	default Object getVarValue() {
		throw new UnsupportedOperationException();
	}

	default void removeVarValue() {
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
