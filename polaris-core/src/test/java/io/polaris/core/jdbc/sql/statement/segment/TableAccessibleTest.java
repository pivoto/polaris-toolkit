package io.polaris.core.jdbc.sql.statement.segment;

import io.polaris.core.io.Consoles;
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

		String msg10 = SqlTextParsers.resolveTableRef("%{t1}", tableAccessible);
		Consoles.println(msg10);
		String msg9 = SqlTextParsers.resolveTableRef("%{t2}", tableAccessible);
		Consoles.println(msg9);
		String msg8 = SqlTextParsers.resolveTableRef("%{t3}", tableAccessible);
		Consoles.println(msg8);
		String msg7 = SqlTextParsers.resolveTableRef("%{t1.*}", tableAccessible);
		Consoles.println(msg7);
		String msg6 = SqlTextParsers.resolveTableRef("%{t2.*}", tableAccessible);
		Consoles.println(msg6);
		String msg5 = SqlTextParsers.resolveTableRef("%{t3.*}", tableAccessible);
		Consoles.println(msg5);
		String msg4 = SqlTextParsers.resolveTableRef("select %{t1.*} from %{t1} where %{t1.fieldStr1} like '%xx%'", tableAccessible);
		Consoles.println(msg4);
		String msg3 = SqlTextParsers.resolveTableRef("%{t1?.*}", tableAccessible);
		Consoles.println(msg3);
		String msg2 = SqlTextParsers.resolveTableRef("%{t2?.*}", tableAccessible);
		Consoles.println(msg2);
		String msg1 = SqlTextParsers.resolveTableRef("%{t3?.*}", tableAccessible);
		Consoles.println(msg1);
		String msg = SqlTextParsers.resolveTableRef("select %{t1?.*} from %{t1} where %{t1?.fieldStr1} like '%xx%'", tableAccessible);
		Consoles.println(msg);
	}

	@Test
	void test02() {
		TableAccessible tableAccessible = null;
		String msg10 = SqlTextParsers.resolveTableRef("%{t1(io.polaris.core.jdbc.entity.DemoEntity)}", tableAccessible);
		Consoles.println(msg10);
		String msg9 = SqlTextParsers.resolveTableRef("%{t2(io.polaris.core.jdbc.entity.Demo2Entity)}", tableAccessible);
		Consoles.println(msg9);
		String msg8 = SqlTextParsers.resolveTableRef("%{t3(io.polaris.core.jdbc.entity.Demo3Entity)}", tableAccessible);
		Consoles.println(msg8);
		String msg7 = SqlTextParsers.resolveTableRef("%{t1(io.polaris.core.jdbc.entity.DemoEntity).*}", tableAccessible);
		Consoles.println(msg7);
		String msg6 = SqlTextParsers.resolveTableRef("%{t2(io.polaris.core.jdbc.entity.Demo2Entity).*}", tableAccessible);
		Consoles.println(msg6);
		String msg5 = SqlTextParsers.resolveTableRef("%{t3(io.polaris.core.jdbc.entity.Demo3Entity).*}", tableAccessible);
		Consoles.println(msg5);
		String msg4 = SqlTextParsers.resolveTableRef("select %{t1(io.polaris.core.jdbc.entity.DemoEntity).*} from %{t1(io.polaris.core.jdbc.entity.DemoEntity)} where %{t1(io.polaris.core.jdbc.entity.DemoEntity).fieldStr1} like '%xx%'", tableAccessible);
		Consoles.println(msg4);
		String msg3 = SqlTextParsers.resolveTableRef("%{t1(io.polaris.core.jdbc.entity.DemoEntity)?.*}", tableAccessible);
		Consoles.println(msg3);
		String msg2 = SqlTextParsers.resolveTableRef("%{t2(io.polaris.core.jdbc.entity.Demo2Entity)?.*}", tableAccessible);
		Consoles.println(msg2);
		String msg1 = SqlTextParsers.resolveTableRef("%{t3(io.polaris.core.jdbc.entity.Demo3Entity)?.*}", tableAccessible);
		Consoles.println(msg1);
		String msg = SqlTextParsers.resolveTableRef("select %{t1(io.polaris.core.jdbc.entity.DemoEntity)?.*} from %{t1(io.polaris.core.jdbc.entity.DemoEntity)?} where %{t1(io.polaris.core.jdbc.entity.DemoEntity)?.fieldStr1} like '%xx%'", tableAccessible);
		Consoles.println(msg);


	}
}
