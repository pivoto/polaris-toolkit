package io.polaris.core.lang.primitive;

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
		System.out.println(Hex.formatHex(l));

		buffer.flip();
		buffer.order(ByteOrder.nativeOrder());
		buffer.putLong(0x0102030405060708L);
		buffer.flip();
		System.out.println(Hex.formatBytes(buffer.array()));

		System.out.println(Hex.formatHex(Bytes.bytesToLong(new byte[]{1, 2, 3, 4, 5, 6, 7, 8})));
		System.out.println(Bytes.CPU_ENDIAN);
		System.out.println(ByteOrder.nativeOrder());
	}

	@Test
	void test02() {
		ByteBuffer buf = ByteBuffer.allocate(16);
		buf.putInt(0x12345678);
		buf.flip();
		byte[] bytes = new byte[4];
		buf.get(bytes);
		System.out.println(Hex.formatBytes(bytes));

		System.out.println(Hex.formatHex(Bits.getIntB(buf, 0)));
		System.out.println(Hex.formatHex(Bits.getIntL(buf, 0)));
	}


}
