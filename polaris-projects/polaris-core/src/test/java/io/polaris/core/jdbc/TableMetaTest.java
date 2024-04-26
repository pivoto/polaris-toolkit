package io.polaris.core.jdbc;

import io.polaris.core.jdbc.entity.DemoEntity;
import io.polaris.core.jdbc.entity.DemoTest01Entity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TableMetaTest {


	@Test
	void test() {
		TableMeta tableMeta = TableMetaKit.instance().get(DemoTest01Entity.class);
		TableMeta clone = tableMeta.clone();

		assertNotEquals(tableMeta, clone);
	}



}
