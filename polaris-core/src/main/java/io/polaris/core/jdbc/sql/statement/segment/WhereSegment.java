package io.polaris.core.jdbc.sql.statement.segment;


import io.polaris.core.annotation.AnnotationProcessing;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.statement.BaseSegment;
import io.polaris.core.jdbc.sql.statement.Segment;
import io.polaris.core.jdbc.sql.statement.SqlNodeBuilder;
import io.polaris.core.jdbc.sql.statement.Statements;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.reflect.GetterFunction;
import io.polaris.core.reflect.Reflects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author Qt
 * @since 1.8,  Aug 22, 2023
 */
@AnnotationProcessing
public class WhereSegment<O extends Segment<O>, S extends WhereSegment<O, S>> extends BaseSegment<S> implements SqlNodeBuilder {

	private final O owner;
	private final TableSegment<?> table;
	private final List<CriterionSegment<S, ?>> criteria = new ArrayList<>();
	private final TextNode delimiter;
	private final TableAccessible tableAccessible;

	public <T extends TableSegment<?>> WhereSegment(O owner, T table, TextNode delimiter) {
		this.owner = owner;
		this.table = table;
		this.delimiter = delimiter;
		this.tableAccessible = getTableAccessible();
	}

	private TableAccessible getTableAccessible() {
		if (owner instanceof WhereSegment) {
			return ((WhereSegment<?, ?>) owner).getTableAccessible();
		}
		if (owner instanceof TableAccessible) {
			return (TableAccessible) owner;
		}
		// 暂不支持复杂Update语句
		return null;
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
			containerNode.addNode(new TextNode("( "));
			containerNode.addNode(n);
			containerNode.addNode(new TextNode(" )"));
			if (!notEmpty) {
				notEmpty = true;
			}
		}
		return containerNode;
	}

	public S byEntity(Object entity) {
		TableMeta tableMeta = table.getTableMeta();
		if (tableMeta != null) {
			Statements.addWhereSqlByEntity(this, entity, tableMeta);
		}
		return getThis();
	}

	public S byEntity(Object entity, Predicate<String> includeWhereNulls) {
		TableMeta tableMeta = table.getTableMeta();
		if (tableMeta != null) {
			Statements.addWhereSqlByEntity(this, entity, tableMeta, includeWhereNulls);
		}
		return getThis();
	}

	public S byEntityId(Object entity) {
		TableMeta tableMeta = table.getTableMeta();
		if (tableMeta != null) {
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

	public S andRaw(String raw) {
		criteria.add(new CriterionSegment<>(getThis(), new TextNode(raw)));
		return getThis();
	}

	public S andSql(SqlNode sql) {
		criteria.add(new CriterionSegment<>(getThis(), sql));
		return getThis();
	}

	public CriterionSegment<S, ?> rawColumn(String rawColumn) {
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
