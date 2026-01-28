package io.polaris.core.classpath;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClassPathResourcesTest {

	@Test
	void test_determineRootDir() {
		// 测试带有简单模式的位置
		assertEquals("com/example/config/", ClassPathResources.determineRootDir("com/example/config/*.xml"));
		// 测试带有复杂模式的位置
		assertEquals("com/example/", ClassPathResources.determineRootDir("com/example/**/config/*.properties"));
		// 测试没有模式的位置
		assertEquals("classpath:com/example/config/app.xml", ClassPathResources.determineRootDir("classpath:com/example/config/app.xml"));
		// 测试只有前缀的情况
		assertEquals("", ClassPathResources.determineRootDir(""));
		// 测试模式在末尾的情况
		assertEquals("com/example/config/", ClassPathResources.determineRootDir("com/example/config/*"));
		// 测试包含多个斜杠的情况
		assertEquals("com/example/", ClassPathResources.determineRootDir("com/example/**/sub/**/*.xml"));
		// 测试问号通配符模式
		assertEquals("com/", ClassPathResources.determineRootDir("com/exam?le/config/app.xml"));
		// 测试花括号模式
		assertEquals("com/", ClassPathResources.determineRootDir("com/{example,test}/config/*.xml"));
		// 测试没有冒号的位置（边界情况）
		assertEquals("com/example/config/", ClassPathResources.determineRootDir("com/example/config/*.xml"));
		// 测试空字符串（边界情况）
		assertEquals("", ClassPathResources.determineRootDir(""));
	}
}
