package io.polaris.builder.code.dto;

import org.junit.jupiter.api.Test;

import java.sql.Types;

import static org.junit.jupiter.api.Assertions.*;

class TableDtoTest {

	@Test
	void test01() {
		TableDto t1 = new TableDto();
		t1.setName("t_user");

		ColumnDto c1 = new ColumnDto();
		c1.setName("id");
		c1.setType(Types.VARCHAR);
		t1.getColumns().add(c1);
		t1.getPkColumns().add(c1);
		TableDto t2 = t1.clone();
		t2.setName("t_user2");
		t2.getPkColumns().get(0).setName("id2");

		System.out.println(t1);
		System.out.println(t1.getColumns());
		System.out.println(t1.getPkColumns());
		System.out.println(t1.getNormalColumns());
		System.out.println(t2);
		System.out.println(t2.getColumns());
		System.out.println(t2.getPkColumns());
		System.out.println(t2.getNormalColumns());
	}
}
