package io.polaris.core.net.http;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

class HttpClientsTest {

	@Test
	void test01() throws GeneralSecurityException, IOException {
		Response response = HttpClients.doRequest(new RequestSettings()
			.withUrl("https://www.baidu.com/s?wd=test")
		);
		System.out.println(JSON.toJSONString(response, JSONWriter.Feature.PrettyFormat));
	}
}
