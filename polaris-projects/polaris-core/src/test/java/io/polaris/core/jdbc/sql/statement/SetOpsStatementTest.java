package io.polaris.core.jdbc.sql.statement;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author Qt
 * @since 1.8,  Dec 30, 2023
 */
public class SetOpsStatementTest {

	@Test
	void test01() {
		Long[] ids = new Long[20];
		Arrays.fill(ids,1L);


		SetOpsStatement<?> setOpsStatement = SetOpsStatement.of(
			DemoEntitySql.select().selectAll().where().andRaw("1=1")
				.id().inLarge(Arrays.asList(ids),5)
				.end());
		setOpsStatement.unionAll(
			Demo2EntitySql.select().selectAll().where().andRaw("1=1").end());
		setOpsStatement.minus(
			Demo3EntitySql.select().selectAll().where().andRaw("1=1").end());

		System.out.println(setOpsStatement.toSqlNode());
		System.out.println();
		System.out.println(setOpsStatement.toCountSqlNode());
	}
}
