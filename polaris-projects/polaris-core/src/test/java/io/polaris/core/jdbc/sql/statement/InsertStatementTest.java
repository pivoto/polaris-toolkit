package io.polaris.core.jdbc.sql.statement;

import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since 1.8,  Oct 28, 2023
 */
public class InsertStatementTest {

	@Test
	void test03() {
		InsertStatement<?> s = new InsertStatement<>(DemoEntity.class);
		s.column(DemoEntityMeta.FieldName.id, 1);
		s.column(DemoEntityMeta.FieldName.name, "test");
		s.column(DemoEntityMeta.FieldName.score, "null");
		s.column(DemoEntityMeta.FieldName.fieldStr1, "...");
		System.out.printf("%s\n\n", s.toSqlNode().asBoundSql());
	}

}
