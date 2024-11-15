package io.polaris.core.compiler;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MemoryCompilerTest {

	@Test
	void test01() {
		MemoryCompiler memoryCompiler = MemoryCompiler.getInstance();
		{
			try {
				String className = "io.polaris.Test001";
				Class<?> c = memoryCompiler.compile(className, "" +
					"package io.polaris;" +
					"public class Test001{" +
					"}" +
					"");
				Consoles.log("", c);
				Object[] args = new Object[]{c.newInstance()};
				Consoles.log("", args);
				Assertions.assertEquals(className, c.getName());
			} catch (Exception e) {
				Consoles.printStackTrace(e);
			}
		}
		{
			try {
				String className = "io.polaris.Test002";
				Class<?> c = memoryCompiler.compile(className, "" +
					"package io.polaris;" +
					"public class Test002{" +
					"}" +
					"");
				Consoles.log("", c);
				Object[] args = new Object[]{c.newInstance()};
				Consoles.log("", args);
				Assertions.assertEquals(className, c.getName());
			} catch (Exception e) {
				Consoles.printStackTrace(e);
			}
		}


	}
}
