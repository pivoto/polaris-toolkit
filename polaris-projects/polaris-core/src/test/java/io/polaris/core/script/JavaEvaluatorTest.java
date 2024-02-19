package io.polaris.core.script;

import java.util.HashMap;
import java.util.Map;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JavaEvaluatorTest {

	@Test
	void test01() {
		for (int i = 0; i < 10; i++) {
			Map<Object, Object> output = new HashMap<>();
			Object rs = ScriptEvaluators.getEvaluator("java")
				.eval("output.put(\"rs\",input<5); return input<5;"
					, i, output, null);
			TestConsole.println(output);
			Assertions.assertEquals(i < 5, output.get("rs"));
			Assertions.assertEquals(i < 5, output.get(Evaluator.RESULT));
			Assertions.assertEquals(i < 5, rs);
		}
	}

	@Test
	void test02() {
		for (int i = 0; i < 10; i++) {
			Map<Object, Object> output = new HashMap<>();
			Object rs = ScriptEvaluators.getEvaluator("groovy")
				.eval("output.put(\"rs\",input<5); return input<5;"
					, i, output, null);
			TestConsole.println(output);
			Assertions.assertEquals(i < 5, output.get("rs"));
			Assertions.assertEquals(i < 5, output.get(Evaluator.RESULT));
			Assertions.assertEquals(i < 5, rs);
		}
	}

	@Test
	void test03() {
		for (int i = 0; i < 10; i++) {
			Map<Object, Object> output = new HashMap<>();
			Object rs = ScriptEvaluators.getEvaluator("javascript")
				.eval("output.put(\"rs\",input<5);  /*return */input<5;"
					, i, output, null);
			TestConsole.println(output);
			Assertions.assertEquals(i < 5, output.get("rs"));
			Assertions.assertEquals(i < 5, output.get(Evaluator.RESULT));
			Assertions.assertEquals(i < 5, rs);
		}
	}
}
