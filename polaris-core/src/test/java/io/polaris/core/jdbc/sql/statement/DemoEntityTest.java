package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.io.Consoles;
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

		Object[] args1 = new Object[]{select.toSqlNode().asBoundSql()};
		Consoles.println(args1);
		Object[] args = new Object[]{select.toSqlNode().asPreparedSql()};
		Consoles.println(args);
	}

	@Test
	void test02() {
		DemoEntity entity = DemoEntity.builder().id(1L).version(0L)
			.name("demo")
			.col1(1)
			.fieldStr1("f1").fieldStr2("f2")
			.col6(6)
			.build();

		Consoles.println();
		Consoles.println("select>");
		//			.column(DemoEntityMeta.FieldName.col6).notNull()
		//			.groupBy().col6()
		//			.end()
		Object[] args10 = new Object[]{DemoEntitySql.select()
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
			.toSqlNode().asBoundSql()};
		Consoles.println(args10);

		Consoles.println();
		Consoles.println("merge>");
		Object[] args9 = new Object[]{new MergeStatement<>(DemoEntity.class, "T").withEntity(entity)
			.toSqlNode().asBoundSql()};
		Consoles.println(args9);


		Consoles.println();
		Consoles.println("insert>");
		Object[] args8 = new Object[]{DemoEntitySql.insert()
			.withEntity(entity)
			.toSqlNode().asBoundSql()};
		Consoles.println(args8);

		Consoles.println();
		Consoles.println("insert-onDuplicateKeyUpdate>");
		Object[] args7 = new Object[]{DemoEntitySql.insert()
			.withEntity(entity)
			.enableUpdateByDuplicateKey(true)
			.toSqlNode().asBoundSql()};
		Consoles.println(args7);

		Consoles.println();
		Consoles.println("insert-replace>");
		Object[] args6 = new Object[]{DemoEntitySql.insert()
			.withEntity(entity)
			.enableReplace(true)
			.toSqlNode().asBoundSql()};
		Consoles.println(args6);

		Consoles.println();
		Consoles.println("update-byId>");
		Object[] args5 = new Object[]{DemoEntitySql.update()
			.withEntity(entity)
			.where().byEntityId(entity)
			.end()
			.toSqlNode().asBoundSql()};
		Consoles.println(args5);

		Consoles.println();
		Consoles.println("update-byIdAndVersion>");
		Object[] args4 = new Object[]{DemoEntitySql.update()
			.withEntity(entity)
			.where().byEntityIdAndVersion(entity)
			.end()
			.toSqlNode().asBoundSql()};
		Consoles.println(args4);

		Consoles.println();
		Consoles.println("update-byAny>");
		Object[] args3 = new Object[]{DemoEntitySql.update()
			.withEntity(entity)
			.where().byEntity(entity)
			.end()
			.toSqlNode().asBoundSql()};
		Consoles.println(args3);

		Consoles.println();
		Consoles.println("delete-byId>");
		Object[] args2 = new Object[]{DemoEntitySql.delete()
			.where().byEntityId(entity)
			.end()
			.toSqlNode().asBoundSql()};
		Consoles.println(args2);

		Consoles.println();
		Consoles.println("delete-byIdAndVersion>");
		Object[] args1 = new Object[]{DemoEntitySql.delete()
			.where().byEntityIdAndVersion(entity)
			.end()
			.toSqlNode().asBoundSql()};
		Consoles.println(args1);

		Consoles.println();
		Consoles.println("delete-byAny>");
		Object[] args = new Object[]{DemoEntitySql.delete()
			.where().byEntity(entity)
			.end()
			.toSqlNode().asBoundSql()};
		Consoles.println(args);
	}

}
