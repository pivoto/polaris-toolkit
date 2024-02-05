package io.polaris.core.jdbc.sql.statement.segment;

import java.util.HashMap;
import java.util.Map;

import io.polaris.core.jdbc.sql.statement.Demo2Entity;
import io.polaris.core.jdbc.sql.statement.Demo3Entity;
import io.polaris.core.jdbc.sql.statement.DemoEntity;
import org.junit.jupiter.api.Test;

class TableAccessibleTest {


	@Test
	void test01() {
		Map<String, TableEntitySegment<?>> params = new HashMap<>();
		params.put("t1", new TableEntitySegment<>(DemoEntity.class, "t1"));
		params.put("t2", new TableEntitySegment<>(Demo2Entity.class, "t2"));
		params.put("t3", new TableEntitySegment<>(Demo3Entity.class, "t3"));
		TableAccessible tableAccessible = new TableAccessible() {
			@Override
			public TableSegment<?> getTable(int tableIndex) {
				return null;
			}

			@Override
			public TableSegment<?> getTable(String tableAlias) {
				return params.get(tableAlias);
			}
		};


		System.out.printf("%s%n", tableAccessible.resolveRefTableField("&{t1}"));
		System.out.printf("%s%n", tableAccessible.resolveRefTableField("&{t2}"));
		System.out.printf("%s%n", tableAccessible.resolveRefTableField("&{t3}"));
		System.out.printf("%s%n", tableAccessible.resolveRefTableField("&{t1.*}"));
		System.out.printf("%s%n", tableAccessible.resolveRefTableField("&{t2.*}"));
		System.out.printf("%s%n", tableAccessible.resolveRefTableField("&{t3.*}"));
		System.out.printf("%s%n", tableAccessible.resolveRefTableField("select &{t1.*} from &{t1} where &{t1.fieldStr1} like '%xx%'"));


	}
}
