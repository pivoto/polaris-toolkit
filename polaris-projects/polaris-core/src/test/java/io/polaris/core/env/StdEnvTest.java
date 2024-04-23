package io.polaris.core.env;

import java.util.Properties;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

class StdEnvTest {

	@Test
	@Order(1)
	void test01() {
		TestConsole.println(GlobalStdEnv.class);
	}

	@Test
	@Order(2)
	void test02() {
		TestConsole.println(GlobalStdEnv.asMap());
		TestConsole.println(Version.current());
	}

	@Test
	@Order(3)
	void test03() {
		GlobalStdEnv.env().withCustomizer();
		GlobalStdEnv.env().withCustomizer();
	}


	@Test
	@Order(4)
	void test04() {
		StdEnv props = StdEnv.newInstance();
		props.setDefaults("key", "1");
		props.setDefaults("keyBlank", "");
		TestConsole.println("[{}]", props.get("key"));
		TestConsole.println("[{}]", props.get("keyBlank"));
		TestConsole.println("[{}]", props.get("keyNull"));
	}

	@Test
	@Order(5)
	void test05() {
		TestConsole.println(GlobalStdEnv.get("java.home"));
		TestConsole.println(GlobalStdEnv.get("java.io.tmpdir"));
		TestConsole.println(GlobalStdEnv.get("kkk", "default"));

		Env follower = Env.wrap(new Properties());
		follower.set("kkk", "follower");
		GlobalStdEnv.addEnvLast(follower);
		TestConsole.println(GlobalStdEnv.get("kkk"));

		Env leader = Env.wrap(new Properties());
		leader.set("kkk", "leader");
		GlobalStdEnv.addEnvFirst(leader);
		TestConsole.println(GlobalStdEnv.get("kkk"));

		GlobalStdEnv.set("kkk", "${user.home} | ${java.home} | ${test.home:-${test.home2:-/a/b/c}}");
		TestConsole.println(GlobalStdEnv.get("kkk"));
		GlobalStdEnv.set("test.home2", "/test/home2");
		TestConsole.println(GlobalStdEnv.get("kkk"));
		GlobalStdEnv.set("test.home", "/test/home");
		TestConsole.println(GlobalStdEnv.get("kkk"));
	}


	@Test
	void test06() {
		GlobalStdEnv.addEnvFirst(Env.delegate(GlobalStdEnv.env()));
		TestConsole.println("key: {}",GlobalStdEnv.get("kkk"));
		TestConsole.println("keys: {}",GlobalStdEnv.keys());
	}
}
