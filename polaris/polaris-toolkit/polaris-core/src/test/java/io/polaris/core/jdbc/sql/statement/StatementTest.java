package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.query.*;
import io.polaris.core.jdbc.sql.statement.any.AnySelectStatement;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class StatementTest {

	@Test
	void test04() {
		DeleteStatement<?> s = new DeleteStatement<>(DemoEntity.class);
		s.where().column("id").gt(1)
			.end();

		s.where(Queries.newCriteria(DemoEntity.builder()
			.name("%123%")
			.build()));

		System.out.printf("%s\n\n", s.toSqlNode().asPreparedSql());
		System.out.printf("%s\n\n", s.toSqlNode().asBoundSql());
	}

	@Test
	void test03() {
		InsertStatement<?> s = new InsertStatement<>(DemoEntity.class);
		s.column("id", 1);
		s.column("name", "test");
		s.column("score", "null");
		s.column("fieldStr1", "...");

		System.out.printf("%s\n\n", s.toSqlNode().asPreparedSql());
		System.out.printf("%s\n\n", s.toSqlNode().asBoundSql());
	}

	@Test
	void test02() {
		UpdateStatement<?> s = new UpdateStatement<>(DemoEntity.class);
		s.column("id", 1);
		s.column("name", "test");
		s.column("score", "null");
		s.column("fieldStr1", "...");
		s.where().column("id").gt(1)
			.end();


		s.where(Queries.newCriteria(DemoEntity.builder()
				.name("%123%")
			.build()));

		System.out.printf("%s\n\n", s.toSqlNode().asPreparedSql());
		System.out.printf("%s\n\n", s.toSqlNode().asBoundSql());
	}

	@Test
	void test01() {
		SelectStatement<?> statement = new SelectStatement<>(DemoEntity.class, "t")
			.select().end()
			.select("id", "id1")
			.select().column("id").apply("${ref}||'suffix'").alias("idx")
			.leftJoin(DemoEntity.class, "t1")
			.selectAll(true)
			.on().column("id").apply("${ref0} = ${ref1}", "t", "id").end()
			.column("score").apply("${ref0} > ${ref1}", "t", "score").end()
			.end()
			.end()
			.where()
			.column("col1").isNull()
			.column("col2").eq(2)
			.column("col3").in(Arrays.asList(1, 2, 3, 4))
			.or()
			.column("col4").ge(1)
			.column("col5").le(2)
			.end()
			.end()
			.groupBy().column("id")
			.end()
			.orderBy().column("id")
			.end();

		statement.where(
			Criteria.newCriteria()
				.relation(Relation.OR)
				.addSubset(
					Criteria.newCriteria().field("id")
						.criterion(Criterion.newCriterion()
							.addSubset(Criterion.newCriterion().operator(Operator.LE).value("321"))
							.addSubset(Criterion.newCriterion().operator(Operator.GT).value("123"))
						)
				)
				.addSubset(
					Criteria.newCriteria().field("name")
						.criterion(Criterion.newCriterion().operator(Operator.LIKE).value("xxx"))
				)
		);

		System.out.println("");
		System.out.println(statement.toSqlNode().asPreparedSql());
		System.out.println(statement.toSqlNode().asBoundSql());
		System.out.println();
		System.out.println(statement.toCountSqlNode().asBoundSql());
		System.out.println();
		System.out.println();
		System.out.println(AnySelectStatement.of(statement,"x").select().column("*").count().end().toSqlNode().asPreparedSql());
		System.out.println(AnySelectStatement.of(statement,"x").select().column("*").count().end().toSqlNode().asBoundSql());
	}


	@Test
	void testNode() {
		ContainerNode sql = new ContainerNode();
		for (int i = 0; i < 10; i++) {
			sql.addNode(new TextNode(" " + i + " "));
		}
		{
			ContainerNode sub = new ContainerNode();
			for (int i = 0; i < 10; i++) {
				sql.addNode(new TextNode(" " + i + " "));
			}
			sql.addNode(sub);
		}
		for (int i = 0; i < 10; i++) {
			sql.addNode(new TextNode(" " + i + " "));
		}

		System.out.println(sql.asBoundSql().toString());
		sql.visitSubsetWritable(op -> {
			if (op.getSqlNode().isTextNode()) {
				if (Integer.parseInt(op.getSqlNode().getText().trim()) % 3 == 0) {
					op.delete();
				}
			}
		});
		System.out.println(sql.asBoundSql().toString());

	}

	static class Base<T extends Base<T>> {

		T getThis() {
			return (T) this;
		}

		T then() {
			return getThis();
		}

		<X extends Base<X>> Base<X> aaa() {
			return null;
		}
	}

	static class Sub extends Base<Sub> {

		@Override
		Sub aaa() {
			return this;
		}

	}
}
