package io.polaris.core.net;

import java.util.Arrays;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

class URLEncodersTest {

	@Test
	void test01() {
		Object[] args6 = new Object[]{URLEncoders.parsePathSegments("//aaa//bbb/ccc/ddd////")};
		Consoles.println("parsePathSegments: {}", args6);
		Object[] args5 = new Object[]{URLEncoders.parseQuery("&abc=1234&ddd=%E6%B5%8B%E8%AF%95&eee=%3F%20%3F")};
		Consoles.println("parseQuery: {}", args5);
		Object[] args4 = new Object[]{URLEncoders.encodeFormFields("测试 测试")};
		Consoles.println("encodeFormFields: {}", args4);
		String url = "http://host/a/b/c?p1=x y&p2=测试 测试";
		Object[] args3 = new Object[]{URLEncoders.encodeFormFields(url)};
		Consoles.println("encodeFormFields: {}", args3);
		Object[] args2 = new Object[]{URLEncoders.encodeUric(url)};
		Consoles.println("encodeUric: {}", args2);
		Object[] args1 = new Object[]{URLEncoders.encodePath(url)};
		Consoles.println("encodePath: {}", args1);
		Object[] args = new Object[]{URLEncoders.encodeUserInfo(url)};
		Consoles.println("encodeUserInfo: {}", args);
	}

	@Test
	void test02() {
		String url = "http://localhost:8080/group1/default/20241226/10/50/0/01JG0DYX1A71736RSREMRGQPCW.xlsx?name=01JG0DYX1A71736RSREMRGQPCW.xlsx&download=1";
		Consoles.println(url.substring(url.indexOf('?')+1));
		Consoles.println(Arrays.toString(url.substring(url.indexOf('?')+1).split("&")));
	}
}
