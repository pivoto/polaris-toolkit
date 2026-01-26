package io.polaris.core.classpath;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClassPathsTest {

	@Test
	void test01() {
		Consoles.println(ClassPaths.getClassLoaderUrls(ClassPaths.class.getClassLoader()));
	}
}
