package io.polaris.core.jdbc.sql.statement.segment;

import io.polaris.core.TestConsole;
import io.polaris.core.jdbc.sql.SqlTextParsers;
import io.polaris.core.jdbc.entity.Demo2Entity;
import io.polaris.core.jdbc.entity.Demo3Entity;
import io.polaris.core.jdbc.entity.DemoEntity;
import org.junit.jupiter.api.Test;

class TableAccessibleTest {


	@Test
	void test01() {
		TableAccessible tableAccessible = TableAccessible.of(
			new TableEntitySegment<>(DemoEntity.class, "t1"),
			new TableEntitySegment<>(Demo2Entity.class, "t2"),
			new TableEntitySegment<>(Demo3Entity.class, "t3")
		);

		TestConsole.println(SqlTextParsers.resolveTableRef("&{t1}", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("&{t2}", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("&{t3}", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("&{t1.*}", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("&{t2.*}", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("&{t3.*}", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("select &{t1.*} from &{t1} where &{t1.fieldStr1} like '%xx%'", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("&{t1?.*}", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("&{t2?.*}", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("&{t3?.*}", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("select &{t1?.*} from &{t1} where &{t1?.fieldStr1} like '%xx%'", tableAccessible));
	}

	@Test
	void test02() {
		TableAccessible tableAccessible = null;
		TestConsole.println(SqlTextParsers.resolveTableRef("&{t1(io.polaris.core.jdbc.entity.DemoEntity)}", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("&{t2(io.polaris.core.jdbc.entity.Demo2Entity)}", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("&{t3(io.polaris.core.jdbc.entity.Demo3Entity)}", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("&{t1(io.polaris.core.jdbc.entity.DemoEntity).*}", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("&{t2(io.polaris.core.jdbc.entity.Demo2Entity).*}", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("&{t3(io.polaris.core.jdbc.entity.Demo3Entity).*}", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("select &{t1(io.polaris.core.jdbc.entity.DemoEntity).*} from &{t1(io.polaris.core.jdbc.entity.DemoEntity)} where &{t1(io.polaris.core.jdbc.entity.DemoEntity).fieldStr1} like '%xx%'", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("&{t1(io.polaris.core.jdbc.entity.DemoEntity)?.*}", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("&{t2(io.polaris.core.jdbc.entity.Demo2Entity)?.*}", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("&{t3(io.polaris.core.jdbc.entity.Demo3Entity)?.*}", tableAccessible));
		TestConsole.println(SqlTextParsers.resolveTableRef("select &{t1(io.polaris.core.jdbc.entity.DemoEntity)?.*} from &{t1(io.polaris.core.jdbc.entity.DemoEntity)?} where &{t1(io.polaris.core.jdbc.entity.DemoEntity)?.fieldStr1} like '%xx%'", tableAccessible));


	}
}
