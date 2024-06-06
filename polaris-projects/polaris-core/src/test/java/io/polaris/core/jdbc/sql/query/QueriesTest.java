package io.polaris.core.jdbc.sql.query;

import java.util.function.Function;

import io.polaris.core.TestConsole;
import io.polaris.core.collection.Iterables;
import io.polaris.core.jdbc.entity.Demo1Entity;
import io.polaris.core.jdbc.entity.Demo2Entity;
import io.polaris.core.jdbc.sql.PreparedSql;
import io.polaris.core.jdbc.sql.consts.Direction;
import io.polaris.core.jdbc.sql.consts.Operator;
import io.polaris.core.jdbc.sql.consts.Relation;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.statement.segment.TableSegment;
import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;

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


	@Test
	void test03() {

		{
			OrderBy orderBy = OrderBy.newOrderBy()
				.by(Direction.ASC, Demo1Entity.Fields.fieldStr1)
				.by(Direction.DESC, Demo1Entity.Fields.fieldStr2);
			SqlNode sql = Queries.parse(orderBy, Queries.newColumnDiscovery(Demo1Entity.class));
			TestConsole.println(sql.toString());
		}
		{
			OrderBy orderBy = OrderBy.newOrderBy()
				.by(Direction.ASC, "t1." + Demo1Entity.Fields.fieldStr1)
				.by(Direction.DESC, "t2." + Demo2Entity.Fields.fieldStr2);
			SqlNode sql = Queries.parse(orderBy, Queries.newColumnDiscovery(Demo1Entity.class, "t1"));
			TestConsole.println(sql.toString());
		}
		{
			OrderBy orderBy = OrderBy.newOrderBy()
				.by(Direction.ASC, "t1." + Demo1Entity.Fields.fieldStr1)
				.by(Direction.DESC, "t2." + Demo2Entity.Fields.fieldStr2);
			SqlNode sql = Queries.parse(orderBy, Queries.newColumnDiscovery(
				TableSegment.fromEntity(Demo1Entity.class, "t1"),
				TableSegment.fromEntity(Demo2Entity.class, "t2")
			));
			TestConsole.println(sql.toString());
		}
	}


}
