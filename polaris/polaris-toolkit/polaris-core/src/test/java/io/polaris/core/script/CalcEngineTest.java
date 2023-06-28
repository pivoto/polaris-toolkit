package io.polaris.core.script;

import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.objects.NativeJSON;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.options.Options;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CalcEngineTest {
	@Test
	void test02() {
		CalcEngine engine = CalcEngines.getCalcEngine("javascript");
		Map<String, Object> map = new HashMap<>();
		map.put("c","123");
		map.put("a","123");
		map.put("b","123");
		Global global = new Global(new Context(new Options(""), new ErrorManager(), Thread.currentThread().getContextClassLoader()));
		Context.setGlobal(global);
		System.out.println(NativeJSON.stringify(global,map,null," "));
		String content = "var o = JSON.stringify(input);\n" +
			"out.println(input);" +
			"for(var k in JSON){out.println(k);}" +
			"o;";
		Object rs = engine.eval(content, map);
		System.out.println(rs);
//		System.out.println(rs.getClass());
//		System.out.println(rs instanceof Map);
//		System.out.println(rs instanceof List);

	}

	@Test
	void test01() {
		CalcEngine engine = CalcEngines.getCalcEngine("javascript");
		String content = "JSON.parse(input)";
		String input = "{\"a\":1,\"b\":2,\"c\":{\"c1\":3,\"c2\":4},\"d\":[1,2,3,4]}";
		Object rs = engine.eval(content, input);
		System.out.println(rs.getClass());
		System.out.println(rs instanceof Map);
		System.out.println(rs instanceof List);
		System.out.println(rs);
		if (rs instanceof Map) {
			((Map<?, ?>) rs).forEach((k, v) -> {
				System.out.printf("%s: %s%n", k, v);
			});
			Object d = ((Map<?, ?>) rs).get("d");
			System.out.println(d.getClass());
		}
		if (rs instanceof JSObject) {
			System.out.println("array: " + ((JSObject) rs).isArray());
		}
	}
}
