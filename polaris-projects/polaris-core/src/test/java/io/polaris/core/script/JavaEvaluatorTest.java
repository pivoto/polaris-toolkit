package io.polaris.core.script;

import io.polaris.core.reflect.Reflects;
import io.polaris.core.reflect.SerializableQuaternionConsumer;
import org.junit.jupiter.api.Test;

import java.util.Map;

class JavaEvaluatorTest {

	@Test
	void test01() {
		SerializableQuaternionConsumer<JavaEvaluatorFunction, Object, Object, Map<String, Object>> calc = JavaEvaluatorFunction::doEval;
		System.out.println(Reflects.getLambdaMethodName(calc));

	}
}
