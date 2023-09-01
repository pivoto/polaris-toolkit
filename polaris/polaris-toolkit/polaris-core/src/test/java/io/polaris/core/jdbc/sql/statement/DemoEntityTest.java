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
			.score().le(100,(v)->true)
			.end()
			.select("*")
			.end()
			.where()
			.id().eq(1);

		System.out.println(select.toSqlNode().asBoundSql());
		System.out.println(select.toSqlNode().asPreparedSql());
	}
}
