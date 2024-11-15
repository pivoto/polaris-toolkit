package io.polaris.core.script;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

import java.util.Random;

class GroovyCompilerTest {

	@Test
	void test01() {
		Script s = GroovyCompiler.getInstance().compileScript(null,
			"def a = new " + Random.class.getName() + "().nextInt(); \n" +
				"return a;");
		Object[] args = new Object[]{s.run(null)};
		Consoles.println(args);

	}
}
