package io.polaris.core.jdbc.sql.statement.segment;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.polaris.core.annotation.AnnotationProcessing;
import io.polaris.core.assertion.Assertions;
import io.polaris.core.consts.StdConsts;
import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.jdbc.sql.SqlTextParsers;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.statement.BaseSegment;
import io.polaris.core.jdbc.sql.statement.Segment;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.core.jdbc.sql.statement.SqlNodeBuilder;
import io.polaris.core.jdbc.sql.statement.expression.AggregateFunction;
import io.polaris.core.jdbc.sql.statement.expression.Expression;
import io.polaris.core.jdbc.sql.statement.expression.Expressions;
import io.polaris.core.jdbc.sql.statement.expression.LargeInExpression;
import io.polaris.core.jdbc.sql.statement.expression.LargeNotInExpression;
import io.polaris.core.jdbc.sql.statement.expression.LogicalExpression;
import io.polaris.core.jdbc.sql.statement.expression.MultiColumnLogicalExpression;
import io.polaris.core.reflect.GetterFunction;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since  Aug 20, 2023
 */
@AnnotationProcessing
public class CriterionSegment<O extends Segment<O>, S extends CriterionSegment<O, S>> extends BaseSegment<S> implements SqlNodeBuilder, TableAccessibleHolder {
	private final O owner;
	// region 直接SqlNode
	/** 直接SqlNode */
	private SqlNode sql;
	// endregion

	// region 组合
	/** And子组 */
	private AndSegment<O, ?> and;
	/** Or子组 */
	private OrSegment<O, ?> or;
	// endregion

	// region 表达式
	/** 实体表 */
	private TableSegment<?> table;
	/** 实体字段 */
	private String field;
	/** 表达式 */
	private ExpressionSegment<?> expression;
	private transient String _rawColumn;
	private TableAccessible tableAccessible;
	// endregion

	// region subSelect
	private SelectStatement<?> subSelect;
	private TextNode subSelectSymbol;
	private boolean subSelectWithColumn = false;
	// endregion


	public CriterionSegment(O owner, SqlNode sql) {
		this.owner = owner;
		this.sql = sql;
	}

	public CriterionSegment(O owner, AndSegment<O, ?> and) {
		this.owner = owner;
		this.and = and;
	}

	public CriterionSegment(O owner, OrSegment<O, ?> or) {
		this.owner = owner;
		this.or = or;
	}


	public CriterionSegment(O owner, String rawColumn) {
		this.owner = owner;
		this._rawColumn = rawColumn;
	}

	public <T extends TableSegment<?>> CriterionSegment(O owner, TableAccessible tableAccessible, T table, String field) {
		this.owner = owner;
		this.table = table;
		this.field = field;
		this.tableAccessible = tableAccessible;
	}

	@Override
	public TableAccessible getTableAccessible() {
		return tableAccessible;
	}

	@Override
	public SqlNode toSqlNode() {
		if (this.sql != null) {
			return sql;
		}
		if (this.or != null) {
			return this.or.toSqlNode();
		}
		if (this.and != null) {
			return this.and.toSqlNode();
		}
		if (this.subSelect != null && this.subSelectSymbol != null) {
			ContainerNode sql = new ContainerNode();
			if (subSelectWithColumn) {
				if (this.expression != null) {
					sql.addNode(this.expression.toSqlNode(column()));
				}else{
					sql.addNode(SqlNodes.text(column()));
				}
			}
			sql.addNode(this.subSelectSymbol);
			sql.addNode(SqlNodes.LEFT_PARENTHESIS);
			sql.addNode(SqlNodes.LF);
			sql.addNode(this.subSelect.toSqlNode());
			sql.addNode(SqlNodes.LF);
			sql.addNode(SqlNodes.RIGHT_PARENTHESIS);
			return sql;
		}
		if (this.expression != null) {
			return this.expression.toSqlNode(column());
		}
		return SqlNodes.EMPTY;
	}

	public O end() {
		return owner;
	}

	protected S rawColumn(String rawColumn) {
		// 解析表字段名
		rawColumn = SqlTextParsers.resolveTableRef(rawColumn, tableAccessible);
		this._rawColumn = rawColumn;
		return getThis();
	}

	private String column() {
		if (Strings.isNotBlank(_rawColumn)) {
			return _rawColumn;
		}
		if (table == null || Strings.isBlank(field)) {
			return SymbolConsts.EMPTY;
		}
		this._rawColumn = table.getColumnExpression(field);
		return _rawColumn;
	}

	public O of(Function<String, SqlNode> function) {
		SqlNode sqlNode = function.apply(column());
		if (sqlNode != null) {
			this.sql = sqlNode;
		}
		return end();
	}

	public O of(Function<String, SqlNode> function, Predicate<String> predicate) {
		if (predicate.test(column())) {
			SqlNode sqlNode = function.apply(column());
			if (sqlNode != null) {
				this.sql = sqlNode;
			}
		}
		return end();
	}

	public S apply(String functionPattern, TableField[] extFields, Map<String, Object> bindings) {
		return apply(Expressions.pattern(functionPattern), extFields, bindings);
	}

	public S apply(String functionPattern, TableField[] extFields, Object... bindings) {
		return apply(Expressions.pattern(functionPattern), extFields, bindings);
	}

	public S apply(String functionPattern, TableField... extFields) {
		return apply(Expressions.pattern(functionPattern), extFields);
	}

	public S apply(String functionPattern) {
		return apply(Expressions.pattern(functionPattern), StdConsts.EMPTY_ARRAY);
	}

	public S apply(String functionPattern, Object[] bindings) {
		return apply(Expressions.pattern(functionPattern), bindings);
	}

	public S apply(Expression function, TableField[] extFields, Map<String, Object> bindings) {
		this.expression = new ExpressionSegment<>(this.expression, tableAccessible, extFields, function, bindings);
		return getThis();
	}

	public S apply(Expression function, TableField[] extFields, Object... bindings) {
		this.expression = new ExpressionSegment<>(this.expression, tableAccessible, extFields, function, bindings);
		return getThis();
	}

	public S apply(Expression function, TableField... extFields) {
		return apply(function, extFields, StdConsts.EMPTY_ARRAY);
	}

	public S apply(Expression function) {
		this.expression = new ExpressionSegment<>(this.expression, function, StdConsts.EMPTY_ARRAY);
		return getThis();
	}

	public S apply(Expression function, Object[] bindings) {
		this.expression = new ExpressionSegment<>(this.expression, function, bindings);
		return getThis();
	}

	public S apply(Expression function, Map<String, Object> bindings) {
		this.expression = new ExpressionSegment<>(this.expression, function, bindings);
		return getThis();
	}

	public S apply(String functionPattern, int tableIndex, String field) {
		return apply(functionPattern, TableField.of(tableIndex, field));
	}

	public S apply(Expression function, int tableIndex, String field) {
		return apply(function, TableField.of(tableIndex, field));
	}


	public S apply(String functionPattern, String tableAlias, String field) {
		return apply(functionPattern, TableField.of(tableAlias, field));
	}

	public S apply(Expression function, String tableAlias, String field) {
		return apply(function, TableField.of(tableAlias, field));
	}

	public <T, R> S apply(String functionPattern, int tableIndex, GetterFunction<T, R> field) {
		return apply(functionPattern, TableField.of(tableIndex, field));
	}

	public <T, R> S apply(Expression function, int tableIndex, GetterFunction<T, R> field) {
		return apply(function, TableField.of(tableIndex, field));
	}

	public <T, R> S apply(String functionPattern, String tableAlias, GetterFunction<T, R> field) {
		return apply(functionPattern, TableField.of(tableAlias, field));
	}

	public <T, R> S apply(Expression function, String tableAlias, GetterFunction<T, R> field) {
		return apply(function, TableField.of(tableAlias, field));
	}

	public <I extends SelectStatement<?>> S exists(I subSelect) {
		subSelect.nested(this.tableAccessible);
		this.subSelect = subSelect;
		this.subSelectSymbol = SqlNodes.EXISTS;
		this.subSelectWithColumn = false;
		return getThis();
	}

	public <I extends SelectStatement<?>> S exists(I subSelect, Consumer<I> append) {
		subSelect.nested(this.tableAccessible);
		this.subSelect = subSelect;
		this.subSelectSymbol = SqlNodes.EXISTS;
		this.subSelectWithColumn = false;
		append.accept(subSelect);
		return getThis();
	}

	public <I extends SelectStatement<?>> S notExists(I subSelect) {
		subSelect.nested(this.tableAccessible);
		this.subSelect = subSelect;
		this.subSelectSymbol = SqlNodes.NOT_EXISTS;
		this.subSelectWithColumn = false;
		return getThis();
	}

	public <I extends SelectStatement<?>> S notExists(I subSelect, Consumer<I> append) {
		subSelect.nested(this.tableAccessible);
		this.subSelect = subSelect;
		this.subSelectSymbol = SqlNodes.NOT_EXISTS;
		this.subSelectWithColumn = false;
		append.accept(subSelect);
		return getThis();
	}

	public <I extends SelectStatement<?>> S in(I subSelect) {
		subSelect.nested(this.tableAccessible);
		this.subSelect = subSelect;
		this.subSelectSymbol = SqlNodes.IN;
		this.subSelectWithColumn = true;
		return getThis();
	}

	public <I extends SelectStatement<?>> S in(I subSelect, Consumer<I> append) {
		subSelect.nested(this.tableAccessible);
		this.subSelect = subSelect;
		this.subSelectSymbol = SqlNodes.IN;
		this.subSelectWithColumn = true;
		append.accept(subSelect);
		return getThis();
	}

	public <I extends SelectStatement<?>> S notIn(I subSelect) {
		subSelect.nested(this.tableAccessible);
		this.subSelect = subSelect;
		this.subSelectSymbol = SqlNodes.NOT_IN;
		this.subSelectWithColumn = true;
		return getThis();
	}

	public <I extends SelectStatement<?>> S notIn(I subSelect, Consumer<I> append) {
		subSelect.nested(this.tableAccessible);
		this.subSelect = subSelect;
		this.subSelectSymbol = SqlNodes.NOT_IN;
		this.subSelectWithColumn = true;
		append.accept(subSelect);
		return getThis();
	}


	public S count() {
		this.expression = new ExpressionSegment<>(this.expression, AggregateFunction.COUNT.getExpression());
		return getThis();
	}

	public S sum() {
		this.expression = new ExpressionSegment<>(this.expression, AggregateFunction.SUM.getExpression());
		return getThis();
	}

	public S max() {
		this.expression = new ExpressionSegment<>(this.expression, AggregateFunction.MAX.getExpression());
		return getThis();
	}

	public S min() {
		this.expression = new ExpressionSegment<>(this.expression, AggregateFunction.MIN.getExpression());
		return getThis();
	}

	public S avg() {
		this.expression = new ExpressionSegment<>(this.expression, AggregateFunction.AVG.getExpression());
		return getThis();
	}

	public O isNull() {
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.IS_NULL.getExpression());
		return end();
	}

	public O isNull(Supplier<Boolean> predicate) {
		if (Boolean.TRUE.equals(predicate.get())) {
			isNull();
		}
		return end();
	}

	public O isNull(boolean predicate) {
		if (predicate) {
			isNull();
		}
		return end();
	}

	public O notNull() {
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.NOT_NULL.getExpression());
		return end();
	}

	public O notNull(Supplier<Boolean> predicate) {
		if (Boolean.TRUE.equals(predicate.get())) {
			notNull();
		}
		return end();
	}

	public O notNull(boolean predicate) {
		if (predicate) {
			notNull();
		}
		return end();
	}

	public O isTrue() {
		return eq(1);
	}

	public O isTrue(Predicate<Object> predicate) {
		return eq(1, predicate);
	}

	public O isTrue(boolean predicate) {
		return eq(1, predicate);
	}

	public O isFalse() {
		return eq(0);
	}

	public O isFalse(Predicate<Object> predicate) {
		return eq(0, predicate);
	}

	public O isFalse(boolean predicate) {
		return eq(0, predicate);
	}

	public O eq(Object value) {
		if (value == null) {
			return isNull();
		}
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.EQ.getExpression(), value);
		return end();
	}

	public O eq(Object value, Predicate<Object> predicate) {
		if (predicate.test(value)) {
			eq(value);
		}
		return end();
	}

	public O eq(Object value, boolean predicate) {
		if (predicate) {
			eq(value);
		}
		return end();
	}

	public O ne(Object value) {
		if (value == null) {
			return notNull();
		}
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.NE.getExpression(), value);
		return end();
	}

	public O ne(Object value, Predicate<Object> predicate) {
		if (predicate.test(value)) {
			ne(value);
		}
		return end();
	}

	public O ne(Object value, boolean predicate) {
		if (predicate) {
			ne(value);
		}
		return end();
	}

	public O gt(Object value) {
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.GT.getExpression(), value);
		return end();
	}

	public O gt(Object value, Predicate<Object> predicate) {
		if (predicate.test(value)) {
			gt(value);
		}
		return end();
	}

	public O gt(Object value, boolean predicate) {
		if (predicate) {
			gt(value);
		}
		return end();
	}

	public O ge(Object value) {
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.GE.getExpression(), value);
		return end();
	}

	public O ge(Object value, Predicate<Object> predicate) {
		if (predicate.test(value)) {
			ge(value);
		}
		return end();
	}

	public O ge(Object value, boolean predicate) {
		if (predicate) {
			ge(value);
		}
		return end();
	}

	public O lt(Object value) {
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.LT.getExpression(), value);
		return end();
	}

	public O lt(Object value, Predicate<Object> predicate) {
		if (predicate.test(value)) {
			lt(value);
		}
		return end();
	}

	public O lt(Object value, boolean predicate) {
		if (predicate) {
			lt(value);
		}
		return end();
	}

	public O le(Object value) {
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.LE.getExpression(), value);
		return end();
	}

	public O le(Object value, Predicate<Object> predicate) {
		if (predicate.test(value)) {
			le(value);
		}
		return end();
	}

	public O le(Object value, boolean predicate) {
		if (predicate) {
			le(value);
		}
		return end();
	}

	public O between(Object value1, Object value2) {
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.BETWEEN.getExpression(), value1, value2);
		return end();
	}

	public O between(Object value1, Object value2, Predicate<Object[]> predicate) {
		if (predicate.test(new Object[]{value1, value2})) {
			between(value1, value2);
		}
		return end();
	}

	public O between(Object value1, Object value2, boolean predicate) {
		if (predicate) {
			between(value1, value2);
		}
		return end();
	}


	public O notBetween(Object value1, Object value2) {
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.NOT_BETWEEN.getExpression(), value1, value2);
		return end();
	}

	public O notBetween(Object value1, Object value2, Predicate<Object[]> predicate) {
		if (predicate.test(new Object[]{value1, value2})) {
			notBetween(value1, value2);
		}
		return end();
	}

	public O notBetween(Object value1, Object value2, boolean predicate) {
		if (predicate) {
			notBetween(value1, value2);
		}
		return end();
	}

	public <E> O in(Collection<E> value, int limitSize) {
		Assertions.assertTrue(limitSize > 0,"集合元素数量上限必须大于0");
		if (value.size() < limitSize) {
			this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.IN.getExpression(), value);
		} else {
			this.expression = new ExpressionSegment<>(this.expression, LargeInExpression.of(limitSize), value);
		}
		return end();
	}
	public <E> O in(Collection<E> value) {
		if (value.size() < 1000) {
			this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.IN.getExpression(), value);
		} else {
			this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.LARGE_IN.getExpression(), value);
		}
		return end();
	}

	public <E> O in(Collection<E> value, Predicate<Collection<E>> predicate) {
		if (predicate.test(value)) {
			in(value);
		}
		return end();
	}

	public <E> O in(Collection<E> value, boolean predicate) {
		if (predicate) {
			in(value);
		}
		return end();
	}

	public <E> O inLarge(Collection<E> value) {
		this.expression = new ExpressionSegment<>(this.expression, LargeInExpression.DEFAULT, value);
		return end();
	}

	public <E> O inLarge(Collection<E> value, Predicate<Collection<E>> predicate) {
		if (predicate.test(value)) {
			inLarge(value);
		}
		return end();
	}

	public <E> O inLarge(Collection<E> value, boolean predicate) {
		if (predicate) {
			inLarge(value);
		}
		return end();
	}

	public <E> O inLarge(Collection<E> value, int limit) {
		this.expression = new ExpressionSegment<>(this.expression, LargeInExpression.of(limit), value);
		return end();
	}

	public <E> O inLarge(Collection<E> value, int limit, Predicate<Collection<E>> predicate) {
		if (predicate.test(value)) {
			inLarge(value, limit);
		}
		return end();
	}

	public <E> O inLarge(Collection<E> value, int limit, boolean predicate) {
		if (predicate) {
			inLarge(value, limit);
		}
		return end();
	}

	public <E> O notIn(Collection<E> value) {
		if (value.size() < 1000) {
			this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.NOT_IN.getExpression(), value);
		} else {
			this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.LARGE_NOT_IN.getExpression(), value);
		}
		return end();
	}

	public <E> O notIn(Collection<E> value, Predicate<Collection<E>> predicate) {
		if (predicate.test(value)) {
			notIn(value);
		}
		return end();
	}

	public <E> O notIn(Collection<E> value, boolean predicate) {
		if (predicate) {
			notIn(value);
		}
		return end();
	}


	public <E> O notInLarge(Collection<E> value) {
		this.expression = new ExpressionSegment<>(this.expression, LargeNotInExpression.DEFAULT, value);
		return end();
	}

	public <E> O notInLarge(Collection<E> value, Predicate<Collection<E>> predicate) {
		if (predicate.test(value)) {
			notInLarge(value);
		}
		return end();
	}

	public <E> O notInLarge(Collection<E> value, boolean predicate) {
		if (predicate) {
			notInLarge(value);
		}
		return end();
	}

	public <E> O notInLarge(Collection<E> value, int limit) {
		this.expression = new ExpressionSegment<>(this.expression, LargeNotInExpression.of(limit), value);
		return end();
	}

	public <E> O notInLarge(Collection<E> value, int limit, Predicate<Collection<E>> predicate) {
		if (predicate.test(value)) {
			notInLarge(value, limit);
		}
		return end();
	}

	public <E> O notInLarge(Collection<E> value, int limit, boolean predicate) {
		if (predicate) {
			notInLarge(value, limit);
		}
		return end();
	}


	/**
	 * like 'value'
	 */
	public O like(String value) {
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.LIKE.getExpression(), value);
		return end();
	}

	public O like(String value, Predicate<String> predicate) {
		if (predicate.test(value)) {
			like(value);
		}
		return end();
	}

	public O like(String value, boolean predicate) {
		if (predicate) {
			like(value);
		}
		return end();
	}

	/**
	 * like '%value%'
	 */
	public O contains(String value) {
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.CONTAINS.getExpression(), value);
		return end();
	}

	public O contains(String value, Predicate<String> predicate) {
		if (predicate.test(value)) {
			contains(value);
		}
		return end();
	}

	public O contains(String value, boolean predicate) {
		if (predicate) {
			contains(value);
		}
		return end();
	}

	/**
	 * like 'value%'
	 */
	public O startsWith(String value) {
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.STARTS_WITH.getExpression(), value);
		return end();
	}

	public O startsWith(String value, Predicate<String> predicate) {
		if (predicate.test(value)) {
			startsWith(value);
		}
		return end();
	}

	public O startsWith(String value, boolean predicate) {
		if (predicate) {
			startsWith(value);
		}
		return end();
	}


	/**
	 * like 'value%'
	 */
	public O endsWith(String value) {
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.ENDS_WITH.getExpression(), value);
		return end();
	}

	public O endsWith(String value, Predicate<String> predicate) {
		if (predicate.test(value)) {
			endsWith(value);
		}
		return end();
	}

	public O endsWith(String value, boolean predicate) {
		if (predicate) {
			endsWith(value);
		}
		return end();
	}


	public O notLike(String value) {
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.NOT_LIKE.getExpression(), value);
		return end();
	}

	public O notLike(String value, Predicate<String> predicate) {
		if (predicate.test(value)) {
			notLike(value);
		}
		return end();
	}

	public O notLike(String value, boolean predicate) {
		if (predicate) {
			notLike(value);
		}
		return end();
	}

	public O notContains(String value) {
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.NOT_CONTAINS.getExpression(), value);
		return end();
	}

	public O notContains(String value, Predicate<String> predicate) {
		if (predicate.test(value)) {
			notContains(value);
		}
		return end();
	}

	public O notContains(String value, boolean predicate) {
		if (predicate) {
			notContains(value);
		}
		return end();
	}

	public O notStartsWith(String value) {
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.NOT_STARTS_WITH.getExpression(), value);
		return end();
	}

	public O notStartsWith(String value, Predicate<String> predicate) {
		if (predicate.test(value)) {
			notStartsWith(value);
		}
		return end();
	}

	public O notStartsWith(String value, boolean predicate) {
		if (predicate) {
			notStartsWith(value);
		}
		return end();
	}

	public O notEndsWith(String value) {
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.NOT_ENDS_WITH.getExpression(), value);
		return end();
	}

	public O notEndsWith(String value, Predicate<String> predicate) {
		if (predicate.test(value)) {
			notEndsWith(value);
		}
		return end();
	}

	public O notEndsWith(String value, boolean predicate) {
		if (predicate) {
			notEndsWith(value);
		}
		return end();
	}


	public O exists(String rawSql) {
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.EXISTS.getExpression(), rawSql);
		return end();
	}

	public O exists(String rawSql, Predicate<String> predicate) {
		if (predicate.test(rawSql)) {
			exists(rawSql);
		}
		return end();
	}

	public O exists(String rawSql, boolean predicate) {
		if (predicate) {
			exists(rawSql);
		}
		return end();
	}

	public O notExists(String rawSql) {
		this.expression = new ExpressionSegment<>(this.expression, LogicalExpression.NOT_EXISTS.getExpression(), rawSql);
		return end();
	}

	public O notExists(String rawSql, Predicate<String> predicate) {
		if (predicate.test(rawSql)) {
			notExists(rawSql);
		}
		return end();
	}

	public O notExists(String rawSql, boolean predicate) {
		if (predicate) {
			notExists(rawSql);
		}
		return end();
	}


	public O eq(TableField tableField) {
		apply(MultiColumnLogicalExpression.EQ.getExpression(), tableField);
		return end();
	}

	public O eq(TableField tableField, Predicate<TableField> predicate) {
		if (predicate.test(tableField)) {
			eq(tableField);
		}
		return end();
	}

	public O eq(TableField tableField, boolean predicate) {
		if (predicate) {
			eq(tableField);
		}
		return end();
	}

	public O ne(TableField tableField) {
		apply(MultiColumnLogicalExpression.NE.getExpression(), tableField);
		return end();
	}

	public O ne(TableField tableField, Predicate<TableField> predicate) {
		if (predicate.test(tableField)) {
			ne(tableField);
		}
		return end();
	}

	public O ne(TableField tableField, boolean predicate) {
		if (predicate) {
			ne(tableField);
		}
		return end();
	}


	public O gt(TableField tableField) {
		apply(MultiColumnLogicalExpression.GT.getExpression(), tableField);
		return end();
	}

	public O gt(TableField tableField, Predicate<TableField> predicate) {
		if (predicate.test(tableField)) {
			gt(tableField);
		}
		return end();
	}

	public O gt(TableField tableField, boolean predicate) {
		if (predicate) {
			gt(tableField);
		}
		return end();
	}

	public O ge(TableField tableField) {
		apply(MultiColumnLogicalExpression.GE.getExpression(), tableField);
		return end();
	}

	public O ge(TableField tableField, Predicate<TableField> predicate) {
		if (predicate.test(tableField)) {
			ge(tableField);
		}
		return end();
	}

	public O ge(TableField tableField, boolean predicate) {
		if (predicate) {
			ge(tableField);
		}
		return end();
	}

	public O lt(TableField tableField) {
		apply(MultiColumnLogicalExpression.LT.getExpression(), tableField);
		return end();
	}

	public O lt(TableField tableField, Predicate<TableField> predicate) {
		if (predicate.test(tableField)) {
			lt(tableField);
		}
		return end();
	}

	public O lt(TableField tableField, boolean predicate) {
		if (predicate) {
			lt(tableField);
		}
		return end();
	}


	public O le(TableField tableField) {
		apply(MultiColumnLogicalExpression.LE.getExpression(), tableField);
		return end();
	}

	public O le(TableField tableField, Predicate<TableField> predicate) {
		if (predicate.test(tableField)) {
			le(tableField);
		}
		return end();
	}

	public O le(TableField tableField, boolean predicate) {
		if (predicate) {
			le(tableField);
		}
		return end();
	}

	public O between(TableField tableField1, TableField tableField2) {
		apply(MultiColumnLogicalExpression.BETWEEN.getExpression(),
			tableField1, tableField2);
		return end();
	}

	public O notBetween(TableField tableField1, TableField tableField2) {
		apply(MultiColumnLogicalExpression.NOT_BETWEEN.getExpression(),
			tableField1, tableField2);
		return end();
	}

}
