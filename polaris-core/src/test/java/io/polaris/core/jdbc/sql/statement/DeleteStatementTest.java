package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.io.Consoles;
import io.polaris.core.jdbc.entity.DemoEntity;
import io.polaris.core.jdbc.entity.DemoEntityMeta;
import io.polaris.core.jdbc.sql.query.Queries;
import org.junit.jupiter.api.Test;

public class DeleteStatementTest {

	@Test
	void test01() {
		DeleteStatement<?> s = new DeleteStatement<>(DemoEntity.class);
		s.where().column(DemoEntityMeta.FieldName.id).gt(1)
			.end();
		s.where(Queries.newCriteria(DemoEntity.builder()
			.name("%123%")
			.col6(1)
			.build()));
		Object[] args = new Object[]{s.toSqlNode().asBoundSql()};
		Consoles.println(args);
	}

}
