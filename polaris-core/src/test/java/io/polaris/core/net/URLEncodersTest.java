package io.polaris.core.net;

import org.junit.jupiter.api.Test;

class URLEncodersTest {

	@Test
	void test01() {
		System.out.println(URLEncoders.parsePathSegments("//aaa//bbb/ccc/ddd////"));
		System.out.println(URLEncoders.parseQuery("&abc=1234&ddd=%E6%B5%8B%E8%AF%95&eee=%3F%20%3F"));
		System.out.println(URLEncoders.encodeFormFields("测试 测试"));
		System.out.println(URLEncoders.encodeFormFields("http://host/a/b/c?p1=测试 测试"));
		System.out.println(URLEncoders.encodeUric("http://host/a/b/c?p1=测试 测试"));
		System.out.println(URLEncoders.encodePath("http://host/a/b/c?p1=测试 测试"));
		System.out.println(URLEncoders.encodeUserInfo("http://host/a/b/c?p1=测试 测试"));
	}

}
