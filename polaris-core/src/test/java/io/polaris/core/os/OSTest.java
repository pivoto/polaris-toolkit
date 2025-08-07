package io.polaris.core.os;

import io.polaris.core.consts.StdKeys;
import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

class OSTest {

	static {
		System.setProperty(StdKeys.IP_REGEX,"192\\.,172\\.");
	}

	@Test
	void test1() {
		Object[] args = new Object[]{OS.getAllIps()};
		Consoles.log("", args);
		String msg = OS.getIp();
		Consoles.log(msg);
	}
}
