package io.polaris.core.net;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Test;

class NetsTest {

	@Test
	void test01() {
		TestConsole.println(Nets.getRandomLocalPort());
	}

	@Test
	void test02() {
		TestConsole.println(Nets.getRandomLocalPort(1024, 2048));
	}

	@Test
	void test03() {
		TestConsole.println(Nets.getUsableLocalPort(1024, 2048));
	}
}
