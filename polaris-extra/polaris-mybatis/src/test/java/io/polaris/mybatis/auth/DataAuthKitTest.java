package io.polaris.mybatis.auth;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.polaris.core.io.Consoles;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.Strings;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.schema.Table;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

class DataAuthKitTest {

	@Test
	void testStandardName() throws Exception {
		String[] names = new String[]{"table_name", "TABLE_01", "`table_name`", "\"tableName\""};
		for (String name : names) {
			Consoles.println("{}: {}", name, DataAuthKit.instance().isStandardName(name));
		}
		Enumeration<URL> es = Thread.currentThread().getContextClassLoader().getResources("");
		while (es.hasMoreElements()) {
			URL url = es.nextElement();
			Consoles.println(url);
		}
	}

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
		TableWhereGenerator conditionSupplier = (tables -> {
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

		TableColumnFilter columnFilter = (table, col) -> {
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
		String s = DataAuthKit.instance().resolveSelect(sql, conditionSupplier, columnFilter);
		System.out.println(s);
	}
}
