package io.polaris.core.compiler;

import io.polaris.core.TestConsole;
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
				TestConsole.printx(c);
				TestConsole.printx(c.newInstance());
				Assertions.assertEquals(className, c.getName());
			} catch (Exception e) {
				TestConsole.printStackTrace(e);
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
				TestConsole.printx(c);
				TestConsole.printx(c.newInstance());
				Assertions.assertEquals(className, c.getName());
			} catch (Exception e) {
				TestConsole.printStackTrace(e);
			}
		}


	}
}
