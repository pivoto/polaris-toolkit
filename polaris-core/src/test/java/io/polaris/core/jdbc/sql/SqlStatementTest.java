package io.polaris.core.jdbc.sql;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Test;

class SqlStatementTest {

	@Test
	void test01() {
		TestConsole.println(SqlStatement.of()
			.select("a,b,c,d,e")
			.from("tab t")
			.join("table2 t2")
			.on().add("t2.a=t.a")
			.end()
			.where("a = 1")
			.where("b = 2")
			.where()
			.or().add("c=1").add("c=2").end()
			.end()
			.groupBy("a,b,c,d,e")
			.having()
			.add("count(*)>1")
			.end()
			.toSqlString());
	}

	@Test
	void test02() {
		TestConsole.println(SqlStatement.of()
			.insert("table1")
			.columns("a", "b", "c")
			.values("1", "2", "3")
			.toSqlString());
	}

	@Test
	void test03() {
		TestConsole.println(SqlStatement.of()
			.delete("table1")
			.where("a=1", "b=2", "c=3")
			.values("1", "2", "3")
			.toSqlString());
	}
	@Test
	void test04() {
		TestConsole.println(SqlStatement.of()
			.update("table1")
			.set("a=#{a}", "b=#{b}", "c=#{c}")
			.where("a = 1")
			.where("b = 2")
			.toSqlString());
	}
}
