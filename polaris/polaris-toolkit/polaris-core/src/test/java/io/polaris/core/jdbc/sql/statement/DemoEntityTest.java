package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.jdbc.sql.statement.segment.TableField;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since 1.8,  Aug 24, 2023
 */
public class DemoEntityTest {

	@Test
	void test01() {
		DemoEntitySql.Select select = DemoEntitySql.select("x");
		select.distinct()
			.select().id().end()
			.select().name().end()
			.col1().col2().col3("x_col3")
			.join(DemoEntitySql.Join.builder()).left().alias("t2")
			.on().id().eq(TableField.of("x", DemoEntityMeta.FieldName.id)).end()
			.where().name().like("123")
			.score().le(100, (v) -> true)
			.end()
			.select("*")
			.end()
			.where()
			.id().eq(1);

		System.out.println(select.toSqlNode().asBoundSql());
		System.out.println(select.toSqlNode().asPreparedSql());
	}

	@Test
	void test02() {
		DemoEntity entity = DemoEntity.builder().id(1L).name("demo").col1(1).fieldStr1("f1").fieldStr2("f2").build();

		System.out.println();
		System.out.println("select>");
		System.out.println(DemoEntitySql.select()
			.quotaSelectAlias(true)
			.select().value(1, "a")
			.select().all().end()
			.where().byEntity(entity)
			.end()
			.toSqlNode().asBoundSql());

		System.out.println();
		System.out.println("merge>");
		System.out.println(new MergeStatement<>(DemoEntity.class, "T").withEntity(entity)
			.toSqlNode().asBoundSql());


		System.out.println();
		System.out.println("insert>");
		System.out.println(DemoEntitySql.insert()
			.withEntity(entity)
			.toSqlNode().asBoundSql());
		System.out.println();
		System.out.println("insert>");
		System.out.println(DemoEntitySql.insert()
			.withEntity(entity)
			.enableUpdateByDuplicateKey(true)
			.toSqlNode().asBoundSql());

		System.out.println();
		System.out.println("update>");
		System.out.println(DemoEntitySql.update()
			.withEntity(entity)
			.where().byEntityId(entity)
			.end()
			.toSqlNode().asBoundSql());

		System.out.println();
		System.out.println("delete>");
		System.out.println(DemoEntitySql.delete()
			.where().byEntityId(entity)
			.end()
			.toSqlNode().asBoundSql());
	}

}
