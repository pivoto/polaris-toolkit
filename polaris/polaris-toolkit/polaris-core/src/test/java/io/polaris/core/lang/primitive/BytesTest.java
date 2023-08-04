package io.polaris.core.lang.primitive;

import io.polaris.core.string.Hex;
import org.junit.jupiter.api.Test;

import java.nio.ByteOrder;

/**
 * @author Qt
 * @since 1.8,  Aug 04, 2023
 */
public class BytesTest {

	@Test
	void test01() {
		int i = 0x01020304;
		System.out.println(Hex.formatHex(Bytes.intToByte(i)));
		System.out.println(Hex.formatHex(Bytes.byteToUnsignedInt((byte) i)));

		System.out.println(Hex.formatHex(Bytes.bytesToShort(new byte[]{0x01, 0x02})));
		System.out.println(Hex.formatHex(Bytes.bytesToShort(new byte[]{0x01, 0x02}, ByteOrder.nativeOrder())));
		System.out.println(Hex.formatHex(Bytes.bytesToInt(new byte[]{0x01, 0x02, 0x3, 0x4})));
	}
}
