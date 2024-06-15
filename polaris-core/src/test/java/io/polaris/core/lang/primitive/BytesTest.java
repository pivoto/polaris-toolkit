package io.polaris.core.lang.primitive;

import io.polaris.core.TestConsole;
import io.polaris.core.string.Hex;
import org.junit.jupiter.api.Test;

import java.nio.ByteOrder;

/**
 * @author Qt
 * @since  Aug 04, 2023
 */
public class BytesTest {

	@Test
	void test01() {
		int i = 0x01020304;
		TestConsole.println(Hex.formatHex(Bytes.intToByte(i)));
		TestConsole.println(Hex.formatHex(Bytes.byteToUnsignedInt((byte) i)));

		TestConsole.println(Hex.formatHex(Bytes.bytesToShort(new byte[]{0x01, 0x02})));
		TestConsole.println(Hex.formatHex(Bytes.bytesToShort(new byte[]{0x01, 0x02}, ByteOrder.nativeOrder())));
		TestConsole.println(Hex.formatHex(Bytes.bytesToInt(new byte[]{0x01, 0x02, 0x3, 0x4})));
	}
}
