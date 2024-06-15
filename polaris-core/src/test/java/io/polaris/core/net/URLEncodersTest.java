package io.polaris.core.net;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Test;

class URLEncodersTest {

	@Test
	void test01() {
		TestConsole.println("parsePathSegments: {}",URLEncoders.parsePathSegments("//aaa//bbb/ccc/ddd////"));
		TestConsole.println("parseQuery: {}",URLEncoders.parseQuery("&abc=1234&ddd=%E6%B5%8B%E8%AF%95&eee=%3F%20%3F"));
		TestConsole.println("encodeFormFields: {}",URLEncoders.encodeFormFields("测试 测试"));
		String url = "http://host/a/b/c?p1=x y&p2=测试 测试";
		TestConsole.println("encodeFormFields: {}",URLEncoders.encodeFormFields(url));
		TestConsole.println("encodeUric: {}",URLEncoders.encodeUric(url));
		TestConsole.println("encodePath: {}",URLEncoders.encodePath(url));
		TestConsole.println("encodeUserInfo: {}",URLEncoders.encodeUserInfo(url));
	}

}
