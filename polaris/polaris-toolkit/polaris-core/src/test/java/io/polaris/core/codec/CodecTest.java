package io.polaris.core.codec;

import io.polaris.core.random.Randoms;
import org.junit.jupiter.api.Test;

class CodecTest {

	@Test
	void test01() {
		byte[] data = Randoms.randomBytes(20);
		System.out.printf("encode: %s\n", Hex.encodeToString(data));
		System.out.printf("encode: %s\n", new String(Hex.encode(data)));
		System.out.printf("decode: %s\n", new String(Hex.decode(Hex.encodeToString(data))));
	}

	@Test
	void test02() {
		byte[] data = Randoms.randomBytes(20);
		System.out.printf("encode: %s\n", Base64.encodeToString(data));
		System.out.printf("encode: %s\n", new String(Base64.encode(data)));
		System.out.printf("decode: %s\n", new String(Base64.decode(Base64.encodeToString(data))));
		System.out.printf("encode: %s\n", java.util.Base64.getEncoder().encodeToString(data));
		System.out.printf("encode: %s\n", new String(java.util.Base64.getEncoder().encode(data)));
		System.out.printf("decode: %s\n", new String(java.util.Base64.getDecoder().decode(Base64.encodeToString(data))));
	}

	@Test
	void test03() {
		byte[] data = Randoms.randomBytes(20);
		System.out.printf("encode: %s\n", Base32.encodeToString(data));
		System.out.printf("encode: %s\n", new String(Base32.encode(data)));
		System.out.printf("decode: %s\n", new String(Base32.decode(Base32.encodeToString(data))));
	}
}
