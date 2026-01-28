package io.polaris.core.string;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import io.polaris.core.reflect.SmartClassHandle;
import io.polaris.core.reflect.SmartClassHandles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PathMatcherTest {

	private AntPathMatcher pathMatcher;

	@BeforeEach
	void setUp() {
		pathMatcher = new AntPathMatcher();
	}

	@Test
	void isPattern() {
		// 测试是否能正确识别路径模式
		assertTrue(pathMatcher.isPattern("com/t?st.jsp"));
		assertTrue(pathMatcher.isPattern("com/*.jsp"));
		assertTrue(pathMatcher.isPattern("com/**/test.jsp"));
		assertTrue(pathMatcher.isPattern("com/{filename:\\w+}.jsp"));

		assertFalse(pathMatcher.isPattern("com/test.jsp"));
		assertFalse(pathMatcher.isPattern(""));
		assertFalse(pathMatcher.isPattern(null));
	}

	@Test
	void match() {
		// 测试基本匹配功能
		assertTrue(pathMatcher.match("com/t?st.jsp", "com/test.jsp"));
		assertTrue(pathMatcher.match("com/t?st.jsp", "com/tast.jsp"));
		assertTrue(pathMatcher.match("com/*.jsp", "com/test.jsp"));
		assertTrue(pathMatcher.match("com/*.jsp", "com/hello.jsp"));

		// 测试通配符匹配
		assertTrue(pathMatcher.match("com/**/test.jsp", "com/test.jsp"));
		assertTrue(pathMatcher.match("com/**/test.jsp", "com/foo/test.jsp"));
		assertTrue(pathMatcher.match("com/**/test.jsp", "com/foo/bar/test.jsp"));

		// 测试URI模板变量
		assertTrue(pathMatcher.match("com/{filename:\\w+}.jsp", "com/test.jsp"));
		assertTrue(pathMatcher.match("com/{filename:\\w+}.jsp", "com/hello.jsp"));

		// 测试不匹配的情况
		assertFalse(pathMatcher.match("com/t?st.jsp", "com/teest.jsp"));
		assertFalse(pathMatcher.match("com/*.jsp", "com/test.html"));
		assertFalse(pathMatcher.match("com/test.jsp", "com/test1.jsp"));
	}

	@Test
	void matchStart() {
		// 测试部分匹配功能
		assertTrue(pathMatcher.matchStart("com/*", "com/test.jsp"));
		assertTrue(pathMatcher.matchStart("com/**", "com/foo/test.jsp"));
		assertFalse(pathMatcher.matchStart("com/test/*", "com/hello.jsp"));
	}

	@Test
	void extractPathWithinPattern() {
		assertEquals("", pathMatcher.extractPathWithinPattern("/docs/cvs/commit.html", "/docs/cvs/commit.html"));
		assertEquals("cvs/commit", pathMatcher.extractPathWithinPattern("/docs/*", "/docs/cvs/commit"));
		assertEquals("commit.html", pathMatcher.extractPathWithinPattern("/docs/cvs/*.html", "/docs/cvs/commit.html"));
		assertEquals("cvs/commit", pathMatcher.extractPathWithinPattern("/docs/**", "/docs/cvs/commit"));
		assertEquals("cvs/commit.html", pathMatcher.extractPathWithinPattern("/docs/**/*html", "/docs/cvs/commit.html"));
		assertEquals("cvs/sub/commit.html", pathMatcher.extractPathWithinPattern("/docs/**/*html", "/docs/cvs/sub/commit.html"));
		assertEquals("docs/cvs/commit.html", pathMatcher.extractPathWithinPattern("/*.html", "/docs/cvs/commit.html"));
		assertEquals("/docs/cvs/commit.html", pathMatcher.extractPathWithinPattern("*", "/docs/cvs/commit.html"));
	}

	@Test
	void extractUriTemplateVariables() {
		// 测试URI模板变量提取
		Map<String, String> vars = pathMatcher.extractUriTemplateVariables("com/{filename:\\w+}.jsp", "com/test.jsp");
		assertNotNull(vars);
		assertEquals(1, vars.size());
		assertEquals("test", vars.get("filename"));

		vars = pathMatcher.extractUriTemplateVariables("/hotels/{hotel}/bookings/{booking}", "/hotels/123/bookings/456");
		assertNotNull(vars);
		assertEquals(2, vars.size());
		assertEquals("123", vars.get("hotel"));
		assertEquals("456", vars.get("booking"));
	}

	@Test
	void combine() {
		assertEquals("", pathMatcher.combine(null, null));
		assertEquals("/hotels", pathMatcher.combine("/hotels", null));
		assertEquals("/hotels", pathMatcher.combine(null, "/hotels"));
		assertEquals("/hotels/bookings", pathMatcher.combine("/hotels", "/bookings"));
		assertEquals("/hotels/bookings", pathMatcher.combine("/hotels", "bookings"));
		assertEquals("/hotels/bookings", pathMatcher.combine("/hotels/*", "/bookings"));
		assertEquals("/hotels/**/bookings", pathMatcher.combine("/hotels/**", "/bookings"));
		assertEquals("/hotels/{hotel}", pathMatcher.combine("/hotels", "{hotel}"));
		assertEquals("/hotels/{hotel}", pathMatcher.combine("/hotels/*", "{hotel}"));
		assertEquals("/hotels/**/{hotel}", pathMatcher.combine("/hotels/**", "{hotel}"));
		assertEquals("/hotels.html", pathMatcher.combine("/*.html", "/hotels.html"));
		assertEquals("/hotels.html", pathMatcher.combine("/*.html", "/hotels"));
	}

	@Test
	void customizePathSeparator() {
		AntPathMatcher matcherWithCustomSeparator = new AntPathMatcher(".");
		assertFalse(matcherWithCustomSeparator.match("com.test.jsp", "com/test.jsp"));
		assertTrue(matcherWithCustomSeparator.match("com.test.jsp", "com.test.jsp"));
	}

	@Test
	void caseSensitive() {
		// 测试大小写敏感
		assertTrue(pathMatcher.match("com/test.jsp", "com/test.jsp"));
		assertFalse(pathMatcher.match("com/Test.jsp", "com/test.jsp"));

		// 设置为不区分大小写
		pathMatcher.setCaseSensitive(false);
		assertTrue(pathMatcher.match("com/Test.jsp", "com/test.jsp"));
		assertTrue(pathMatcher.match("com/test.jsp", "COM/TEST.JSP"));
	}

	@Test
	void trimTokens() throws Throwable {
		SmartClassHandle<AntPathMatcher> handle = SmartClassHandles.newClassHandle(AntPathMatcher.class);
		// 测试是否修剪标记
		assertFalse(handle.getField(pathMatcher, "trimTokens", Boolean.class));
		pathMatcher.setTrimTokens(true);
		assertTrue(handle.getField(pathMatcher, "trimTokens", Boolean.class));
	}

	@Test
	void cachePatterns() throws Throwable {
		SmartClassHandle<AntPathMatcher> handle = SmartClassHandles.newClassHandle(AntPathMatcher.class);
		// 测试缓存模式
		assertNull(handle.getField(pathMatcher, "cachePatterns", Boolean.class));

		pathMatcher.setCachePatterns(true);
		assertTrue(handle.getField(pathMatcher, "cachePatterns", Boolean.class));
		pathMatcher.setCachePatterns(false);
		assertFalse(handle.getField(pathMatcher, "cachePatterns", Boolean.class));
	}
}
