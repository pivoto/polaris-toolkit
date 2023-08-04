package io.polaris.core.string;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

class HexCodecTest {

	@Test
	void test01() {
		long r = new Random().nextLong();
		System.out.println(r);
		System.out.println(Hex.formatBin(r, 64));
		System.out.println(Hex.formatOct(r, 64));
		System.out.println(Hex.formatHex(r, 64));
		System.out.println(Hex.format32Radix(r, 64));

		byte[] bytes = new byte[10];
		new Random().nextBytes(bytes);

		System.out.println(Arrays.toString(bytes));
		System.out.println(Hex.formatBytes(bytes));
		System.out.println(Arrays.toString(Hex.parseBytes(Hex.formatBytes(bytes))));
	}
}
