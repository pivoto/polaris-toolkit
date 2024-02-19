package io.polaris.core.jdbc.sql.statement.segment;

import io.polaris.core.TestConsole;
import io.polaris.core.jdbc.sql.SqlTextParsers;
import io.polaris.core.jdbc.sql.statement.Demo2Entity;
import io.polaris.core.jdbc.sql.statement.Demo3Entity;
import io.polaris.core.jdbc.sql.statement.DemoEntity;
import org.junit.jupiter.api.Test;

class TableAccessibleTest {


	@Test
	void test01() {
		TableAccessible tableAccessible = 	TableAccessible.of(
			new TableEntitySegment<>(DemoEntity.class, "t1"),
			new TableEntitySegment<>(Demo2Entity.class, "t2"),
			new TableEntitySegment<>(Demo3Entity.class, "t3")
		);

		TestConsole.println( SqlTextParsers.resolveRefTableField("&{t1}", tableAccessible));
		TestConsole.println( SqlTextParsers.resolveRefTableField("&{t2}", tableAccessible));
		TestConsole.println( SqlTextParsers.resolveRefTableField("&{t3}", tableAccessible));
		TestConsole.println( SqlTextParsers.resolveRefTableField("&{t1.*}", tableAccessible));
		TestConsole.println( SqlTextParsers.resolveRefTableField("&{t2.*}", tableAccessible));
		TestConsole.println( SqlTextParsers.resolveRefTableField("&{t3.*}", tableAccessible));
		TestConsole.println( SqlTextParsers.resolveRefTableField("select &{t1.*} from &{t1} where &{t1.fieldStr1} like '%xx%'", tableAccessible));
		TestConsole.println( SqlTextParsers.resolveRefTableField("&{t1?.*}", tableAccessible));
		TestConsole.println( SqlTextParsers.resolveRefTableField("&{t2?.*}", tableAccessible));
		TestConsole.println( SqlTextParsers.resolveRefTableField("&{t3?.*}", tableAccessible));
		TestConsole.println( SqlTextParsers.resolveRefTableField("select &{t1?.*} from &{t1} where &{t1?.fieldStr1} like '%xx%'", tableAccessible));


	}
}
