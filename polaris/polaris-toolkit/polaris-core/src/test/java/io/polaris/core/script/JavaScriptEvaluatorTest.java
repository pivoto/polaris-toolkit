package io.polaris.core.script;

import io.polaris.core.reflect.Reflects;
import io.polaris.core.reflect.SerializableQuaternionConsumer;
import org.junit.jupiter.api.Test;

import java.util.Map;

class JavaScriptEvaluatorTest {

	@Test
	void test01() {
		SerializableQuaternionConsumer<JavaScriptFunction, Object, Object, Map<String, Object>> calc = JavaScriptFunction::doEval;
		System.out.println(Reflects.getLambdaMethodName(calc));

	}
}
