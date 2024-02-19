package io.polaris.core.codec;

import io.polaris.core.TestConsole;
import io.polaris.core.random.Randoms;
import io.polaris.core.string.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CodecTest {

	@Test
	void test01() {
		byte[] data = Randoms.randomBytes(20);
		String hex = Hex.formatBytes(data);
		TestConsole.printx("raw: {}", new String(data));
		TestConsole.printx("encode: {}", hex);
		TestConsole.printx("encode: {}", HexCodec.encodeToString(data));
		TestConsole.printx("encode: {}", new String(HexCodec.encode(data)));
		TestConsole.printx("decode: {}", new String(HexCodec.decode(HexCodec.encodeToString(data))));

		Assertions.assertEquals(hex.toUpperCase(), HexCodec.encodeToString(data).toUpperCase());
		Assertions.assertEquals(hex.toUpperCase(), new String(HexCodec.encode(data)).toUpperCase());
		Assertions.assertArrayEquals(data, HexCodec.decode(HexCodec.encodeToString(data)));
	}

	@Test
	void test02() {
		byte[] data = Randoms.randomBytes(20);
		String base64 = Base64.encodeToString(data);

		TestConsole.printx("raw: {}", new String(data));
		TestConsole.printx("encode: {}", base64);
		TestConsole.printx("encode: {}", new String(Base64.encode(data)));
		TestConsole.printx("decode: {}", new String(Base64.decode(base64)));
		TestConsole.printx("encode: {}", java.util.Base64.getEncoder().encodeToString(data));
		TestConsole.printx("encode: {}", new String(java.util.Base64.getEncoder().encode(data)));
		TestConsole.printx("decode: {}", new String(java.util.Base64.getDecoder().decode(base64)));

		Assertions.assertEquals(base64, new String(Base64.encode(data)));
		Assertions.assertArrayEquals(data, Base64.decode(base64));
		Assertions.assertEquals(base64, java.util.Base64.getEncoder().encodeToString(data));
		Assertions.assertEquals(base64, new String(java.util.Base64.getEncoder().encode(data)));
		Assertions.assertArrayEquals(data, java.util.Base64.getDecoder().decode(base64));

	}

	@Test
	void test03() {
		byte[] data = Randoms.randomBytes(20);
		String base32 = Base32.encodeToString(data);

		TestConsole.printx("raw: {}", new String(data));
		TestConsole.printx("encode: {}", base32);
		TestConsole.printx("encode: {}", new String(Base32.encode(data)));
		TestConsole.printx("decode: {}", new String(Base32.decode(base32)));
		Assertions.assertEquals(base32, new String(Base32.encode(data)));
		Assertions.assertArrayEquals(data, Base32.decode(base32));
	}
}
