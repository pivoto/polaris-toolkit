package io.polaris.core.annotation.processing;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import io.polaris.core.jdbc.TableMetaKit;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.lang.bean.BeanMap;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.reflect.SerializableBiConsumer;
import com.squareup.javapoet.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

