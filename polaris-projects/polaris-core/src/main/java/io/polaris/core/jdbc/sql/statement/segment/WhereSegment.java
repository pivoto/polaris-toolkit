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
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.sql.BindingValues;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.statement.BaseSegment;
import io.polaris.core.jdbc.sql.statement.ColumnPredicate;
import io.polaris.core.jdbc.sql.statement.ConfigurableColumnPredicate;
import io.polaris.core.jdbc.sql.statement.Segment;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.core.jdbc.sql.statement.SqlNodeBuilder;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.reflect.GetterFunction;
import io.polaris.core.reflect.Reflects;

import static io.polaris.core.lang.Objs.isNotEmpty;

/**
 * @author Qt
 * @since 1.8,  Aug 22, 2023
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
		// 暂不支持复杂Update语句
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
				addWhereSqlByColumnValue(meta, val, columnPredicate);
			}
		}
		return getThis();
	}

	private void addWhereSqlByColumnValue(ColumnMeta meta, Object val
		, ColumnPredicate columnPredicate) {
		if (isNotEmpty(val)) {
			Class<?> fieldType = meta.getFieldType();
			// 日期字段
			if (Date.class.isAssignableFrom(fieldType)) {
				Date[] range = BindingValues.getDateRangeOrNull(val);
				if (range != null) {
					if (range[0] != null) {
						this.column(meta.getFieldName()).ge(range[0]);
					}
					if (range[1] != null) {
						this.column(meta.getFieldName()).le(range[1]);
					}
					// 完成条件绑定
					return;
				}
			}
			// 文本字段
			else if (String.class.isAssignableFrom(fieldType)) {
				if (val instanceof String && (((String) val).startsWith("%") || ((String) val).endsWith("%"))) {
					this.column(meta.getFieldName()).like((String) val);
					// 完成条件绑定
					return;
				}
			}
			if (val instanceof Collection) {
				List<Object> list = new ArrayList<>((Collection<?>) val);
				this.column(meta.getFieldName()).in(convertListElements(list, o -> Converters.convertQuietly(fieldType, o)));
			} else if (val instanceof Iterable) {
				@SuppressWarnings("unchecked")
				List<Object> list = Iterables.asCollection(ArrayList::new, (Iterable<Object>) val);
				this.column(meta.getFieldName()).in(convertListElements(list, o -> Converters.convertQuietly(fieldType, o)));
			} else if (val instanceof Iterator) {
				@SuppressWarnings("unchecked")
				List<Object> list = Iterables.asCollection(ArrayList::new, (Iterator<Object>) val);
				this.column(meta.getFieldName()).in(convertListElements(list, o -> Converters.convertQuietly(fieldType, o)));
			} else if (val.getClass().isArray()) {
				List<Object> list = ObjectArrays.toList(val);
				this.column(meta.getFieldName()).in(convertListElements(list, o -> Converters.convertQuietly(fieldType, o)));
			} else {
				this.column(meta.getFieldName()).eq((Object) Converters.convertQuietly(fieldType, val));
			}
		} else {
			// 需要包含空值字段
			if (columnPredicate.isIncludedEmptyColumn(meta.getFieldName())) {
				this.column(meta.getFieldName()).isNull();
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

	public S byEntityId(Object entity) {
		TableMeta tableMeta = table.getTableMeta();
		if (tableMeta != null) {
			@SuppressWarnings("unchecked")
			Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, tableMeta.getEntityClass());
			for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
				String name = entry.getKey();
				ColumnMeta meta = entry.getValue();
				if (meta.isPrimaryKey() || meta.isVersion()) {
					Object val = entityMap.get(name);
					if (val == null) {
						this.column(name).isNull();
					} else {
						this.column(name).eq(val);
					}
				}
			}
		}
		return getThis();
	}

	/**
	 * @param raw 原生SQL条件，支持表达式`&{tableAlias.tableField}`解析
	 * @return 当前条件对象
	 */
	public S raw(String raw) {
		if (tableAccessible != null) {
			// 解析表字段名
			raw = tableAccessible.resolveRefTableField(raw);
		}
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
	 * @param rawColumn 原生SQL字段名，支持表达式`&{tableAlias.tableField}`解析
	 * @return 字段条件对象
	 */
	public CriterionSegment<S, ?> rawColumn(String rawColumn) {
		if (tableAccessible != null) {
			// 解析表字段名
			rawColumn = tableAccessible.resolveRefTableField(rawColumn);
		}
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
