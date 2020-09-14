package io.polaris.core.script;

import org.junit.jupiter.api.Test;

import java.util.Random;

class GroovyCompilerTest {

	@Test
	void test01() {
		IScript s = GroovyCompiler.getInstance().compileScript(null,
			"def a = new " + Random.class.getName() + "().nextInt(); \n" +
				"return a;");
		System.out.println(s.run(null));

	}
}
