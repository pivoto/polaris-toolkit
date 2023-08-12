package io.polaris.core.jdbc.sql.query;

import io.polaris.core.collection.Iterables;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.string.Strings;

import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8,  Aug 11, 2023
 */
public class CriteriaParser {


	public static SqlNode parse(Criteria criteria) {
		return parse(criteria, Function.identity());
	}


	public static SqlNode parse(Criteria criteria, Function<String, String> fieldConverter) {
		SqlNode sql = new ContainerNode();
		if (!Iterables.isEmpty(criteria.getSubset())) {
			int i = 0;
			for (Criteria subCondition : criteria.getSubset()) {
				if (i == 0) {
					sql.addNodes(parse(subCondition, fieldConverter).subset());
				} else {
					if (i == 1) {
						sql.addNode(0, new TextNode(" ( "));
						sql.addNode(new TextNode(" ) "));
					}
					sql.addNode(new TextNode(" " + getRelationOrDefault(criteria).getSqlText() + " ( "));
					sql.addNodes(parse(subCondition, fieldConverter).subset());
					sql.addNode(new TextNode(" ) "));
				}
				i++;
			}
		} else if (Strings.isNotBlank(criteria.getField()) && criteria.getCriterion() != null) {
			Criterion criterion = criteria.getCriterion();
			String field = criteria.getField();
			field = fieldConverter.apply(field);
			sql.addNodes(parse(field, criterion).subset());
		}
		return sql;
	}

	public static SqlNode parse(String field, Criterion criterion) {
		SqlNode sql = new ContainerNode();
		if (!Iterables.isEmpty(criterion.getSubset())) {
			int i = 0;
			for (Criterion subset : criterion.getSubset()) {
				if (i == 0) {
					sql.addNodes(parse(field, subset).subset());
				} else {
					if (i == 1) {
						sql.addNode(0, new TextNode(" ( "));
						sql.addNode(new TextNode(" ) "));
					}
					sql.addNode(new TextNode(" " + getRelationOrDefault(criterion).getSqlText() + " ( "));
					sql.addNodes(parse(field, subset).subset());
					sql.addNode(new TextNode(" ) "));
				}
				i++;
			}
		} else if (criterion.getOperator() != null) {
			CriteriaOperator symbol = criterion.getOperator();
			sql.addNodes(symbol.toSqlNode(field, "", criterion.getValue(), criterion.getReference()).subset());
		}
		return sql;
	}

	private static CriteriaRelation getRelationOrDefault(Criteria condition) {
		CriteriaRelation relation = condition.getRelation();
		if (relation == null) {
			relation = CriteriaRelation.AND;
		}
		return relation;
	}

	private static CriteriaRelation getRelationOrDefault(Criterion condition) {
		CriteriaRelation relation = condition.getRelation();
		if (relation == null) {
			relation = CriteriaRelation.AND;
		}
		return relation;
	}
}
