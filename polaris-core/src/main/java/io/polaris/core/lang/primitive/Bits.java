package io.polaris.core.lang.primitive;

import java.nio.ByteBuffer;

/**
 * see java.nio.Bits
 *
 * @author Qt
 * @since  Aug 03, 2023
 */
public class Bits {

	static short reverse(short x) {
		return Short.reverseBytes(x);
	}

	static char reverse(char x) {
		return Character.reverseBytes(x);
	}

	static int reverse(int x) {
		return Integer.reverseBytes(x);
	}

	static long reverse(long x) {
		return Long.reverseBytes(x);
	}


	// -- get/put char --

	static private char makeChar(byte b1, byte b0) {
		return (char) ((b1 << 8) | (b0 & 0xff));
	}

	static char getCharL(ByteBuffer bb, int bi) {
		return makeChar(bb.get(bi + 1), bb.get(bi));
	}


	static char getCharB(ByteBuffer bb, int bi) {
		return makeChar(bb.get(bi),
			bb.get(bi + 1));
	}


	static char getChar(ByteBuffer bb, int bi, boolean bigEndian) {
		return bigEndian ? getCharB(bb, bi) : getCharL(bb, bi);
	}


	private static byte char1(char x) {
		return (byte) (x >> 8);
	}

	private static byte char0(char x) {
		return (byte) (x);
	}

	static void putCharL(ByteBuffer bb, int bi, char x) {
		bb.put(bi, char0(x));
		bb.put(bi + 1, char1(x));
	}


	static void putCharB(ByteBuffer bb, int bi, char x) {
		bb.put(bi, char1(x));
		bb.put(bi + 1, char0(x));
	}


	static void putChar(ByteBuffer bb, int bi, char x, boolean bigEndian) {
		if (bigEndian) {
			putCharB(bb, bi, x);
		} else {
			putCharL(bb, bi, x);
		}
	}


	// -- get/put short --

	static private short makeShort(byte b1, byte b0) {
		return (short) ((b1 << 8) | (b0 & 0xff));
	}

	static short getShortL(ByteBuffer bb, int bi) {
		return makeShort(bb.get(bi + 1),
			bb.get(bi));
	}


	static short getShortB(ByteBuffer bb, int bi) {
		return makeShort(bb.get(bi),
			bb.get(bi + 1));
	}


	static short getShort(ByteBuffer bb, int bi, boolean bigEndian) {
		return bigEndian ? getShortB(bb, bi) : getShortL(bb, bi);
	}


	private static byte short1(short x) {
		return (byte) (x >> 8);
	}

	private static byte short0(short x) {
		return (byte) (x);
	}

	static void putShortL(ByteBuffer bb, int bi, short x) {
		bb.put(bi, short0(x));
		bb.put(bi + 1, short1(x));
	}


	static void putShortB(ByteBuffer bb, int bi, short x) {
		bb.put(bi, short1(x));
		bb.put(bi + 1, short0(x));
	}


	static void putShort(ByteBuffer bb, int bi, short x, boolean bigEndian) {
		if (bigEndian) {
			putShortB(bb, bi, x);
		} else {
			putShortL(bb, bi, x);
		}
	}


	// -- get/put int --

	static private int makeInt(byte b3, byte b2, byte b1, byte b0) {
		return (((b3) << 24) |
			((b2 & 0xff) << 16) |
			((b1 & 0xff) << 8) |
			((b0 & 0xff)));
	}

	static int getIntL(ByteBuffer bb, int bi) {
		return makeInt(bb.get(bi + 3),
			bb.get(bi + 2),
			bb.get(bi + 1),
			bb.get(bi));
	}


	static int getIntB(ByteBuffer bb, int bi) {
		return makeInt(bb.get(bi),
			bb.get(bi + 1),
			bb.get(bi + 2),
			bb.get(bi + 3));
	}


	static int getInt(ByteBuffer bb, int bi, boolean bigEndian) {
		return bigEndian ? getIntB(bb, bi) : getIntL(bb, bi);
	}


	private static byte int3(int x) {
		return (byte) (x >> 24);
	}

	private static byte int2(int x) {
		return (byte) (x >> 16);
	}

	private static byte int1(int x) {
		return (byte) (x >> 8);
	}

	private static byte int0(int x) {
		return (byte) (x);
	}

	static void putIntL(ByteBuffer bb, int bi, int x) {
		bb.put(bi + 3, int3(x));
		bb.put(bi + 2, int2(x));
		bb.put(bi + 1, int1(x));
		bb.put(bi, int0(x));
	}


	static void putIntB(ByteBuffer bb, int bi, int x) {
		bb.put(bi, int3(x));
		bb.put(bi + 1, int2(x));
		bb.put(bi + 2, int1(x));
		bb.put(bi + 3, int0(x));
	}


	static void putInt(ByteBuffer bb, int bi, int x, boolean bigEndian) {
		if (bigEndian) {
			putIntB(bb, bi, x);
		} else {
			putIntL(bb, bi, x);
		}
	}


	// -- get/put long --

	static private long makeLong(byte b7, byte b6, byte b5, byte b4,
								 byte b3, byte b2, byte b1, byte b0) {
		return ((((long) b7) << 56) |
			(((long) b6 & 0xff) << 48) |
			(((long) b5 & 0xff) << 40) |
			(((long) b4 & 0xff) << 32) |
			(((long) b3 & 0xff) << 24) |
			(((long) b2 & 0xff) << 16) |
			(((long) b1 & 0xff) << 8) |
			(((long) b0 & 0xff)));
	}

	static long getLongL(ByteBuffer bb, int bi) {
		return makeLong(bb.get(bi + 7),
			bb.get(bi + 6),
			bb.get(bi + 5),
			bb.get(bi + 4),
			bb.get(bi + 3),
			bb.get(bi + 2),
			bb.get(bi + 1),
			bb.get(bi));
	}

	static long getLongB(ByteBuffer bb, int bi) {
		return makeLong(bb.get(bi),
			bb.get(bi + 1),
			bb.get(bi + 2),
			bb.get(bi + 3),
			bb.get(bi + 4),
			bb.get(bi + 5),
			bb.get(bi + 6),
			bb.get(bi + 7));
	}

	static long getLong(ByteBuffer bb, int bi, boolean bigEndian) {
		return bigEndian ? getLongB(bb, bi) : getLongL(bb, bi);
	}

	private static byte long7(long x) {
		return (byte) (x >> 56);
	}

	private static byte long6(long x) {
		return (byte) (x >> 48);
	}

	private static byte long5(long x) {
		return (byte) (x >> 40);
	}

	private static byte long4(long x) {
		return (byte) (x >> 32);
	}

	private static byte long3(long x) {
		return (byte) (x >> 24);
	}

	private static byte long2(long x) {
		return (byte) (x >> 16);
	}

	private static byte long1(long x) {
		return (byte) (x >> 8);
	}

	private static byte long0(long x) {
		return (byte) (x);
	}

	static void putLongL(ByteBuffer bb, int bi, long x) {
		bb.put(bi + 7, long7(x));
		bb.put(bi + 6, long6(x));
		bb.put(bi + 5, long5(x));
		bb.put(bi + 4, long4(x));
		bb.put(bi + 3, long3(x));
		bb.put(bi + 2, long2(x));
		bb.put(bi + 1, long1(x));
		bb.put(bi, long0(x));
	}


	static void putLongB(ByteBuffer bb, int bi, long x) {
		bb.put(bi, long7(x));
		bb.put(bi + 1, long6(x));
		bb.put(bi + 2, long5(x));
		bb.put(bi + 3, long4(x));
		bb.put(bi + 4, long3(x));
		bb.put(bi + 5, long2(x));
		bb.put(bi + 6, long1(x));
		bb.put(bi + 7, long0(x));
	}


	static void putLong(ByteBuffer bb, int bi, long x, boolean bigEndian) {
		if (bigEndian) {
			putLongB(bb, bi, x);
		} else {
			putLongL(bb, bi, x);
		}
	}


	// -- get/put float --

	static float getFloatL(ByteBuffer bb, int bi) {
		return Float.intBitsToFloat(getIntL(bb, bi));
	}


	static float getFloatB(ByteBuffer bb, int bi) {
		return Float.intBitsToFloat(getIntB(bb, bi));
	}


	static float getFloat(ByteBuffer bb, int bi, boolean bigEndian) {
		return bigEndian ? getFloatB(bb, bi) : getFloatL(bb, bi);
	}


	static void putFloatL(ByteBuffer bb, int bi, float x) {
		putIntL(bb, bi, Float.floatToRawIntBits(x));
	}


	static void putFloatB(ByteBuffer bb, int bi, float x) {
		putIntB(bb, bi, Float.floatToRawIntBits(x));
	}


	static void putFloat(ByteBuffer bb, int bi, float x, boolean bigEndian) {
		if (bigEndian) {
			putFloatB(bb, bi, x);
		} else {
			putFloatL(bb, bi, x);
		}
	}


	// -- get/put double --

	static double getDoubleL(ByteBuffer bb, int bi) {
		return Double.longBitsToDouble(getLongL(bb, bi));
	}


	static double getDoubleB(ByteBuffer bb, int bi) {
		return Double.longBitsToDouble(getLongB(bb, bi));
	}


	static double getDouble(ByteBuffer bb, int bi, boolean bigEndian) {
		return bigEndian ? getDoubleB(bb, bi) : getDoubleL(bb, bi);
	}


	static void putDoubleL(ByteBuffer bb, int bi, double x) {
		putLongL(bb, bi, Double.doubleToRawLongBits(x));
	}

	static void putDoubleB(ByteBuffer bb, int bi, double x) {
		putLongB(bb, bi, Double.doubleToRawLongBits(x));
	}

	static void putDouble(ByteBuffer bb, int bi, double x, boolean bigEndian) {
		if (bigEndian) {
			putDoubleB(bb, bi, x);
		} else {
			putDoubleL(bb, bi, x);
		}
	}


}
