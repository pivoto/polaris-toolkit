package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.io.Consoles;
import io.polaris.core.jdbc.entity.DemoEntity;
import io.polaris.core.jdbc.entity.DemoEntityMeta;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since Oct 28, 2023
 */
public class InsertStatementTest {

	@Test
	void test03() {
		InsertStatement<?> s = new InsertStatement<>(DemoEntity.class);
		s.column(DemoEntityMeta.FieldName.id, 1);
		s.column(DemoEntityMeta.FieldName.name, "test");
		s.column(DemoEntityMeta.FieldName.score, "null");
		s.column(DemoEntityMeta.FieldName.fieldStr1, "...");
		Object[] args = new Object[]{s.toSqlNode().asBoundSql()};
		Consoles.println(args);
	}

	@Test
	void test01() {
		InsertStatement<?> s = new InsertStatement<>(DemoEntity.class);
		SelectStatement<?> select = SelectStatement.of(DemoEntity.class, "x");
		select
			.select(DemoEntityMeta.FieldName.id)
			.select(DemoEntityMeta.FieldName.name)
			.select(DemoEntityMeta.FieldName.score)
			.where().column(DemoEntityMeta.FieldName.id).eq(1)
			.end()
		;
		s.withSelect(select, DemoEntityMeta.FieldName.id, DemoEntityMeta.FieldName.name, DemoEntityMeta.FieldName.score);
		Object[] args = new Object[]{s.toSqlNode().asBoundSql()};
		Consoles.println(args);
	}

}
