package io.polaris.core.lang.primitive;

import io.polaris.core.TestConsole;
import io.polaris.core.string.Hex;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class BitsTest {

	@Test
	void test01() {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

		buffer.put(new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
		buffer.flip();
		long l = buffer.getLong();
		TestConsole.println(Hex.formatHex(l));

		buffer.flip();
		buffer.order(ByteOrder.nativeOrder());
		buffer.putLong(0x0102030405060708L);
		buffer.flip();
		TestConsole.println(Hex.formatBytes(buffer.array()));

		TestConsole.println(Hex.formatHex(Bytes.bytesToLong(new byte[]{1, 2, 3, 4, 5, 6, 7, 8})));
		TestConsole.println(Bytes.CPU_ENDIAN);
		TestConsole.println(ByteOrder.nativeOrder());
	}

	@Test
	void test02() {
		ByteBuffer buf = ByteBuffer.allocate(16);
		buf.putInt(0x12345678);
		buf.flip();
		byte[] bytes = new byte[4];
		buf.get(bytes);
		TestConsole.println(Hex.formatBytes(bytes));

		TestConsole.println(Hex.formatHex(Bits.getIntB(buf, 0)));
		TestConsole.println(Hex.formatHex(Bits.getIntL(buf, 0)));
	}


}
