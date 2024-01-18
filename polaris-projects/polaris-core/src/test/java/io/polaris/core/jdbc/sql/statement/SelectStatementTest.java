package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.jdbc.sql.BoundSql;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.Criterion;
import io.polaris.core.jdbc.sql.query.Operator;
import io.polaris.core.jdbc.sql.query.Relation;
import io.polaris.core.jdbc.sql.statement.any.AnySelectStatement;
import io.polaris.core.jdbc.sql.statement.expression.Expressions;
import io.polaris.core.jdbc.sql.statement.segment.TableField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author Qt
 * @since 1.8,  Oct 28, 2023
 */
public class SelectStatementTest {

	@Nested
	@DisplayName("简单场景")
	public class Test01 {
		@Test
		@DisplayName("简单使用")
		void test01() {
			SelectStatement<?> sql = new SelectStatement<>(DemoEntity.class);
			sql.selectAll();
			sql.where().column(DemoEntity.Fields.name).eq(123);
			sql.orderBy().column(DemoEntity.Fields.score).desc();
			System.out.println(sql.toSqlNode().asBoundSql());
		}

		@Test
		@DisplayName("简单函数")
		void test02() {
			SelectStatement<?> sql = new SelectStatement<>(DemoEntity.class);
			sql.select().column(DemoEntity.Fields.name).alias("n")
				.select().column(DemoEntity.Fields.score).max().alias("m");
			sql.where().column(DemoEntity.Fields.name).gt(123);
			sql.groupBy().column(DemoEntity.Fields.name).end();
			sql.having().column(DemoEntity.Fields.score).max().gt(60);
			sql.orderBy().rawColumn("m").desc();
			System.out.println(sql.toSqlNode().asBoundSql());
		}

	}


	@Nested
	@DisplayName("复杂条件")
	public class Test02 {
		@Test
		@DisplayName("复杂条件1")
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

			System.out.println("查询：");
			System.out.println(statement.toSqlNode().asBoundSql());
			System.out.println("查总数：");
			System.out.println(statement.toCountSqlNode().asBoundSql());
			System.out.println(AnySelectStatement.of(statement,"x").select().column("*").count().end().toSqlNode().asBoundSql());
		}
	}


	@Nested
	@DisplayName("嵌套查询")
	public class Test03 {
		@Test
		@DisplayName("嵌套查询1")
		void test01() {
			SelectStatement<?> sql = new SelectStatement<>(DemoEntity.class, "t1");
			sql.select().column(DemoEntity.Fields.name).aliasPrefix("prefix_").aliasSuffix("_suffix");

			SelectStatement<?> sub = new SelectStatement<>(Demo2Entity.class, "t2");
			sub.select().column(Demo2Entity.Fields.name);
			sub.nested(sql);
			sub.where().column(Demo2Entity.Fields.name).eq(TableField.of("t1", Demo2Entity.Fields.name));

			BoundSql subSql = sub.toSqlNode().asBoundSql();
			sql.where().column("").apply(Expressions.pattern("\nexists ( " + subSql.getText() + " )\n"), subSql.getBindings());

			sql.where().exists(sub);
			sql.where().exists(Demo2EntitySql.select("t3"), x->{
				x.where().name().eq(TableField.of("t1", Demo2Entity.Fields.name))
					.col2().notNull();
			});
			sql.where().exists(Demo2EntitySql.select("t4"), x->{
				x.where().name().eq(TableField.of("t1", Demo2Entity.Fields.name))
					.col1().notNull();
			});

			sql.where().column(Demo2Entity.Fields.name).in(Demo2EntitySql.select("t4").name().where().col1().notNull().end());

			System.out.println(sql.toSqlNode().asBoundSql());
		}
	}
}
