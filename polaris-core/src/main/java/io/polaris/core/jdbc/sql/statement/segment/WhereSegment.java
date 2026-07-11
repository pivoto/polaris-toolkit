package io.polaris.core.jdbc.sql.statement.segment;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import io.polaris.core.annotation.AnnotationProcessing;
import io.polaris.core.collection.Iterables;
import io.polaris.core.collection.ObjectArrays;
import io.polaris.core.converter.Converters;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.ExpressionMeta;
import io.polaris.core.jdbc.SqlParamPredicate;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.sql.BindingValues;
import io.polaris.core.jdbc.sql.SqlTextParsers;
import io.polaris.core.jdbc.sql.VarRef;
import io.polaris.core.jdbc.sql.VarRefs;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.query.StringValue;
import io.polaris.core.jdbc.sql.query.ValueRange;
import io.polaris.core.jdbc.sql.statement.BaseSegment;
import io.polaris.core.jdbc.sql.statement.ColumnPredicate;
import io.polaris.core.jdbc.sql.statement.ConfigurableColumnPredicate;
import io.polaris.core.jdbc.sql.statement.Segment;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.core.jdbc.sql.statement.SqlNodeBuilder;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.map.Maps;
import io.polaris.core.reflect.GetterFunction;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.tuple.Ref;

/**
 * @author Qt
 * @since Aug 22, 2023
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
@AnnotationProcessing
public class WhereSegment<O extends Segment<O>, S extends WhereSegment<O, S>> extends BaseSegment<S> implements SqlNodeBuilder, TableAccessibleHolder {

	private final O owner;
	private final TableSegment<?> table;
	private final List<CriterionSegment<S, ?>> criteria = new ArrayList<>();
	private final TextNode delimiter;
	private final TableAccessible tableAccessible;

	public <T extends TableSegment<?>> WhereSegment(O owner, T table, TextNode delimiter) {
		this.owner = owner;
		this.table = table;
		this.delimiter = delimiter;
		this.tableAccessible = fetchTableAccessible();
	}

	private TableAccessible fetchTableAccessible() {
		// 嵌套对象下取值
		if (owner instanceof WhereSegment) {
			return ((WhereSegment<?, ?>) owner).fetchTableAccessible();
		}
		if (owner instanceof TableAccessible) {
			return (TableAccessible) owner;
		}
		if (owner instanceof TableAccessibleHolder) {
			return ((TableAccessibleHolder) owner).getTableAccessible();
		}
		return null;
	}

	@Override
	public TableAccessible getTableAccessible() {
		return tableAccessible;
	}


	public O end() {
		return owner;
	}

	@AnnotationProcessing
	protected void addCriterion(CriterionSegment<S, ?> criterion) {
		this.criteria.add(criterion);
	}

	@AnnotationProcessing
	public AndSegment<S, ?> and() {
		AndSegment<S, ?> x = new AndSegment<>(getThis(), this.table);
		addCriterion(new CriterionSegment<>(getThis(), x));
		return x;
	}

	@AnnotationProcessing
	public OrSegment<S, ?> or() {
		OrSegment<S, ?> x = new OrSegment<>(getThis(), table);
		addCriterion(new CriterionSegment<>(getThis(), x));
		return x;
	}


	@Override
	public SqlNode toSqlNode() {
		if (criteria.isEmpty()) {
			return SqlNodes.EMPTY;
		}
		ContainerNode containerNode = new ContainerNode();
		boolean notEmpty = false;
		for (CriterionSegment<S, ?> criterion : criteria) {
			SqlNode n = criterion.toSqlNode();
			if (n.isSkipped()) {
				continue;
			}
			if (notEmpty) {
				containerNode.addNode(delimiter);
			}
			containerNode.addNode(SqlNodes.LEFT_PARENTHESIS);
			containerNode.addNode(n);
			containerNode.addNode(SqlNodes.RIGHT_PARENTHESIS);
			if (!notEmpty) {
				notEmpty = true;
			}
		}
		return containerNode;
	}

	public S exceptLogicDeleted() {
		TableMeta tableMeta = table.getTableMeta();
		if (tableMeta != null) {
			tableMeta.getColumns().values().stream()
				.filter(ColumnMeta::isLogicDeleted)
				.forEach(meta -> {
					Object val = Converters.convertQuietly(meta.getFieldType(), false);
					this.column(meta.getFieldName()).eq(val);
				});
		}
		return getThis();
	}

	public S exceptLogicDeleted(boolean includeSelf, boolean includeJoins, boolean includeSubSelects) {
		if (includeSubSelects) {
			recursiveSubSelectsExceptLogicDeleted(includeSelf, includeJoins, includeSubSelects);
		}
		if (includeSelf) {
			exceptLogicDeleted();
		}
		return getThis();
	}

	void recursiveSubSelectsExceptLogicDeleted(boolean includeSelf, boolean includeJoins, boolean includeSubSelects) {
		for (CriterionSegment<S, ?> criterion : this.criteria) {
			criterion.recursiveSubSelectsExceptLogicDeleted(includeSelf, includeJoins, includeSubSelects);
		}
	}

	public S byEntity(Object entity) {
		return byEntity(entity, ColumnPredicate.DEFAULT);
	}

	public S byEntity(Object entity, Predicate<String> isIncludeEmptyColumns) {
		return byEntity(entity, ConfigurableColumnPredicate.of(isIncludeEmptyColumns));
	}

	public S byEntity(Object entity, Predicate<String> isIncludeColumns, Predicate<String> isExcludeColumns
		, Predicate<String> isIncludeEmptyColumns, boolean includeAllEmpty) {
		return byEntity(entity, ConfigurableColumnPredicate.of(
			isIncludeColumns, isExcludeColumns, isIncludeEmptyColumns, includeAllEmpty));
	}

	public S byEntity(Object entity, ColumnPredicate columnPredicate) {
		TableMeta tableMeta = table.getTableMeta();
		if (tableMeta != null) {
			@SuppressWarnings("unchecked")
			Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
				(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
					? Beans.newBeanMap(entity, tableMeta.getEntityClass())
					: Beans.newBeanMap(entity));

			for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
				String name = entry.getKey();
				// 不在包含列表
				if (!columnPredicate.isIncludedColumn(name)) {
					continue;
				}
				ColumnMeta meta = entry.getValue();
				Object val = entityMap.get(name);
				addWhereSqlByColumnValue(meta.getFieldName(), meta.getFieldType(), VarRefs.wrap(meta, val), columnPredicate);
			}
			for (Map.Entry<String, ExpressionMeta> entry : tableMeta.getExpressions().entrySet()) {
				String name = entry.getKey();
				// 不在包含列表
				if (!columnPredicate.isIncludedColumn(name)) {
					continue;
				}
				ExpressionMeta meta = entry.getValue();
				Object val = entityMap.get(name);
				addWhereSqlByColumnValue(meta.getFieldName(), meta.getFieldType(), VarRefs.wrap(meta, val), columnPredicate);
			}
		}
		return getThis();
	}

	private void addWhereSqlByColumnValue(String fieldName, Class<?> fieldType, VarRef<?> varRef, ColumnPredicate columnPredicate) {
		Object val = varRef.getValue();
		Map<String, String> props = varRef.getPropsIfNotEmpty();

		// 先进行空值判定，处理空值情况
		{
			// 解构包装对象
			boolean include = false;
			if (val instanceof Ref) {
				include = true;
				val = ((Ref<?>) val).get();
			}
			SqlParamPredicate.Result result = SqlParamPredicate.check(val);
			// 处理空值情况
			if (result != SqlParamPredicate.Result.NONEMPTY) {
				// 需要包含空值字段
				if (include || columnPredicate.isIncludedEmptyColumn(fieldName)
					|| (result == SqlParamPredicate.Result.EMPTY && SqlParamPredicate.whereIncludeEmpty())) {
					if (result == SqlParamPredicate.Result.EMPTY && !SqlParamPredicate.whereEmptyAsNull()) {
						if (props != null) {
							this.column(fieldName).eq(BindingValues.convert(fieldType, val, props));
						} else {
							this.column(fieldName).eq(BindingValues.convert(fieldType, val));
						}
					} else {
						this.column(fieldName).isNull();
					}
				}
				// 完成条件绑定
				return;
			}
		}

		// 以下为非空值情况
		assert val != null;

		// 日期字段
		if (Date.class.isAssignableFrom(fieldType)) {
			Date[] range = BindingValues.getDateRangeOrNull(val);
			if (range != null) {
				// 两个元素的日期类字段特殊处理，认为是日期范围条件
				if (range[0] != null) {
					if (props != null) {
						this.column(fieldName).ge(VarRef.of(range[0], props));
					} else {
						this.column(fieldName).ge(range[0]);
					}
				}
				if (range[1] != null) {
					if (props != null) {
						this.column(fieldName).le(VarRef.of(range[1], props));
					} else {
						this.column(fieldName).le(range[1]);
					}
				}
				// 完成条件绑定
				return;
			}
		}
		// 文本字段
		else if (String.class == fieldType) {
			if (val instanceof StringValue) {
				boolean like = ((StringValue) val).isLike();
				char escape = ((StringValue) val).getEscape();
				String str = ((StringValue) val).getValue();
				if (str == null) {
					this.column(fieldName).isNull();
				} else if (str.isEmpty() && SqlParamPredicate.whereEmptyAsNull()) {
					this.column(fieldName).isNull();
				} else {
					if (like) {
						// 判断escape是否是可见ascii字符
						if (escape > 0 && escape < 128) {
							// like 不需要VarRef附加属性
							this.column(fieldName).like(str, escape);
						} else {
							// like 不需要VarRef附加属性
							this.column(fieldName).like(str);
						}
					} else {
						if (props != null) {
							this.column(fieldName).eq(VarRef.of(str, props));
						} else {
							this.column(fieldName).eq(str);
						}
					}
				}
				// 完成条件绑定
				return;
			}
			if (val instanceof String && (((String) val).startsWith("%") || ((String) val).endsWith("%"))) {
				// like 不需要VarRef附加属性
				this.column(fieldName).like((String) val);
				// 完成条件绑定
				return;
			}
		}

		// 尚未绑定条件，考虑其他情况
		if (val instanceof ValueRange) {
			ValueRange<?> range = (ValueRange<?>) val;
			{
				Object start = range.getStart();
				SqlParamPredicate.Result result = SqlParamPredicate.check(start);
				if (result == SqlParamPredicate.Result.NONEMPTY
					|| (result == SqlParamPredicate.Result.EMPTY && !SqlParamPredicate.whereEmptyAsNull())) {
					start = Converters.convertQuietly(fieldType, start);
					if (start != null) {
						if (props != null) {
							this.column(fieldName).ge(VarRef.of(start, props));
						} else {
							this.column(fieldName).ge((start));
						}
					}
				}
			}
			{
				Object end = range.getEnd();
				SqlParamPredicate.Result result = SqlParamPredicate.check(end);
				if (result == SqlParamPredicate.Result.NONEMPTY
					|| (result == SqlParamPredicate.Result.EMPTY && !SqlParamPredicate.whereEmptyAsNull())) {
					end = Converters.convertQuietly(fieldType, end);
					if (end != null) {
						if (props != null) {
							this.column(fieldName).le(VarRef.of(end, props));
						} else {
							this.column(fieldName).le(end);
						}
					}
				}
			}
		} else if (val instanceof Collection) {
			List<Object> list = new ArrayList<>((Collection<?>) val);
			if (props != null) {
				this.column(fieldName).in(convertListElements(list, o -> BindingValues.convert(fieldType, o, props)));
			} else {
				this.column(fieldName).in(convertListElements(list, o -> BindingValues.convert(fieldType, o)));
			}
		} else if (val instanceof Iterable) {
			@SuppressWarnings("unchecked")
			List<Object> list = Iterables.asCollection(ArrayList::new, (Iterable<Object>) val);
			if (props != null) {
				this.column(fieldName).in(convertListElements(list, o -> BindingValues.convert(fieldType, o, props)));
			} else {
				this.column(fieldName).in(convertListElements(list, o -> BindingValues.convert(fieldType, o)));
			}
		} else if (val instanceof Iterator) {
			@SuppressWarnings("unchecked")
			List<Object> list = Iterables.asCollection(ArrayList::new, (Iterator<Object>) val);
			if (props != null) {
				this.column(fieldName).in(convertListElements(list, o -> BindingValues.convert(fieldType, o, props)));
			} else {
				this.column(fieldName).in(convertListElements(list, o -> BindingValues.convert(fieldType, o)));
			}
		} else if (val.getClass().isArray()) {
			List<Object> list = ObjectArrays.toList(val);
			if (props != null) {
				this.column(fieldName).in(convertListElements(list, o -> BindingValues.convert(fieldType, o, props)));
			} else {
				this.column(fieldName).in(convertListElements(list, o -> BindingValues.convert(fieldType, o)));
			}
		} else {
			if (props != null) {
				this.column(fieldName).eq(BindingValues.convert(fieldType, val, props));
			} else {
				this.column(fieldName).eq(BindingValues.convert(fieldType, val));
			}
		}
	}

	private List<Object> convertListElements(List<Object> list, Function<Object, Object> converter) {
		int size = list.size();
		for (int i = 0; i < size; i++) {
			Object o = list.get(i);
			list.set(i, converter.apply(o));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public S byEntityId(Object entity) {
		TableMeta tableMeta = table.getTableMeta();
		if (tableMeta != null) {
			Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
				(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
					? Beans.newBeanMap(entity, tableMeta.getEntityClass())
					: Beans.newBeanMap(entity));
			for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
				String name = entry.getKey();
				ColumnMeta meta = entry.getValue();
				if (meta.isPrimaryKey()) {
					Object val = entityMap.get(name);
					Map<String, String> props = null;
					if (val instanceof VarRef) {
						props = ((VarRef<?>) val).getProps();
						val = ((VarRef<?>) val).getValue();
					}
					val = Converters.convertQuietly(meta.getFieldType(), val);
					if (val == null) {
						this.column(name).isNull();
					} else {
						if (Maps.isNotEmpty(props)) {
							this.column(name).eq(VarRef.of(val, props));
						} else if (Maps.isNotEmpty(meta.getProps())) {
							this.column(name).eq(VarRefs.wrap(meta, val));
						} else {
							this.column(name).eq(val);
						}
					}
				}
			}
		}
		return getThis();
	}

	@SuppressWarnings("unchecked")
	public S byEntityIdAndVersion(Object entity) {
		TableMeta tableMeta = table.getTableMeta();
		if (tableMeta != null) {
			Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
				(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
					? Beans.newBeanMap(entity, tableMeta.getEntityClass())
					: Beans.newBeanMap(entity));
			for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
				String name = entry.getKey();
				ColumnMeta meta = entry.getValue();
				if (meta.isPrimaryKey() || meta.isVersion()) {
					Object val = entityMap.get(name);
					Map<String, String> props = null;
					if (val instanceof VarRef) {
						props = ((VarRef<?>) val).getProps();
						val = ((VarRef<?>) val).getValue();
					}
					val = Converters.convertQuietly(meta.getFieldType(), val);
					if (val == null) {
						this.column(name).isNull();
					} else {
						if (Maps.isNotEmpty(props)) {
							this.column(name).eq(VarRef.of(val, props));
						} else if (Maps.isNotEmpty(meta.getProps())) {
							this.column(name).eq(VarRefs.wrap(meta, val));
						} else {
							this.column(name).eq(val);
						}
					}
				}
			}
		}
		return getThis();
	}

	/**
	 * @param raw 原生SQL条件，支持表达式`%{tableAlias.tableField}`解析
	 * @return 当前条件对象
	 */
	public S raw(String raw) {
		// 解析表字段名
		raw = SqlTextParsers.resolveTableRef(raw, tableAccessible);
		criteria.add(new CriterionSegment<>(getThis(), new TextNode(raw)));
		return getThis();
	}

	public S sql(SqlNode sql) {
		criteria.add(new CriterionSegment<>(getThis(), sql));
		return getThis();
	}

	public <I extends SelectStatement<?>> S exists(I subSelect) {
		this.column("").exists(subSelect);
		return getThis();
	}

	public <I extends SelectStatement<?>> S exists(I subSelect, Consumer<I> append) {
		this.column("").exists(subSelect, append);
		return getThis();
	}

	public <I extends SelectStatement<?>> S notExists(I subSelect) {
		this.column("").notExists(subSelect);
		return getThis();
	}


	public <I extends SelectStatement<?>> S notExists(I subSelect, Consumer<I> append) {
		this.column("").notExists(subSelect, append);
		return getThis();
	}

	/**
	 * @param rawColumn 原生SQL字段名，支持表达式`%{tableAlias.tableField}`解析
	 * @return 字段条件对象
	 */
	public CriterionSegment<S, ?> rawColumn(String rawColumn) {
		// 解析表字段名
		rawColumn = SqlTextParsers.resolveTableRef(rawColumn, tableAccessible);
		CriterionSegment<S, ?> c = new CriterionSegment<>(getThis(), rawColumn);
		criteria.add(c);
		return c;
	}


	public <T, R> CriterionSegment<S, ?> column(GetterFunction<T, R> getter) {
		return column(Reflects.getPropertyName(getter));
	}

	@AnnotationProcessing
	public CriterionSegment<S, ?> column(String field) {
		CriterionSegment<S, ?> c = new CriterionSegment<>(getThis(), tableAccessible, table, field);
		criteria.add(c);
		return c;
	}


	@AnnotationProcessing
	public TableSegment<?> getTable() {
		return table;
	}
}
