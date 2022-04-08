package io.polaris.builder;

import com.alibaba.fastjson.JSON;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import io.polaris.builder.bean.CodeEnv;
import io.polaris.builder.bean.CodeGroup;
import io.polaris.builder.bean.CodeTable;
import io.polaris.builder.bean.CodeTemplate;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Qt
 * @version Jun 10, 2019
 */
public class CodeEnvParser {

	public static XStream buildXStream() {
		XStream xs = new XStream();
		xs.ignoreUnknownElements();
		xs.autodetectAnnotations(true);
		xs.processAnnotations(CodeEnv.class);
		xs.processAnnotations(CodeGroup.class);
		xs.processAnnotations(CodeTable.class);
		xs.processAnnotations(CodeTemplate.class);
		Converter mapConverter = new Converter() {
			@Override
			public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
				Map map = (Map) source;
				map.forEach((key, value) -> {
					writer.startNode("entry");
					writer.addAttribute("key", key.toString());
					writer.addAttribute("value", value.toString());
					writer.endNode();
				});
			}

			@Override
			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
				Map map = new LinkedHashMap();
				while (reader.hasMoreChildren()) {
					reader.moveDown();
					String key = reader.getAttribute("key");
					String value = reader.getAttribute("value");
					map.put(key, value);
					reader.moveUp();
				}
				return map;
			}

			@Override
			public boolean canConvert(Class type) {
				return Map.class.isAssignableFrom(type);
			}
		};
		xs.registerLocalConverter(CodeEnv.class, "property", mapConverter);
		xs.registerLocalConverter(CodeGroup.class, "property", mapConverter);
		xs.registerLocalConverter(CodeTemplate.class, "property", mapConverter);
		xs.registerLocalConverter(CodeTable.class, "property", mapConverter);
		return xs;
	}

	public static <T> T parseJson(String text, Class<T> clazz) {
		return JSON.parseObject(text, clazz);
	}

	public static <T> T parseJson(InputStream in, Class<T> clazz) throws IOException {
		return JSON.parseObject(in, clazz);
	}

	public static <T> T parseYaml(String text, Class<T> clazz) {
		Yaml yaml = new Yaml();
		Object o = yaml.load(text);
		String jsonString = JSON.toJSONString(o);
		return parseJson(jsonString, clazz);
	}

	public static <T> T parseYaml(InputStream in, Class<T> clazz) throws IOException {
		return parseYaml(IOUtils.toString(in, Charset.defaultCharset()), clazz);
	}

	public static <T> T parseXml(String text, Class<T> clazz) throws ReflectiveOperationException {
		XStream xs = CodeEnvParser.buildXStream();
		return (T) xs.fromXML(text, clazz.newInstance());
	}

	public static <T> T parseXml(InputStream in, Class<T> clazz) throws ReflectiveOperationException {
		XStream xs = CodeEnvParser.buildXStream();
		return (T) xs.fromXML(in, clazz.newInstance());
	}

	public static <T> T parseXml(String text, T t) {
		XStream xs = CodeEnvParser.buildXStream();
		return (T) xs.fromXML(text, t);
	}

	public static <T> T parseXml(InputStream in, T t) {
		XStream xs = CodeEnvParser.buildXStream();
		return (T) xs.fromXML(in, t);
	}

	public static String toYaml(Object o) {
		Object json = JSON.toJSON(o);
		Yaml yaml = new Yaml();
		return yaml.dump(json);
	}


}
