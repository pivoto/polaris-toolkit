package io.polaris.core.jdbc.sql;

import org.junit.jupiter.api.Test;

class SqlStatementTest {

	@Test
	void test01() {
		System.out.println(new SqlStatement()
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
		System.out.println(new SqlStatement()
			.insert("table1")
			.columns("a", "b", "c")
			.values("1", "2", "3")
			.toSqlString());
	}

	@Test
	void test03() {
		System.out.println(new SqlStatement()
			.delete("table1")
			.where("a=1", "b=2", "c=3")
			.values("1", "2", "3")
			.toSqlString());
	}
	@Test
	void test04() {
		System.out.println(new SqlStatement()
			.update("table1")
			.set("a=#{a}", "b=#{b}", "c=#{c}")
			.where("a = 1")
			.where("b = 2")
			.toSqlString());
	}
}
