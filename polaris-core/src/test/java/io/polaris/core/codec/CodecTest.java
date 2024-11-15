package io.polaris.core.codec;

import io.polaris.core.io.Consoles;
import io.polaris.core.random.Randoms;
import io.polaris.core.string.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CodecTest {

	@Test
	void test01() {
		byte[] data = Randoms.randomBytes(20);
		String hex = Hex.formatBytes(data);
		Consoles.log("raw: {}", new String(data));
		Consoles.log("encode: {}", hex);
		Object[] args2 = new Object[]{HexCodec.encodeToString(data)};
		Consoles.log("encode: {}", args2);
		Object[] args1 = new Object[]{new String(HexCodec.encode(data))};
		Consoles.log("encode: {}", args1);
		Object[] args = new Object[]{new String(HexCodec.decode(HexCodec.encodeToString(data)))};
		Consoles.log("decode: {}", args);

		Assertions.assertEquals(hex.toUpperCase(), HexCodec.encodeToString(data).toUpperCase());
		Assertions.assertEquals(hex.toUpperCase(), new String(HexCodec.encode(data)).toUpperCase());
		Assertions.assertArrayEquals(data, HexCodec.decode(HexCodec.encodeToString(data)));
	}

	@Test
	void test02() {
		byte[] data = Randoms.randomBytes(20);
		String base64 = Base64.encodeToString(data);

		Consoles.log("raw: {}", new String(data));
		Consoles.log("encode: {}", base64);
		Object[] args4 = new Object[]{new String(Base64.encode(data))};
		Consoles.log("encode: {}", args4);
		Object[] args3 = new Object[]{new String(Base64.decode(base64))};
		Consoles.log("decode: {}", args3);
		Object[] args2 = new Object[]{java.util.Base64.getEncoder().encodeToString(data)};
		Consoles.log("encode: {}", args2);
		Object[] args1 = new Object[]{new String(java.util.Base64.getEncoder().encode(data))};
		Consoles.log("encode: {}", args1);
		Object[] args = new Object[]{new String(java.util.Base64.getDecoder().decode(base64))};
		Consoles.log("decode: {}", args);

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

		Consoles.log("raw: {}", new String(data));
		Consoles.log("encode: {}", base32);
		Object[] args1 = new Object[]{new String(Base32.encode(data))};
		Consoles.log("encode: {}", args1);
		Object[] args = new Object[]{new String(Base32.decode(base32))};
		Consoles.log("decode: {}", args);
		Assertions.assertEquals(base32, new String(Base32.encode(data)));
		Assertions.assertArrayEquals(data, Base32.decode(base32));
	}
}
