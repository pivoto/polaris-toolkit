package io.polaris.core.jdbc.sql.query;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;

import io.polaris.core.TestConsole;
import io.polaris.core.collection.Iterables;
import io.polaris.core.jdbc.sql.PreparedSql;
import io.polaris.core.jdbc.sql.consts.Operator;
import io.polaris.core.jdbc.sql.consts.Relation;
import io.polaris.core.jdbc.sql.node.SqlNode;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

class QueriesTest {

	@Test
	void test01() {

		Criteria condition = new Criteria()
			.addSubset(new Criteria()
				.field("field01")
				.criterion(
					new Criterion()
						.operator(Operator.EQ)
						.value("123")
				))
			.addSubset(new Criteria()
				.field("field02")
				.criterion(
					new Criterion()
						.operator(Operator.IN)
						.value(new String[]{"1", "2", "3"})
				));

		TestConsole.println(JSON.toJSONString(condition, JSONWriter.Feature.PrettyFormat));
		SqlNode sqlNode = Queries.parse(condition, true, Function.identity());
		PreparedSql sql = sqlNode.asPreparedSql();
		TestConsole.println(sql.getText());
		TestConsole.println(sql.getBindings());
	}

	@Test
	public void test02() {
		Criteria condition = new Criteria()
			.relation(Relation.AND)
			.addSubset(Iterables.asList(
				new Criteria().field("field01")
					.criterion(
						new Criterion()
							.relation(Relation.OR)
							.addSubset(Iterables.asList(
								new Criterion()
									.operator(Operator.EQ)
									.value("123")
								,
								new Criterion()
									.operator(Operator.EQ)
									.value("234")
								,
								new Criterion()
									.relation(Relation.AND)
									.addSubset(Iterables.asList(
										new Criterion()
											.operator(Operator.GE)
											.value("111")
										,
										new Criterion()
											.operator(Operator.LE)
											.value("222")
									))
							))
					)
				,
				new Criteria().field("field02").criterion(
					new Criterion()
						.operator(Operator.EQ)
						.value("123")
				)
			));

		TestConsole.println(JSON.toJSONString(condition, JSONWriter.Feature.PrettyFormat));
		SqlNode sqlNode = Queries.parse(condition, true, Function.identity());
		PreparedSql sql = sqlNode.asPreparedSql();
		TestConsole.println(sql.getText());
		TestConsole.println(sql.getBindings());

	}
}
