package io.polaris.core.string;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UnicodesTest {

	@Test
	void test01() {
		String s = "abc测试";
		String t = Unicodes.toUnicode(s);
		Consoles.log(t);
		String msg = Unicodes.toUnicode(t);
		Consoles.log(msg);
		Assertions.assertEquals(t, Unicodes.toUnicode(t));
		Assertions.assertEquals(s, Unicodes.fromUnicode(t));
	}
}
