package io.polaris.core.compiler;

import org.junit.jupiter.api.Test;

class MemoryCompilerTest {

	@Test
	void test01() {
		MemoryCompiler memoryCompiler = MemoryCompiler.getInstance();
		{
			try {
				Class<?> c = memoryCompiler.compile("io.polaris.Test001", "" +
					"package io.polaris;" +
					"public class Test001{" +
					"}" +
					"");
				System.out.println(c);
				System.out.println(c.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		{
			try {
				Class<?> c = memoryCompiler.compile("io.polaris.Test002", "" +
					"package io.polaris;" +
					"public class Test002{" +
					"}" +
					"");
				System.out.println(c);
				System.out.println(c.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


	}
}
