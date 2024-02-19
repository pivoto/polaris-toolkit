package io.polaris.core.script;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Test;

import java.util.Random;

class GroovyCompilerTest {

	@Test
	void test01() {
		Script s = GroovyCompiler.getInstance().compileScript(null,
			"def a = new " + Random.class.getName() + "().nextInt(); \n" +
				"return a;");
		TestConsole.println(s.run(null));

	}
}
