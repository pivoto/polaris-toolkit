package io.polaris.core.string;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnicodesTest {

	@Test
	void test01() {
		String s = "abc测试";
		String t = Unicodes.toUnicode(s);
		TestConsole.printx(t);
		TestConsole.printx(Unicodes.toUnicode(t));
		Assertions.assertEquals(t, Unicodes.toUnicode(t));
		Assertions.assertEquals(s, Unicodes.fromUnicode(t));
	}
}
