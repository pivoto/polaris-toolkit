package io.polaris.builder.code.config;

import com.alibaba.fastjson2.JSON;
import com.thoughtworks.xstream.XStream;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class CodeEnvTest {

	@Test
	void test01() {
		XStream xs = ConfigParser.buildXStream();

		CodeTable table = new CodeTable();
		table.setCatalog("catalog");
		table.setProperty(new LinkedHashMap<>());
		table.getProperty().put("a","1");
		table.getProperty().put("b","1");

		CodeGroup group = new CodeGroup();
		group.getTables().add(table);
		group.getTemplates().add(new CodeTemplate());

		CodeEnv env = new CodeEnv();
		env.getGroups().add(group);
		env.setTablePrefix("t_");
		env.setTableSuffix("_bak");
		env.setProperty(new LinkedHashMap<>());
		env.getProperty().put("a","1");
		env.getProperty().put("b","1");


		System.out.println(xs.toXML(env));
		System.out.println(JSON.toJSONString(env));
		System.out.println(xs.toXML(ConfigParser.parseXml(xs.toXML(env), new CodeEnv())));
		System.out.println(JSON.toJSONString(ConfigParser.parseXml(xs.toXML(env), new CodeEnv())));

	}
}
