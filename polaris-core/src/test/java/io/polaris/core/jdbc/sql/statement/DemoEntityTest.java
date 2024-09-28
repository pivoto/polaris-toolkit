package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.TestConsole;
import io.polaris.core.jdbc.entity.DemoEntity;
import io.polaris.core.jdbc.entity.DemoEntityMeta;
import io.polaris.core.jdbc.entity.DemoEntitySql;
import io.polaris.core.jdbc.sql.statement.segment.TableField;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Qt
 * @since  Aug 24, 2023
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
			.col6()
//			.select(DemoEntity.Fields.col6)

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
		DemoEntity entity = DemoEntity.builder().id(1L).version(0L)
			.name("demo")
			.col1(1)
			.fieldStr1("f1").fieldStr2("f2")
			.col6(6)
			.build();

		TestConsole.println();
		TestConsole.println("select>");
		TestConsole.println(DemoEntitySql.select()
			.quotaSelectAlias(true)
			.select().value(1, "a")
			.select().all().end()
			.where().byEntity(entity)
			.col6().notNull()
//			.column(DemoEntityMeta.FieldName.col6).notNull()
			.end()
			.orderBy()
			.col6().desc()
//			.groupBy().col6()
//			.end()
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
		TestConsole.println("insert-onDuplicateKeyUpdate>");
		TestConsole.println(DemoEntitySql.insert()
			.withEntity(entity)
			.enableUpdateByDuplicateKey(true)
			.toSqlNode().asBoundSql());

		TestConsole.println();
		TestConsole.println("insert-replace>");
		TestConsole.println(DemoEntitySql.insert()
			.withEntity(entity)
			.enableReplace(true)
			.toSqlNode().asBoundSql());

		TestConsole.println();
		TestConsole.println("update-byId>");
		TestConsole.println(DemoEntitySql.update()
			.withEntity(entity)
			.where().byEntityId(entity)
			.end()
			.toSqlNode().asBoundSql());

		TestConsole.println();
		TestConsole.println("update-byIdAndVersion>");
		TestConsole.println(DemoEntitySql.update()
			.withEntity(entity)
			.where().byEntityIdAndVersion(entity)
			.end()
			.toSqlNode().asBoundSql());

		TestConsole.println();
		TestConsole.println("update-byAny>");
		TestConsole.println(DemoEntitySql.update()
			.withEntity(entity)
			.where().byEntity(entity)
			.end()
			.toSqlNode().asBoundSql());

		TestConsole.println();
		TestConsole.println("delete-byId>");
		TestConsole.println(DemoEntitySql.delete()
			.where().byEntityId(entity)
			.end()
			.toSqlNode().asBoundSql());

		TestConsole.println();
		TestConsole.println("delete-byIdAndVersion>");
		TestConsole.println(DemoEntitySql.delete()
			.where().byEntityIdAndVersion(entity)
			.end()
			.toSqlNode().asBoundSql());

		TestConsole.println();
		TestConsole.println("delete-byAny>");
		TestConsole.println(DemoEntitySql.delete()
			.where().byEntity(entity)
			.end()
			.toSqlNode().asBoundSql());
	}

}
