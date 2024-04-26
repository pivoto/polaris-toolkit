package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.TestConsole;
import io.polaris.core.jdbc.entity.DemoEntity;
import io.polaris.core.jdbc.sql.statement.segment.TableField;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Qt
 * @since 1.8,  Aug 24, 2023
 */
public class DemoEntityTest {

	@Test
	void test01() {
		List<Long> ids = new ArrayList<>(3000);
		for (int i = 0; i < 2100; i++) {
			ids.add(Long.valueOf(i));
		}
		DemoEntitySql.Select select = DemoEntitySql.select("x");
		select.distinct()
			.select().id().end()
			.select().name().end()
			.col1().col2().col3("x_col3")

			.join(DemoEntitySql.join()).left().alias("t2")
			.on().id().eq(TableField.of("x", DemoEntityMeta.FieldName.id))
			.end()
			.where().name().like("123")
			.score().le(100, (v) -> true)
			.end()
			.select("*")
			.end()

			.where()
			.id().in(ids)
			;

//		new SelectStatement<>(null).where()
//			.column("a").eq(1)
//			.end();

		TestConsole.println(select.toSqlNode().asBoundSql());
		TestConsole.println(select.toSqlNode().asPreparedSql());
	}

	@Test
	void test02() {
		DemoEntity entity = DemoEntity.builder().id(1L).name("demo").col1(1).fieldStr1("f1").fieldStr2("f2").build();

		TestConsole.println();
		TestConsole.println("select>");
		TestConsole.println(DemoEntitySql.select()
			.quotaSelectAlias(true)
			.select().value(1, "a")
			.select().all().end()
			.where().byEntity(entity)
			.end()
			.toSqlNode().asBoundSql());

		TestConsole.println();
		TestConsole.println("merge>");
		TestConsole.println(new MergeStatement<>(DemoEntity.class, "T").withEntity(entity)
			.toSqlNode().asBoundSql());


		TestConsole.println();
		TestConsole.println("insert>");
		TestConsole.println(DemoEntitySql.insert()
			.withEntity(entity)
			.toSqlNode().asBoundSql());
		TestConsole.println();
		TestConsole.println("insert>");
		TestConsole.println(DemoEntitySql.insert()
			.withEntity(entity)
			.enableUpdateByDuplicateKey(true)
			.toSqlNode().asBoundSql());

		TestConsole.println();
		TestConsole.println("update>");
		TestConsole.println(DemoEntitySql.update()
			.withEntity(entity)
			.where().byEntityId(entity)
			.end()
			.toSqlNode().asBoundSql());

		TestConsole.println();
		TestConsole.println("delete>");
		TestConsole.println(DemoEntitySql.delete()
			.where().byEntityId(entity)
			.end()
			.toSqlNode().asBoundSql());
	}

}
