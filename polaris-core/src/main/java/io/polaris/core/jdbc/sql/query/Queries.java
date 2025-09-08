package io.polaris.core.jdbc.sql.query;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import io.polaris.core.collection.Iterables;
import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.ExpressionMeta;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.TableMetaKit;
import io.polaris.core.jdbc.sql.consts.Direction;
import io.polaris.core.jdbc.sql.consts.Operator;
import io.polaris.core.jdbc.sql.consts.Relation;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.statement.segment.TableSegment;
import io.polaris.core.lang.Objs;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Aug 11, 2023
 */
public class Queries {

	public static Function<String, String> newColumnDiscovery(Class<?> entityClass) {
		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		return newColumnDiscovery(tableMeta);
	}

	public static Function<String, String> newColumnDiscovery(TableMeta tableMeta) {
		return field -> {
			String[] arr = field.split(Pattern.quote(SymbolConsts.DOT), 2);
			String alias = null;
			if (arr.length == 2) {
				alias = arr[0].trim();
				field = arr[1].trim();
			}
			if (alias != null) {
				if (Strings.equalsIgnoreCase(alias, tableMeta.getAlias())) {
					ColumnMeta columnMeta = tableMeta.getColumns().get(field);
					if (columnMeta != null) {
						return alias + SymbolConsts.DOT + columnMeta.getColumnName();
					}
					ExpressionMeta expressionMeta = tableMeta.getExpressions().get(field);
					if (expressionMeta != null) {
						return expressionMeta.getExpressionWithTableAlias(alias);
					}
				}
			} else {
				ColumnMeta columnMeta = tableMeta.getColumns().get(field);
				if (columnMeta != null) {
					return columnMeta.getColumnName();
				}
				ExpressionMeta expressionMeta = tableMeta.getExpressions().get(field);
				if (expressionMeta != null) {
					// 无别名时，使用表名，防止子查询中字段来源不明确
					return expressionMeta.getExpressionWithTableName();
				}
			}
			return null;
		};
	}

	public static Function<String, String> newColumnDiscovery(Class<?> entityClass, Consumer<ColumnMeta> visitor) {
		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		return newColumnDiscovery(tableMeta, visitor);
	}

	public static Function<String, String> newColumnDiscovery(TableMeta tableMeta, Consumer<ColumnMeta> visitor) {
		return field -> {
			String[] arr = field.split(Pattern.quote(SymbolConsts.DOT), 2);
			String alias = null;
			if (arr.length == 2) {
				alias = arr[0].trim();
				field = arr[1].trim();
			}
			if (alias != null) {
				if (Strings.equalsIgnoreCase(alias, tableMeta.getAlias())) {
					ColumnMeta columnMeta = tableMeta.getColumns().get(field);
					if (columnMeta != null) {
						visitor.accept(columnMeta);
						return alias + SymbolConsts.DOT + columnMeta.getColumnName();
					}
					ExpressionMeta expressionMeta = tableMeta.getExpressions().get(field);
					if (expressionMeta != null) {
						return expressionMeta.getExpressionWithTableAlias(alias);
					}
				}
			} else {
				ColumnMeta columnMeta = tableMeta.getColumns().get(field);
				if (columnMeta != null) {
					visitor.accept(columnMeta);
					return columnMeta.getColumnName();
				}
				ExpressionMeta expressionMeta = tableMeta.getExpressions().get(field);
				if (expressionMeta != null) {
					// 无别名时，使用表名，防止子查询中字段来源不明确
					return expressionMeta.getExpressionWithTableName();
				}
			}
			return null;
		};
	}

	public static Function<String, String> newColumnDiscovery(Class<?> entityClass, String alias) {
		return newColumnDiscovery(TableSegment.fromEntity(entityClass, alias));
	}

	public static Function<String, String> newColumnDiscovery(
		Class<?> entityClass1, String alias1,
		Class<?> entityClass2, String alias2
	) {
		return newColumnDiscovery(
			TableSegment.fromEntity(entityClass1, alias1),
			TableSegment.fromEntity(entityClass2, alias2)
		);
	}

	public static Function<String, String> newColumnDiscovery(
		Class<?> entityClass1, String alias1,
		Class<?> entityClass2, String alias2,
		Class<?> entityClass3, String alias3
	) {
		return newColumnDiscovery(
			TableSegment.fromEntity(entityClass1, alias1),
			TableSegment.fromEntity(entityClass2, alias2),
			TableSegment.fromEntity(entityClass3, alias3)
		);
	}

	public static Function<String, String> newColumnDiscovery(TableSegment<?>... tables) {
		return field -> {
			String[] arr = field.split(Pattern.quote(SymbolConsts.DOT), 2);
			String alias = null;
			if (arr.length == 2) {
				alias = arr[0].trim();
				field = arr[1].trim();
			}
			try {
				if (tables != null) {
					for (TableSegment<?> table : tables) {
						if (alias != null && !Strings.equalsIgnoreCase(alias, table.getTableAlias())) {
							// 不匹配表别名
							continue;
						}
						String col = table.getColumnExpression(field);
						if (Strings.isNotBlank(col)) {
							return col;
						}
					}
				}
			} catch (Exception e) {// 未找到对应的列，忽略此条件字段
			}
			return null;
		};
	}

	public static Criteria newCriteria(Object entity) {
		return newCriteria(entity, null);
	}

	@SuppressWarnings({"unchecked", "DuplicatedCode"})
	public static Criteria newCriteria(Object entity, Class<?> entityClass) {
		Criteria criteria = Criteria.newCriteria();
		if (entityClass == null) {
			entityClass = entity.getClass();
		}
		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, entityClass);
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			Object val = entityMap.get(name);
			if (Objs.isNotEmpty(val)) {
				if (val instanceof Iterable || val instanceof Iterator) {
					criteria.addSubset(Criteria.newCriteria().field(name)
						.criterion(Criterion.newCriterion().operator(Operator.IN).value(val)));
				} else if (val.getClass().isArray()) {
					criteria.addSubset(Criteria.newCriteria().field(name)
						.criterion(Criterion.newCriterion().operator(Operator.IN).value(val)));
				} else if (String.class == meta.getFieldType() && val instanceof String) {
					if (((String) val).startsWith("%") || ((String) val).endsWith("%")) {
						criteria.addSubset(Criteria.newCriteria().field(name)
							.criterion(Criterion.newCriterion().operator(Operator.LIKE).value(val)));
					} else {
						criteria.addSubset(Criteria.newCriteria().field(name)
							.criterion(Criterion.newCriterion().operator(Operator.EQ).value(val)));
					}
				} else {
					criteria.addSubset(Criteria.newCriteria().field(name)
						.criterion(Criterion.newCriterion().operator(Operator.EQ).value(val)));
				}
			}
		}
		for (Map.Entry<String, ExpressionMeta> entry : tableMeta.getExpressions().entrySet()) {
			String name = entry.getKey();
			ExpressionMeta meta = entry.getValue();
			Object val = entityMap.get(name);
			if (Objs.isNotEmpty(val)) {
				if (val instanceof Iterable || val instanceof Iterator) {
					criteria.addSubset(Criteria.newCriteria().field(name)
						.criterion(Criterion.newCriterion().operator(Operator.IN).value(val)));
				} else if (val.getClass().isArray()) {
					criteria.addSubset(Criteria.newCriteria().field(name)
						.criterion(Criterion.newCriterion().operator(Operator.IN).value(val)));
				} else if (String.class == meta.getFieldType() && val instanceof String) {
					if (((String) val).startsWith("%") || ((String) val).endsWith("%")) {
						criteria.addSubset(Criteria.newCriteria().field(name)
							.criterion(Criterion.newCriterion().operator(Operator.LIKE).value(val)));
					} else {
						criteria.addSubset(Criteria.newCriteria().field(name)
							.criterion(Criterion.newCriterion().operator(Operator.EQ).value(val)));
					}
				} else {
					criteria.addSubset(Criteria.newCriteria().field(name)
						.criterion(Criterion.newCriterion().operator(Operator.EQ).value(val)));
				}
			}
		}
		return criteria;
	}

	public static OrderBy newOrderBy(String orderBySql) {
		OrderBy orderBy = OrderBy.newOrderBy();
		if (Strings.isNotBlank(orderBySql)) {
			String[] items = orderBySql.split(",");
			for (String item : items) {
				String[] arr = item.trim().split("\\s+", 2);
				if (arr.length == 2) {
					orderBy.by(Direction.parse(arr[1]), arr[0]);
				} else {
					orderBy.by(arr[0]);
				}
			}
		}
		return orderBy;
	}

	public static SqlNode parse(OrderBy orderBy, Function<String, String> columnDiscovery) {
		SqlNode sql = new ContainerNode();
		boolean first = true;
		for (OrderBy.Item item : orderBy.getItems()) {
			String column = columnDiscovery.apply(item.getField());
			// 为空表示此字段不存在或需跳过忽略
			if (Strings.isNotBlank(column)) {
				if (first) {
					first = false;
				} else {
					sql.addNode(SqlNodes.COMMA);
				}
				sql.addNode(new TextNode(column));
				sql.addNode(SqlNodes.BLANK);
				sql.addNode(item.getDirection().getTextNode());
			}
		}
		return sql;
	}

	public static SqlNode parse(Criteria criteria) {
		return parse(criteria, false, Function.identity());
	}


	public static SqlNode parse(Criteria criteria, boolean supportReplacement) {
		return parse(criteria, supportReplacement, Function.identity());
	}


	public static SqlNode parse(Criteria criteria, Function<String, String> columnDiscovery) {
		return parse(criteria, false, columnDiscovery);
	}

	public static SqlNode parse(Criteria criteria, boolean supportReplacement, Function<String, String> columnDiscovery) {
		SqlNode sql = new ContainerNode();
		if (!Iterables.isEmpty(criteria.getSubset())) {
			boolean first = true;
			for (Criteria subset : criteria.getSubset()) {
				SqlNode sqlNode = parse(subset, supportReplacement, columnDiscovery);
				if (sqlNode.isSkipped()) {
					continue;
				}
				if (first) {
					first = false;
				} else {
					sql.addNode(getRelationOrDefault(criteria).getTextNode());
				}
				sql.addNode(SqlNodes.LEFT_PARENTHESIS);
				sql.addNodes(sqlNode.subset());
				sql.addNode(SqlNodes.RIGHT_PARENTHESIS);
			}
		} else if (Strings.isNotBlank(criteria.getField()) && criteria.getCriterion() != null) {
			Criterion criterion = criteria.getCriterion();
			String column = columnDiscovery.apply(criteria.getField());
			// 为空表示此字段不存在或需跳过忽略
			if (Strings.isNotBlank(column)) {
				SqlNode sqlNode = parse(column, criterion, supportReplacement);
				if (!sqlNode.isSkipped()) {
					sql.addNodes(sqlNode.subset());
				}
			}
		}
		return sql;
	}


	public static SqlNode parse(Criteria criteria, Consumer<String> columnVisitor) {
		return parse(criteria, false, Function.identity(), columnVisitor);
	}

	public static SqlNode parse(Criteria criteria, boolean supportReplacement, Consumer<String> columnVisitor) {
		return parse(criteria, supportReplacement, Function.identity(), columnVisitor);
	}


	public static SqlNode parse(Criteria criteria, Function<String, String> columnDiscovery, Consumer<String> columnVisitor) {
		return parse(criteria, false, columnDiscovery, columnVisitor);
	}

	public static SqlNode parse(Criteria criteria, boolean supportReplacement, Function<String, String> columnDiscovery, Consumer<String> columnVisitor) {
		SqlNode sql = new ContainerNode();
		if (!Iterables.isEmpty(criteria.getSubset())) {
			boolean first = true;
			for (Criteria subset : criteria.getSubset()) {
				SqlNode sqlNode = parse(subset, supportReplacement, columnDiscovery, columnVisitor);
				if (sqlNode.isSkipped()) {
					continue;
				}
				if (first) {
					first = false;
				} else {
					sql.addNode(getRelationOrDefault(criteria).getTextNode());
				}
				sql.addNode(SqlNodes.LEFT_PARENTHESIS);
				sql.addNodes(sqlNode.subset());
				sql.addNode(SqlNodes.RIGHT_PARENTHESIS);
			}
		} else if (Strings.isNotBlank(criteria.getField()) && criteria.getCriterion() != null) {
			Criterion criterion = criteria.getCriterion();
			String column = columnDiscovery.apply(criteria.getField());
			// 为空表示此字段不存在或需跳过忽略
			if (Strings.isNotBlank(column)) {
				SqlNode sqlNode = parse(column, criterion, supportReplacement);
				if (!sqlNode.isSkipped()) {
					columnVisitor.accept(column);
					sql.addNodes(sqlNode.subset());
				}
			}
		}
		return sql;
	}

	public static SqlNode parse(String column, Criterion criterion, boolean supportReplacement) {
		SqlNode sql = new ContainerNode();
		if (!Iterables.isEmpty(criterion.getSubset())) {
			boolean first = true;
			for (Criterion sub : criterion.getSubset()) {
				SqlNode sqlNode = parse(column, sub, supportReplacement);
				if (sqlNode.isSkipped()) {
					continue;
				}
				if (first) {
					first = false;
				} else {
					sql.addNode(getRelationOrDefault(criterion).getTextNode());
				}
				sql.addNode(SqlNodes.LEFT_PARENTHESIS);
				sql.addNodes(sqlNode.subset());
				sql.addNode(SqlNodes.RIGHT_PARENTHESIS);
			}
		} else if (criterion.getOperator() != null) {
			Operator symbol = criterion.getOperator();
			ContainerNode sqlNode = symbol.toSqlNode(column, "", criterion.getValue(),
				supportReplacement ? criterion.getReference() : null);
			sqlNode.skipIfMissingVarValue();
			if (!sqlNode.isSkipped()) {
				sql.addNodes(sqlNode.subset());
			}
		}
		return sql;
	}

	private static Relation getRelationOrDefault(Criteria criteria) {
		Relation relation = criteria.getRelation();
		if (relation == null) {
			relation = Relation.AND;
		}
		return relation;
	}

	private static Relation getRelationOrDefault(Criterion condition) {
		Relation relation = condition.getRelation();
		if (relation == null) {
			relation = Relation.AND;
		}
		return relation;
	}
}
