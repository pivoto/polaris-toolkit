package io.polaris.core.string;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class HexTest {

	@Test
	void test01() {
		long r = ThreadLocalRandom.current().nextLong();
		TestConsole.println(r);
		TestConsole.println(Hex.formatBin(r, 64));
		TestConsole.println(Hex.formatOct(r, 64));
		TestConsole.println(Hex.formatHex(r, 64));
		TestConsole.println(Hex.format32Radix(r, 64));

		byte[] bytes = new byte[10];
		ThreadLocalRandom.current().nextBytes(bytes);

		TestConsole.println(Arrays.toString(bytes));
		TestConsole.println(Hex.formatBytes(bytes));
		TestConsole.println(Arrays.toString(Hex.parseBytes(Hex.formatBytes(bytes))));

		Assertions.assertArrayEquals(bytes, Hex.parseBytes(Hex.formatBytes(bytes)));
	}
}
