package io.polaris.core.codec;

import java.util.stream.Stream;

import io.polaris.core.io.Consoles;
import io.polaris.core.random.Randoms;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TestCodec2 {

	public static Stream<Arguments> test_codec_args() {
		return Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)
			.map(n -> Randoms.randomChineseString(n))
			.map(Arguments::of)
			;
	}

	@ParameterizedTest
	@MethodSource("test_codec_args")
	void test_codec(String raw) {
		byte[] data = raw.getBytes();

		System.out.println("raw: " + raw);

		System.out.println("encodeBase16: " + Base16.encodeToString(data));
		System.out.println("encodeBase16: " + new String(Codec.encodeBase16(data)));
		System.out.println("decodeBase16: " + new String(Codec.decodeBase16(Codec.encodeBase16(data))));

		System.out.println("encodeCrockfordBase32: " + CrockfordBase32.encodeToString(data));
		System.out.println("encodeCrockfordBase32: " + new String(Codec.encodeCrockfordBase32(data)));
		System.out.println("decodeCrockfordBase32: " + new String(Codec.decodeCrockfordBase32(Codec.encodeCrockfordBase32(data))));

		System.out.println("encodeBase32: " + Base32.encodeToString(data));
		System.out.println("encodeBase32: " + new String(Codec.encodeBase32(data)));
		System.out.println("decodeBase32: " + new String(Codec.decodeBase32(Codec.encodeBase32(data))));

		System.out.println("encodeBase64: " + Base64.encodeToString(data));
		System.out.println("encodeBase64: " + new String(Codec.encodeBase64(data)));
		System.out.println("decodeBase64: " + new String(Codec.decodeBase64(Codec.encodeBase64(data))));

		Consoles.println();
	}
}
