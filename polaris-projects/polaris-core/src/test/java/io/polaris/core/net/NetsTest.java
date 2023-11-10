package io.polaris.core.net;

import org.junit.jupiter.api.Test;

class NetsTest {

	@Test
	void test01() {
		System.out.println(Nets.getRandomLocalPort());
	}

	@Test
	void test02() {
		System.out.println(Nets.getRandomLocalPort(1024, 2048));
	}

	@Test
	void test03() {
		System.out.println(Nets.getUsableLocalPort(1024, 2048));
	}
}
