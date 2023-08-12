package io.polaris.core.jdbc.sql.query;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import io.polaris.core.collection.Iterables;
import io.polaris.core.jdbc.sql.PreparedSql;
import io.polaris.core.jdbc.sql.node.SqlNode;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

class CriteriaParserTest {

	@Test
	void test01() {

		Criteria condition = new Criteria()
			.addSubset(new Criteria()
				.field("field01")
				.criterion(
					new Criterion()
						.operator(CriteriaOperator.EQ)
						.value("123")
				))
			.addSubset(new Criteria()
				.field("field02")
				.criterion(
					new Criterion()
						.operator(CriteriaOperator.IN)
						.value(new String[]{"1", "2", "3"})
				));

		System.out.println(JSON.toJSONString(condition, JSONWriter.Feature.PrettyFormat));
		SqlNode sqlNode = CriteriaParser.parse(condition, Function.identity());
		PreparedSql sql = sqlNode.asPreparedSql();
		System.out.println(sql.getText());
		System.out.println(sql.getBindings());
	}

	@Test
	public void test02() {
		Criteria condition = new Criteria()
			.relation(CriteriaRelation.AND)
			.addSubset(Iterables.asList(
				new Criteria().field("field01")
					.criterion(
						new Criterion()
							.relation(CriteriaRelation.OR)
							.addSubset(Iterables.asList(
								new Criterion()
									.operator(CriteriaOperator.EQ)
									.value("123")
								,
								new Criterion()
									.operator(CriteriaOperator.EQ)
									.value("234")
								,
								new Criterion()
									.relation(CriteriaRelation.AND)
									.addSubset(Iterables.asList(
										new Criterion()
											.operator(CriteriaOperator.GE)
											.value("111")
										,
										new Criterion()
											.operator(CriteriaOperator.LE)
											.value("222")
									))
							))
					)
				,
				new Criteria().field("field02").criterion(
					new Criterion()
						.operator(CriteriaOperator.EQ)
						.value("123")
				)
			));

		System.out.println(JSON.toJSONString(condition, JSONWriter.Feature.PrettyFormat));
		SqlNode sqlNode = CriteriaParser.parse(condition, Function.identity());
		PreparedSql sql = sqlNode.asPreparedSql();
		System.out.println(sql.getText());
		System.out.println(sql.getBindings());
	}

}
