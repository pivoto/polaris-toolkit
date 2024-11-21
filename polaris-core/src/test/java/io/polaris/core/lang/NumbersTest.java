package io.polaris.core.lang;

import io.polaris.core.io.Consoles;
import io.polaris.core.string.Strings;
import org.junit.jupiter.api.Test;

class NumbersTest {

	@Test
	void test01() {
		Consoles.println(10 / -3);
		Consoles.println(Strings.repeat('=', 72));
		Consoles.println(Math.floorDiv(10, 3));
		Consoles.println(Math.floorDiv(10, -3));
		Consoles.println(Math.floorDiv(-10, -3));
		Consoles.println(Math.floorDiv(-10, 3));
		Consoles.println(Strings.repeat('=', 72));
		Consoles.println(Numbers.ceilDiv(10, 3));
		Consoles.println(Numbers.ceilDiv(10, -3));
		Consoles.println(Numbers.ceilDiv(-10, -3));
		Consoles.println(Numbers.ceilDiv(-10, 3));
		Consoles.println(Strings.repeat('=', 72));
		Consoles.println(Math.ceil((double) 10 / 3));
		Consoles.println(Math.ceil((double) 10 / -3));
		Consoles.println(Math.ceil((double) -10 / -3));
		Consoles.println(Math.ceil((double) -10 / 3));
	}
}
