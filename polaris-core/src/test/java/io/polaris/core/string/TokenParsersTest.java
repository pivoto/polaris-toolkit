package io.polaris.core.string;

import io.polaris.core.TestConsole;
import io.polaris.core.time.Times;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class TokenParsersTest {


	@Test
	void test01() {
		Map<String, Object> params = new HashMap<>();
		for (int i = 0; i < 10; i++) {
			params.put("v" + i, i);
		}
		String text = "select * from tab t where k1 = @{v1} and k2 = @{v2} and k3 = ${v3}";
		String parsed = TokenParsers.parse(text, "@{", "}", key -> Objects.toString(params == null ? null : params.get(key), "" ));
		TestConsole.println(parsed);


		for (int repeat : new int[]{10000, 100000, 1000000}) {
			TestConsole.println("---------------------------------------------------------------" );
			TestConsole.println("repeat: " + repeat);
			TestConsole.println("time: {}", Times.millsTime(repeat, () ->
				TokenParsers.parse(text, "@{", "}", key -> Objects.toString(params == null ? null : params.get(key), "" ), true)
			));
			TestConsole.println("time: {}", Times.millsTime(repeat, () ->
				TokenParsers.parse0(text, "@{", "}", key -> Objects.toString(params == null ? null : params.get(key), "" ), true)
			));
			TestConsole.println("---------------------------------------------------------------" );
		}
	}
}
