package io.polaris.core.string;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

class HexTest {

	@Test
	void test01() {
		long r = ThreadLocalRandom.current().nextLong();
		Consoles.println(r);
		Consoles.println(Hex.formatBin(r, 64));
		Consoles.println(Hex.formatOct(r, 64));
		Consoles.println(Hex.formatHex(r, 64));
		Consoles.println(Hex.format32Radix(r, 64));

		byte[] bytes = new byte[10];
		ThreadLocalRandom.current().nextBytes(bytes);

		Consoles.println(Arrays.toString(bytes));
		String msg1 = Hex.formatBytes(bytes);
		Consoles.println(msg1);
		String msg = Arrays.toString(Hex.parseBytes(Hex.formatBytes(bytes)));
		Consoles.println(msg);

		Assertions.assertArrayEquals(bytes, Hex.parseBytes(Hex.formatBytes(bytes)));
	}
}
