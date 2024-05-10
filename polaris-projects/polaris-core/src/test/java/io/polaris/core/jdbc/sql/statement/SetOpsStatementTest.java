package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.TestConsole;
import io.polaris.core.jdbc.entity.Demo2EntitySql;
import io.polaris.core.jdbc.entity.Demo3EntitySql;
import io.polaris.core.jdbc.entity.DemoEntitySql;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author Qt
 * @since  Dec 30, 2023
 */
public class SetOpsStatementTest {

	@Test
	void test01() {
		Long[] ids = new Long[20];
		Arrays.fill(ids,1L);


		SetOpsStatement<?> setOpsStatement = SetOpsStatement.of(
			DemoEntitySql.select().selectAll().where().raw("1=1")
				.id().inLarge(Arrays.asList(ids),5)
				.end());
		setOpsStatement.unionAll(
			Demo2EntitySql.select().selectAll().where().raw("1=1").end());
		setOpsStatement.minus(
			Demo3EntitySql.select().selectAll().where().raw("1=1").end());

		TestConsole.println(setOpsStatement.toSqlNode());
		TestConsole.println();
		TestConsole.println(setOpsStatement.toCountSqlNode());
	}
}
