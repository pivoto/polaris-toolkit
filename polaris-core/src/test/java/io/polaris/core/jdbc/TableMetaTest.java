package io.polaris.core.jdbc;

import io.polaris.core.TestConsole;
import io.polaris.core.jdbc.entity.DemoEntity;
import io.polaris.core.jdbc.entity.DemoTest01Entity;
import io.polaris.core.jdbc.entity.DemoTest02Entity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TableMetaTest {


	@Test
	void test01() {
		TableMeta tableMeta = TableMetaKit.instance().get(DemoTest01Entity.class);
		TableMeta clone = tableMeta.clone();
		assertEquals(tableMeta, clone);
	}



	@Test
	void test02() {
		TableMeta tableMeta = TableMetaKit.instance().get(DemoTest02Entity.class);

		TableMetaKit.instance().addMutation(TableMetaMutation.builder(DemoTest02Entity.class).renameTable("NEW_TABLE")
				.renameColumn("id","pk")
			.build());
		TableMeta newTable = TableMetaKit.instance().get(DemoTest02Entity.class);
		assertNotEquals(tableMeta, newTable);
	}

	@Test
	void test03() {
		TableMeta tableMeta = TableMetaKit.instance().get(DemoTest02Entity.class);

		TableMetaKit.instance().addMutationInCurrentThread(TableMetaMutation.builder(DemoTest02Entity.class).renameTable("NEW_TABLE")
				.renameColumn("id","pk")
			.build());
		TableMeta newTable = TableMetaKit.instance().get(DemoTest02Entity.class);
		assertNotEquals(tableMeta, newTable);
	}



}
