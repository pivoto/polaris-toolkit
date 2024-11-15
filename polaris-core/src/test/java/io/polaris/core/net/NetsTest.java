package io.polaris.core.net;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

class NetsTest {

	@Test
	void test01() {
		Object[] args = new Object[]{Nets.getRandomLocalPort()};
		Consoles.println(args);
	}

	@Test
	void test02() {
		Object[] args = new Object[]{Nets.getRandomLocalPort(1024, 2048)};
		Consoles.println(args);
	}

	@Test
	void test03() {
		Object[] args = new Object[]{Nets.getUsableLocalPort(1024, 2048)};
		Consoles.println(args);
	}
}
