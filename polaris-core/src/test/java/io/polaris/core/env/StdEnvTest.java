package io.polaris.core.env;

import java.util.Properties;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

class StdEnvTest {

	@Test
	@Order(1)
	void test01() {
		Consoles.println(GlobalStdEnv.class);
	}

	@Test
	@Order(2)
	void test02() {
		Object[] args = new Object[]{GlobalStdEnv.asMap()};
		Consoles.println(args);
		Consoles.println(Versions.current());
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
		Object[] args2 = new Object[]{props.get("key")};
		Consoles.println("[{}]", args2);
		Object[] args1 = new Object[]{props.get("keyBlank")};
		Consoles.println("[{}]", args1);
		Object[] args = new Object[]{props.get("keyNull")};
		Consoles.println("[{}]", args);
	}

	@Test
	@Order(5)
	void test05() {
		String msg7 = GlobalStdEnv.get("java.home");
		Consoles.println(msg7);
		String msg6 = GlobalStdEnv.get("java.io.tmpdir");
		Consoles.println(msg6);
		String msg5 = GlobalStdEnv.get("kkk", "default");
		Consoles.println(msg5);

		Env follower = Env.wrap(new Properties());
		follower.set("kkk", "follower");
		GlobalStdEnv.addEnvLast(follower);
		String msg4 = GlobalStdEnv.get("kkk");
		Consoles.println(msg4);

		Env leader = Env.wrap(new Properties());
		leader.set("kkk", "leader");
		GlobalStdEnv.addEnvFirst(leader);
		String msg3 = GlobalStdEnv.get("kkk");
		Consoles.println(msg3);

		GlobalStdEnv.set("kkk", "${user.home} | ${java.home} | ${test.home:-${test.home2:-/a/b/c}}");
		String msg2 = GlobalStdEnv.get("kkk");
		Consoles.println(msg2);
		GlobalStdEnv.set("test.home2", "/test/home2");
		String msg1 = GlobalStdEnv.get("kkk");
		Consoles.println(msg1);
		GlobalStdEnv.set("test.home", "/test/home");
		String msg = GlobalStdEnv.get("kkk");
		Consoles.println(msg);
	}


	@Test
	void test06() {
		GlobalStdEnv.addEnvFirst(Env.delegate(GlobalStdEnv.env()));
		Object[] args1 = new Object[]{GlobalStdEnv.get("kkk")};
		Consoles.println("key: {}", args1);
		Object[] args = new Object[]{GlobalStdEnv.keys()};
		Consoles.println("keys: {}", args);
	}

	@Test
	@Order(7)
	void test07() {
		GlobalStdEnv.set("aaa","${bbb}");
		GlobalStdEnv.set("bbb","${ccc}");
		GlobalStdEnv.set("ccc","${aaa}");
		String msg3 = GlobalStdEnv.resolveRef("${sys['test']}");
		Consoles.println(msg3);
		String msg2 = GlobalStdEnv.resolveRef("test:  ${java.home} | ${test.home:-${test.home2:-/a/b/c}}");
		Consoles.println(msg2);
		String msg1 = GlobalStdEnv.resolveRef("test: ${unknown.key} ${aaa} ${user.home} | ${java.home} | ${test.home:-${test.home2:-/a/b/c}}");
		Consoles.println(msg1);
		String msg = GlobalStdEnv.resolveRef("test: ${unknown.key} ${aaa} ${user.home} | ${java.home} | ${test.home:${test.home2:/a/b/c}}");
		Consoles.println(msg);
	}
}
