package io.polaris.core.compiler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryClassLoaderTest {

	@Test
	void test01() {
		System.out.println(MemoryClassLoader.getInstance().getClassPaths());
		System.out.println(MemoryClassLoader.getInstance().getClassPath());
	}

}
