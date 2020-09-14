package io.polaris.core.script;

import com.alibaba.fastjson2.JSON;
import io.polaris.core.json.DefaultJsonSerializer;
import io.polaris.core.time.Times;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.*;

public class EvaluatorTest {
	@Data@Builder@NoArgsConstructor@AllArgsConstructor
	public static class B{
		private String id = "b";
		private int age = 1;
		private Integer intObj = Integer.valueOf(1);
		private boolean bool = true;
		private Boolean boolObj = Boolean.TRUE;
		private Date date = new Date();
	}
	@Test
	void test02() {
//		Global global = new Global(new Context(new Options(""), new ErrorManager(), Thread.currentThread().getContextClassLoader()));
//		Context.setGlobal(global);
//		System.out.println(NativeJSON.stringify(global,map,null," "));

		// language=javascript
		String content = "function asJson(input){\n" +
			"\tif (input == null\n" +
			"\t\t|| input instanceof java.lang.Number\n" +
			"\t\t|| input instanceof java.lang.Boolean\n" +
			"\t\t|| input instanceof java.lang.String\n" +
			"\t\t|| input instanceof java.util.Date\n" +
			"\t\t) {\n" +
			"\t\treturn input;\n" +
			"\t}\n" +
			"\tif (input instanceof java.util.Collection){\n" +
			"\t\tvar o = [];\n" +
			"\t\tvar arr = input.toArray();\n" +
			"\t\tfor (var i = 0; i < arr.length; i++) {\n" +
			"\t\t\to[i] = asJson(arr[i]);\n" +
			"\t\t}\n" +
			"\t\treturn o;\n" +
			"\t}" +
			"\n" +
			"\tif(input.getClass().isArray()){\n" +
			"\t\tvar o = [];\n" +
			"\t\tvar len = java.lang.reflect.Array.getLength(input);\n" +
			"\t\tfor (var i = 0; i <\tlen ; i++) {\n" +
			"\t\t\to[i] = asJson(java.lang.reflect.Array.get(input,i));\n" +
			"\t\t}\n" +
			"\t\treturn o;\n" +
			"\t}\n" +
			"\tif (input instanceof java.util.Map){\n" +
			"\t\tvar o = {};\n" +
			"\t\tfor(var k in input){\n" +
			"\t\t\to[k] = asJson(input[k]);\n" +
			"\t\t}\n" +
			"\t\treturn o;\n" +
			"\t}\n" +
			"\t{\n" +
			"\t\tvar o = {};\n" +
			"out.println('-->'+Object.prototype.toString.call(input));\n" +
			"\t\tfor(var k in input){\n" +
			"\t\t\to[k] = asJson(input[k]);\n" +
			"\t\t\tout.println('-->'+k+' '+o[k])\n" +
			"\t\t}\n" +
			"\t\treturn o;\n" +
			"\t}\n" +

			"}\n" +
			"out.println(Object.prototype.toString.call(input));\n" +
			"out.println(asJson(input));\n" +
			"for (var k in input) {\n" +
			"\tout.println(k+\"->\"+input[k]+\" :\"+Object.prototype.toString.call(input[k]));\n" +
			"}" +
			"\n" +
			"asJson(input);";
		Evaluator engine = ScriptEvaluators.getEvaluator("javascript");
		Map<String, Object> map = new HashMap<>();
		map.put("c","123");
		map.put("a","123");
		map.put("b",B.builder().id("test").age(12).build());
		map.put("d",new ArrayList<>(Arrays.asList("a","b","c")));
		map.put("e",new String[]{"a","b","c"});
		Object rs = engine.eval(content, map);
		System.out.println(rs);
//		System.out.println(rs.getClass());
//		System.out.println(rs instanceof Map);
//		System.out.println(rs instanceof List);

	}

	@Test
	void test01() {
		Evaluator engine = ScriptEvaluators.getEvaluator("javascript");
		String content = "JSON.parse(input)";
		String input = "{\"a\":1,\"b\":2,\"c\":{\"c1\":3,\"c2\":4},\"d\":[1,2,3,4]}";
		input = "[1,2,3]";
		Object rs = engine.eval(content, input);
		System.out.println(rs.getClass());
		System.out.println(rs instanceof ScriptObjectMirror);
		System.out.println(rs instanceof Map);
		System.out.println(rs instanceof JSObject);
		System.out.println(rs instanceof List);
		System.out.println(rs);
		if (rs instanceof Map) {
			((Map<?, ?>) rs).forEach((k, v) -> {
				System.out.printf("%s: %s%n", k, v);
			});
			Object d = ((Map<?, ?>) rs).get("d");
			System.out.println(d==null?null:d.getClass());
		}
		if (rs instanceof JSObject) {
			System.out.println("array: " + ((JSObject) rs).isArray());
			System.out.println("array[0]: " + ((JSObject) rs).getSlot(0));
		}
	}

	@Test
	void test03() {
		String json = "{\"a\":1,\"b\":2,\"c\":{\"c1\":3,\"c2\":4},\"d\":[1,2,3,4]}";
		Map map = new DefaultJsonSerializer().deserialize(json, Map.class);
		System.out.println(map);
	}

	@Test
	void test04() {
		Map<String, Object> map = new HashMap<>();
		map.put("c","123");
		map.put("a","123");
		map.put("b",B.builder().id("test").age(12).build());
		map.put("d",new ArrayList<>(Arrays.asList("a","b","c")));
		map.put("e",new String[]{"a","b","c"});

		System.out.println(new DefaultJsonSerializer().serialize(map));
		System.out.println(JSON.toJSONString(map));

		System.out.println(Times.nanoTime(10000, ()->new DefaultJsonSerializer().serialize(map) ));
		System.out.println(Times.nanoTime(10000, ()->JSON.toJSONString(map) ));
	}
}
