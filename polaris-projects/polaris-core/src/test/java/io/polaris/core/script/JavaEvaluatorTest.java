package io.polaris.core.script;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class JavaEvaluatorTest {

	@Test
	void test01() {
		for (int i = 0; i < 10; i++) {

			{
				Map<Object, Object> output = new HashMap<>();
				ScriptEvaluators.getEvaluator("java").eval(
					"output.put(\"rs\",input>0); return input>0;"
					, 1, output, null);
				System.out.println(output);
			}
		}
	}

	@Test
	void test02() {
		{
			Map<Object, Object> output = new HashMap<>();
			ScriptEvaluators.getEvaluator("groovy").eval(
				"output.put(\"rs\",input>0); return input>0;"
				, 1, output, null);

			System.out.println(output);
		}
	}

	@Test
	void test03() {
		{
			Map<Object, Object> output = new HashMap<>();
			ScriptEvaluators.getEvaluator("javascript").eval(
				"output.put(\"rs\",input>0);   input>0;"
				, 1, output, null);

			System.out.println(output);
		}
	}
}
