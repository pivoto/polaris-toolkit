package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.io.Consoles;
import io.polaris.core.jdbc.entity.DemoEntity;
import io.polaris.core.jdbc.entity.DemoEntityMeta;
import io.polaris.core.jdbc.sql.query.Queries;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since  Oct 28, 2023
 */
public class UpdateStatementTest {

	@Test
	void test02() {
		UpdateStatement<?> s = new UpdateStatement<>(DemoEntity.class);

		s.column(DemoEntityMeta.FieldName.id, 1);
		s.column(DemoEntityMeta.FieldName.name, "test");
		s.column(DemoEntityMeta.FieldName.score, "null");
		s.column(DemoEntityMeta.FieldName.fieldStr1, "...");
		s.where().column(DemoEntityMeta.FieldName.id).gt(1)
			.end();


		s.where(Queries.newCriteria(DemoEntity.builder()
			.name("%123%")
			.build()));

		//Consoles.println(s.toSqlNode().asPreparedSql());
		Object[] args = new Object[]{s.toSqlNode().asBoundSql()};
		Consoles.println(args);
	}

}
