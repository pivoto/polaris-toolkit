package io.polaris.core.os;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Test;

class OSTest {

	static {
		System.setProperty(OS.KEY_IP_REGEX,"192\\.,172\\.");
	}

	@Test
	void test1() {
		TestConsole.printx(OS.getAllIps());
		TestConsole.printx(OS.getIp());
	}
}
