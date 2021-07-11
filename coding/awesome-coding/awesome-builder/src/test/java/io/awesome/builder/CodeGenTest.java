package io.awesome.builder;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.thoughtworks.xstream.XStream;
import io.awesome.builder.CodeEnvParser;
import io.awesome.builder.Main;
import io.awesome.builder.bean.CodeEnv;
import io.awesome.builder.bean.CodeGroup;
import io.awesome.builder.bean.Tables;
import io.awesome.dbv.toolkit.IOKit;
import io.awesome.dbv.toolkit.MapKit;
import org.dom4j.DocumentException;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @version Jun 04, 2019
 */
public class CodeGenTest {
	public static void main(String[] args) throws IOException, DocumentException {
		Main.main(new String[]{
			"--jdbcCfg", "xe.xml",
			"--xmlData", "../awesome--tmp/tables.xml",
			"--codegen", "cfg/codegen.xml",
		});

		/*TablesReader reader = TablesReaders.newJdbcTablesReader(IOUtils.getInputStream("xe.xml"));
		CodeGenerator generator;

		generator = new CodeGenerator(reader, "cfg/codegen.xml");
		generator.generate();
		reader.close();*/

	}

	@Test
	public void test05() {
		XStream xs = new XStream();
		xs.ignoreUnknownElements();
		xs.autodetectAnnotations(true);
		xs.processAnnotations(Tables.class);
		xs.addDefaultImplementation(MapKit.CaseInsensitiveMap.class,Map.class);

		System.out.println(xs.toXML(new HashMap<>()));
	}

	@Test
	public void test04() throws FileNotFoundException {
		XStream xs = CodeEnvParser.buildXStream();
		CodeEnv codeEnv = new CodeEnv();
		codeEnv.setOutdir("./out");
		codeEnv.setProperty(new HashMap<>());
		codeEnv.getProperty().put("env.a", "1");
		codeEnv.setGroups(new ArrayList<>());
		codeEnv.getGroups().add(new CodeGroup());
		codeEnv.getGroups().get(0).setProperty(new HashMap<>());
		codeEnv.getGroups().get(0).getProperty().put("a", "1");

		System.out.println(codeEnv);
		System.out.println(xs.toXML(codeEnv));

	}

	@Test
	public void test03() throws FileNotFoundException {
		XStream xs = CodeEnvParser.buildXStream();
		InputStream in = IOKit.getInputStream("/cfg/codegen.xml");
		CodeEnv o = (CodeEnv) xs.fromXML(in, new CodeEnv());
		System.out.println(o.getClass());
		System.out.println(JSON.toJSONString(o, true));
		System.out.println(o.getGroups().get(0));
	}

//	@Test
//	public void test01() throws Exception {
//		CodeEnv env = Marshallers.unmarshal(new CodeEnv(), IOUtils.getInputStream("/cfg/codegen.xml"));
//		System.out.println(env);
////		System.out.println(JsonUtils.toJson(env));
//		System.out.println(JSONObject.toJSONString(env, true));
//	}

	@Test
	public void test02() throws Exception {
		CodeEnv env = new CodeEnv();
		env.setProperty(new HashMap<>());
		env.getProperty().put("a", "1");
		env.getProperty().put("b", "2");

		env.setGroups(Arrays.asList(new CodeGroup()));

		XmlMapper xmlMapper = new XmlMapper();
		String s = xmlMapper.writeValueAsString(env);
		System.out.println(s);

		s = JSON.toJSONString(env);
		System.out.println(s);

		env = JSON.parseObject(s, CodeEnv.class);
		System.out.println(JSON.toJSONString(env, true));

		Yaml yaml = new Yaml();
		System.out.println(yaml.dump(JSON.parseObject(s)));
		System.out.println(CodeEnvParser.toYaml(env));
	}
}
