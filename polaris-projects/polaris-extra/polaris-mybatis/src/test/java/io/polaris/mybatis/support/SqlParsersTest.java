package io.polaris.mybatis.support;

import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.Strings;
import io.polaris.mybatis.util.SqlParsers;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.schema.Table;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

class SqlParsersTest {

	@Test
	void test02() throws ReflectiveOperationException {
		BoundSql boundSql = new BoundSql(new Configuration(),"select * from dual", new ArrayList<>(), new HashMap<>());
		Reflects.setFieldValue(boundSql, "sql","select 1");
		System.out.println(boundSql.getSql());
	}

	@Test
	void test01() throws JSQLParserException {
		String sql = "select a.id,a.name,a.field1,a.field2 " +
			" from table1 a, table2 b" +
			" where a.id = b.id" +
			" and a.id < 1000 ";
		Function<Collection<Table>, String> conditionSupplier = (tables -> {
			StringBuilder sb = new StringBuilder();
			for (Table table : tables) {
				String name = table.getName();
				if("table2".equalsIgnoreCase(name)){
					sb.append(Strings.coalesce(table.getAlias().getName(), name))
						.append(".").append("org in (1,2,3)");
				}
			}
			return sb.toString();
		});

		BiFunction<Table, String, Boolean> columnFilter = (table, col) -> {
			if (table.getName().equalsIgnoreCase("table1")) {
				if (col.equalsIgnoreCase("field1")) {
					return false;
				}
				if (col.equalsIgnoreCase("field2")) {
					return false;
				}
			}
			return true;
		};
		String s = SqlParsers.visitSelect(sql, conditionSupplier, columnFilter);
		System.out.println(s);
	}
}
