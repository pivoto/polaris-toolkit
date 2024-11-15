package io.polaris.core.os;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

class OSTest {

	static {
		System.setProperty(OS.KEY_IP_REGEX,"192\\.,172\\.");
	}

	@Test
	void test1() {
		Object[] args = new Object[]{OS.getAllIps()};
		Consoles.log("", args);
		String msg = OS.getIp();
		Consoles.log(msg);
	}
}
