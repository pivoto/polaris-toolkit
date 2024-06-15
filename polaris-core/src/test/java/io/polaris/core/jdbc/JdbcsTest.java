package io.polaris.core.jdbc;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import io.polaris.core.TestConsole;
import io.polaris.core.jdbc.base.BeanCompositeMapping;
import io.polaris.core.jdbc.base.BeanMapping;
import io.polaris.core.jdbc.base.BeanPropertyMapping;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JdbcsTest {

	@Test
	void test01() {
		Assertions.assertThrows(ClassCastException.class, () -> {
			Object arr = Array.newInstance(int.class, 10);
			TestConsole.println(arr.getClass().getComponentType());
			TestConsole.println(Arrays.toString((Object[]) arr)); // error  [I cannot be cast to [Ljava.lang.Object;
		});
	}

	@Test
	void test02() throws SQLException {
		try (Connection conn = Jdbcs.getConnection("jdbc:oracle:thin:@localhost:1521/cmisdb", "basesv", "basesv");) {
			String sql = "select 1 id,'name' name, 'nickname' nick_name,3 \"rel.id\", 'name2' \"rel.name\" from dual union all " +
				"select 2 id,'name' name, 'nickname' nick_name,4 \"rel.id\", 'name2' \"rel.name\" from dual ";
			TestConsole.println(Jdbcs.queryForList(conn, sql,
				new BeanMapping<>(DataBean.class)
					.composite(new BeanCompositeMapping<>()
						.property("rel")
						.mapping(new BeanMapping<>()
							.column(new BeanPropertyMapping("name", "NAME"))
						)
					)
			));
			TestConsole.println(Jdbcs.queryForList(conn, sql, DataBean.class));
		}
	}

	@Data
	public static class DataBean {
		private Long id;
		private String name;
		private String nickName;
		private DataBean rel;
	}
}
