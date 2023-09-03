package io.polaris.core.jdbc.sql.query;

import io.polaris.core.collection.Iterables;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.TableMetaKit;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.lang.Objs;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.string.Strings;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8,  Aug 11, 2023
 */
public class Queries {

	public static Criteria newCriteria(Object entity) {
		return newCriteria(entity, null);
	}

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

	public static SqlNode parse(String column, Criterion criterion, boolean supportReplacement) {
		SqlNode sql = new ContainerNode();
		if (!Iterables.isEmpty(criterion.getSubset())) {
			int i = 0;
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
