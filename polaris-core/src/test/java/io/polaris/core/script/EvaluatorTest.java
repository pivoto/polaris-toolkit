package io.polaris.core.script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.polaris.core.io.Consoles;
import io.polaris.core.json.DefaultJsonSerializer;
import io.polaris.core.json.TestJsonSerializer;
import io.polaris.core.time.Times;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSON;

public class EvaluatorTest {
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class B {
		@Builder.Default
		private String id = "b";
		@Builder.Default
		private int age = 1;
		@Builder.Default
		private long longVal = System.currentTimeMillis();
		@Builder.Default
		private Integer intObj = Integer.valueOf(1);
		@Builder.Default
		private Long longObj = System.currentTimeMillis();
		@Builder.Default
		private boolean bool = true;
		@Builder.Default
		private Boolean boolObj = Boolean.TRUE;
		@Builder.Default
		private Date date = new Date();
	}

	@Test
	void test01() {
//		Global global = new Global(new Context(new Options(""), new ErrorManager(), Thread.currentThread().getContextClassLoader()));
//		Context.setGlobal(global);
//		Consoles.println(NativeJSON.stringify(global,map,null," "));

		// language=javascript
		String content = "function asJson(input) {\n" +
			"    if (input == null\n" +
			"        || input instanceof java.lang.Number\n" +
			"        || input instanceof java.lang.Boolean\n" +
			"        || input instanceof java.lang.String\n" +
			"        || input instanceof java.util.Date\n" +
			"    ) {\n" +
			"        return input;\n" +
			"    }\n" +
			"    if (input instanceof java.util.Collection) {\n" +
			"        var o = [];\n" +
			"        var arr = input.toArray();\n" +
			"        for (var i = 0; i < arr.length; i++) {\n" +
			"            o[i] = asJson(arr[i]);\n" +
			"        }\n" +
			"        return o;\n" +
			"    }" +
			"\n" +
			"    if (input.getClass().isArray()) {\n" +
			"        var o = [];\n" +
			"        var len = java.lang.reflect.Array.getLength(input);\n" +
			"        for (var i = 0; i < len; i++) {\n" +
			"            o[i] = asJson(java.lang.reflect.Array.get(input, i));\n" +
			"        }\n" +
			"        return o;\n" +
			"    }\n" +
			"    if (input instanceof java.util.Map) {\n" +
			"        var o = {};\n" +
			"        for (var k in input) {\n" +
			"            o[k] = asJson(input[k]);\n" +
			"        }\n" +
			"        return o;\n" +
			"    }\n" +
			"    {\n" +
			"        var o = {};\n" +
			"        out.println('-->' + Object.prototype.toString.call(input));\n" +
			"        for (var k in input) {\n" +
			"            o[k] = asJson(input[k]);\n" +
			"            out.println('-->' + k + ' ' + o[k])\n" +
			"        }\n" +
			"        return o;\n" +
			"    }\n" +

			"}\n" +
			"\n" +
			"out.println(Object.prototype.toString.call(input));\n" +
			"out.println(asJson(input));\n" +
			"for (var k in input) {\n" +
			"    out.println(k + \"->\" + input[k] + \" :\" + Object.prototype.toString.call(input[k]));\n" +
			"}" +
			"\n" +
			"asJson(input);";
		Evaluator engine = ScriptEvaluators.getEvaluator("javascript");
		Map<String, Object> map = new HashMap<>();
		map.put("c", "123");
		map.put("a", "123");
		map.put("b", B.builder().id("test").age(12).build());
		map.put("d", new ArrayList<>(Arrays.asList("a", "b", "c")));
		map.put("e", new String[]{"a", "b", "c"});
		Object rs = engine.eval(content, map);
		Consoles.println("rs: {}", rs);
		Assertions.assertInstanceOf(ScriptObjectMirror.class, rs);
		Assertions.assertInstanceOf(Map.class, rs);
	}

	@Test
	void test02() {
		Evaluator engine = ScriptEvaluators.getEvaluator("javascript");
		String content = "JSON.parse(input)";
		String input = "{\"a\":1,\"b\":2,\"c\":{\"c1\":3,\"c2\":4},\"d\":[1,2,3,4]}";
		input = "[1,2,3]";
		Object rs = engine.eval(content, input);
		Assertions.assertInstanceOf(ScriptObjectMirror.class, rs);
		Assertions.assertInstanceOf(Map.class, rs);
		Assertions.assertInstanceOf(JSObject.class, rs);
		{
			((Map<?, ?>) rs).forEach((k, v) -> {
				Consoles.println("map> key:{}, val:{}", k, v);
			});
			Object d = ((Map<?, ?>) rs).get("d");
			Object[] args = new Object[]{d == null ? null : d.getClass()};
			Consoles.println(args);
		}
		{
			String msg1 = "array: " + ((JSObject) rs).isArray();
			Consoles.println(msg1);
			String msg = "array[0]: " + ((JSObject) rs).getSlot(0);
			Consoles.println(msg);
		}
	}

	@Test
	void test03() {
		String json = "{\"a\":1,\"b\":2,\"c\":{\"c1\":3,\"c2\":4},\"d\":[1,2,3,4]}";
		Map map = new DefaultJsonSerializer().deserialize(json, Map.class);
		Consoles.println(map);
	}

	@Test
	void test04() {
		Map<String, Object> map = new HashMap<>();
		map.put("c", "123");
		map.put("a", "123");
		B b = new B();
		map.put("b", b);
		map.put("d", new ArrayList<>(Arrays.asList("a", "b", "c")));
		map.put("e", new String[]{"a", "b", "c"});

		Object o = map;

		DefaultJsonSerializer defaultJsonSerializer = new DefaultJsonSerializer();
		TestJsonSerializer testJsonSerializer = new TestJsonSerializer();
		String msg2 = defaultJsonSerializer.serialize(o);
		Consoles.println(msg2);
		String msg1 = testJsonSerializer.serialize(o);
		Consoles.println(msg1);
		String msg = JSON.toJSONString(o);
		Consoles.println(msg);

		Object[] args2 = new Object[]{Times.millsTime(10000, () -> JSON.toJSONString(o))};
		Consoles.println("elapse: {}ms", args2);
		Object[] args1 = new Object[]{Times.millsTime(10000, () -> defaultJsonSerializer.serialize(o))};
		Consoles.println("elapse: {}ms", args1);
		Object[] args = new Object[]{Times.millsTime(10000, () -> testJsonSerializer.serialize(o))};
		Consoles.println("elapse: {}ms", args);
	}
}
