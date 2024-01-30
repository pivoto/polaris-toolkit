package io.polaris.core.annotation.processing;

import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.TableMetaKit;
import io.polaris.core.jdbc.sql.statement.JoinDriver;
import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;

class DemoEntityTest {
	DemoEntity demoEntity = new DemoEntity();

	@Test
	void test01() {
		TableMeta tableMeta = TableMetaKit.instance().get(DemoEntity.class);
		System.out.println(tableMeta);
		System.out.println(JSON.toJSONString(tableMeta, JSONWriter.Feature.PrettyFormat));
		System.out.println(JSON.toJSONString(tableMeta.getColumns().get(DemoEntityMeta.FieldName.name), JSONWriter.Feature.PrettyFormat));
	}

}

