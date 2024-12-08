package io.polaris.core.lang.primitive;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

import javax.annotation.Nonnull;

import io.polaris.core.codec.Base32;
import io.polaris.core.consts.SystemKeys;
import io.polaris.core.lang.Numbers;
import io.polaris.core.random.Randoms;
import io.polaris.core.string.Hex;

/**
 * 字节操作工具类
 * <p>
 * <p>
 * 可对数字和字节进行转换。 假设数据存储是以大端模式存储的：<br>
 * <ul>
 *     <li>byte: 字节类型 占8位二进制 00000000</li>
 *     <li>char: 字符类型 占2个字节 16位二进制 byte[0] byte[1]</li>
 *     <li>int : 整数类型 占4个字节 32位二进制 byte[0] byte[1] byte[2] byte[3]</li>
 *     <li>long: 长整数类型 占8个字节 64位二进制 byte[0] byte[1] byte[2] byte[3] byte[4] byte[5]</li>
 *     <li>long: 长整数类型 占8个字节 64位二进制 byte[0] byte[1] byte[2] byte[3] byte[4] byte[5] byte[6] byte[7]</li>
 *     <li>float: 浮点数(小数) 占4个字节 32位二进制 byte[0] byte[1] byte[2] byte[3]</li>
 *     <li>double: 双精度浮点数(小数) 占8个字节 64位二进制 byte[0] byte[1] byte[2] byte[3] byte[4]byte[5] byte[6] byte[7]</li>
 * </ul>
 *
 * @author Qt
 * @since 1.8
 */
public class Bytes {
	public static final ByteOrder DEFAULT_ORDER = ByteOrder.BIG_ENDIAN;
	/** CPU的字节序 */
	public static final ByteOrder CPU_ENDIAN;

	static {
		ByteOrder byteOrder;
		try {
			byteOrder = ByteOrder.nativeOrder();
		} catch (Throwable e) {
			byteOrder = "little".equals(System.getProperty(SystemKeys.SUN_CPU_ENDIAN)) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
		}
		CPU_ENDIAN = byteOrder;
	}

	// region 字符串转换

	public static String toHexString(byte[] bytes) {
		return Hex.formatBytes(bytes);
	}

	public static byte[] parseHexBytes(String text) {
		return Hex.parseBytes(text);
	}

	public static String toBase32String(byte[] bytes) {
		return Base32.encodeToString(bytes);
	}

	public static byte[] parseBase32Bytes(String text) {
		return Base32.decode(text);
	}

	public static String toBase64String(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	public static byte[] parseBase64Bytes(String text) {
		return Base64.getDecoder().decode(text);
	}


	public static String toBinString(byte num) {
		return Hex.formatBin(num);
	}

	public static String toOctString(byte num) {
		return Hex.formatOct(num);
	}

	public static String toHexString(byte num) {
		return Hex.formatHex(num);
	}

	public static byte parseBin(String text) {
		return Hex.parseBinAsByte(text);
	}

	public static byte parseOct(String text) {
		return Hex.parseOctAsByte(text);
	}

	public static byte parseHex(String text) {
		return Hex.parseHexAsByte(text);
	}

	public static byte parse(String text, int radix) {
		return Byte.parseByte(text, radix);
	}

	// endregion


	// region 数组操作

	/**
	 * 生成一个新的重新设置大小的数组<br>
	 * 调整大小后拷贝原数组到新数组下。扩大则占位前N个位置，其它位置补充0，缩小则截断
	 *
	 * @param bytes   原数组
	 * @param newSize 新的数组大小
	 * @return 调整后的新数组
	 */
	public static byte[] resize(byte[] bytes, int newSize) {
		if (newSize < 0) {
			return bytes;
		}
		final byte[] newArray = new byte[newSize];
		if (newSize > 0 && isNotEmpty(bytes)) {
			System.arraycopy(bytes, 0, newArray, 0, Math.min(bytes.length, newSize));
		}
		return newArray;
	}

	/**
	 * 将多个数组合并在一起<br>
	 * 忽略null的数组
	 *
	 * @param arrays 数组集合
	 * @return 合并后的数组
	 */
	public static byte[] join(byte[]... arrays) {
		if (arrays.length == 1) {
			return arrays[0];
		}

		// 计算总长度
		int length = 0;
		for (byte[] array : arrays) {
			if (isNotEmpty(array)) {
				length += array.length;
			}
		}

		final byte[] result = new byte[length];
		length = 0;
		for (byte[] array : arrays) {
			if (isNotEmpty(array)) {
				System.arraycopy(array, 0, result, length, array.length);
				length += array.length;
			}
		}
		return result;
	}

	/**
	 * 拆分byte数组为几个等份（最后一份按照剩余长度分配空间）
	 *
	 * @param array 数组
	 * @param len   每个小节的长度
	 * @return 拆分后的数组
	 */
	public static byte[][] split(byte[] array, int len) {
		int amount = array.length / len;
		final int remainder = array.length % len;
		if (remainder != 0) {
			++amount;
		}
		final byte[][] arrays = new byte[amount][];
		byte[] arr;
		for (int i = 0; i < amount; i++) {
			if (i == amount - 1 && remainder != 0) {
				// 有剩余，按照实际长度创建
				arr = new byte[remainder];
				System.arraycopy(array, i * len, arr, 0, remainder);
			} else {
				arr = new byte[len];
				System.arraycopy(array, i * len, arr, 0, len);
			}
			arrays[i] = arr;
		}
		return arrays;
	}

	/**
	 * 返回数组中指定元素所在位置，未找到返回 -1
	 *
	 * @param array 数组
	 * @param value 被检查的元素
	 * @return 数组中指定元素所在位置，未找到返回 -1
	 */

	/**
	 * 将原始类型数组包装为包装类型
	 *
	 * @param values 原始类型数组
	 * @return 包装类型数组
	 */
	public static Byte[] wrap(byte... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new Byte[0];
		}

		final Byte[] array = new Byte[length];
		for (int i = 0; i < length; i++) {
			array[i] = values[i];
		}
		return array;
	}

	/**
	 * 包装类数组转为原始类型数组
	 *
	 * @param values 包装类型数组
	 * @return 原始类型数组
	 */
	public static byte[] unwrap(Byte... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new byte[0];
		}

		final byte[] array = new byte[length];
		for (int i = 0; i < length; i++) {
			array[i] = Optional.ofNullable(values[i]).orElse((byte) 0);
		}
		return array;
	}

	/**
	 * 获取子数组
	 *
	 * @param array 数组
	 * @param start 开始位置（包括）
	 * @param end   结束位置（不包括）
	 * @return 新的数组
	 * @see Arrays#copyOfRange(Object[], int, int)
	 */
	public static byte[] sub(byte[] array, int start, int end) {
		int length = array.length;
		if (start < 0) {
			start += length;
		}
		if (end < 0) {
			end += length;
		}
		if (start == length) {
			return new byte[0];
		}
		if (start > end) {
			int tmp = start;
			start = end;
			end = tmp;
		}
		if (end > length) {
			if (start >= length) {
				return new byte[0];
			}
			end = length;
		}
		return Arrays.copyOfRange(array, start, end);
	}

	/**
	 * 移除数组中对应位置的元素<br>
	 * copy from commons-lang
	 *
	 * @param array 数组对象，可以是对象数组，也可以原始类型数组
	 * @param index 位置，如果位置小于0或者大于长度，返回原数组
	 * @return 去掉指定元素后的新数组或原数组
	 * @throws IllegalArgumentException 参数对象不为数组对象
	 */
	public static byte[] remove(byte[] array, int index) throws IllegalArgumentException {
		if (null == array) {
			return null;
		}
		int length = array.length;
		if (index < 0 || index >= length) {
			return array;
		}

		final byte[] result = new byte[length - 1];
		System.arraycopy(array, 0, result, 0, index);
		if (index < length - 1) {
			// 后半部分
			System.arraycopy(array, index + 1, result, index, length - index - 1);
		}
		return result;
	}

	/**
	 * 移除数组中指定的元素<br>
	 * 只会移除匹配到的第一个元素 copy from commons-lang
	 *
	 * @param array   数组对象，可以是对象数组，也可以原始类型数组
	 * @param element 要移除的元素
	 * @return 去掉指定元素后的新数组或原数组
	 * @throws IllegalArgumentException 参数对象不为数组对象
	 */
	public static byte[] removeElement(byte[] array, byte element) throws IllegalArgumentException {
		return remove(array, indexOf(array, element));
	}

	/**
	 * 反转数组，会变更原数组
	 *
	 * @param array               数组，会变更
	 * @param startIndexInclusive 起始位置（包含）
	 * @param endIndexExclusive   结束位置（不包含）
	 * @return 变更后的原数组
	 */
	public static byte[] reverse(byte[] array, final int startIndexInclusive, final int endIndexExclusive) {
		if (isEmpty(array)) {
			return array;
		}
		int i = Math.max(startIndexInclusive, 0);
		int j = Math.min(array.length, endIndexExclusive) - 1;
		while (j > i) {
			swap(array, i, j);
			j--;
			i++;
		}
		return array;
	}

	/**
	 * 反转数组，会变更原数组
	 *
	 * @param array 数组，会变更
	 * @return 变更后的原数组
	 */
	public static byte[] reverse(byte[] array) {
		return reverse(array, 0, array.length);
	}

	/**
	 * 取最小值
	 *
	 * @param numberArray 数字数组
	 * @return 最小值
	 */
	public static byte min(byte... numberArray) {
		if (isEmpty(numberArray)) {
			throw new IllegalArgumentException("Number array must not empty !");
		}
		byte min = numberArray[0];
		for (int i = 1; i < numberArray.length; i++) {
			if (min > numberArray[i]) {
				min = numberArray[i];
			}
		}
		return min;
	}

	/**
	 * 取最大值
	 *
	 * @param numberArray 数字数组
	 * @return 最大值
	 */
	public static byte max(byte... numberArray) {
		if (isEmpty(numberArray)) {
			throw new IllegalArgumentException("Number array must not empty !");
		}
		byte max = numberArray[0];
		for (int i = 1; i < numberArray.length; i++) {
			if (max < numberArray[i]) {
				max = numberArray[i];
			}
		}
		return max;
	}

	/**
	 * 打乱数组顺序，会变更原数组
	 *
	 * @param array 数组，会变更
	 * @return 打乱后的数组
	 */
	public static byte[] shuffle(byte[] array) {
		return shuffle(array, Randoms.getRandom());
	}

	/**
	 * 打乱数组顺序，会变更原数组
	 *
	 * @param array  数组，会变更
	 * @param random 随机数生成器
	 * @return 打乱后的数组
	 */
	public static byte[] shuffle(byte[] array, Random random) {
		if (array == null || random == null || array.length <= 1) {
			return array;
		}

		for (int i = array.length; i > 1; i--) {
			swap(array, i - 1, random.nextInt(i));
		}

		return array;
	}

	/**
	 * 交换数组中两个位置的值
	 *
	 * @param array  数组
	 * @param index1 位置1
	 * @param index2 位置2
	 * @return 交换后的数组，与传入数组为同一对象
	 */
	public static byte[] swap(byte[] array, int index1, int index2) {
		byte tmp = array[index1];
		array[index1] = array[index2];
		array[index2] = tmp;
		return array;
	}

	/**
	 * 检查数组是否升序，即array[i] &lt;= array[i+1]，若传入空数组，则返回false
	 *
	 * @param array 数组
	 * @return 数组是否升序
	 */
	public static boolean isSorted(byte[] array) {
		return isSortedAsc(array);
	}

	/**
	 * 检查数组是否升序，即array[i] &lt;= array[i+1]，若传入空数组，则返回false
	 *
	 * @param array 数组
	 * @return 数组是否升序
	 */
	public static boolean isSortedAsc(byte[] array) {
		if (array == null) {
			return false;
		}

		for (int i = 0; i < array.length - 1; i++) {
			if (array[i] > array[i + 1]) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 检查数组是否降序，即array[i] &gt;= array[i+1]，若传入空数组，则返回false
	 *
	 * @param array 数组
	 * @return 数组是否降序
	 */
	public static boolean isSortedDesc(byte[] array) {
		if (array == null) {
			return false;
		}

		for (int i = 0; i < array.length - 1; i++) {
			if (array[i] < array[i + 1]) {
				return false;
			}
		}

		return true;
	}



	public static byte[] copy(byte[] array, int length) {
		return Arrays.copyOf(array, length);
	}

	public static byte[] copy(byte[] array, int from, int to) {
		return Arrays.copyOfRange(array, from, to);
	}

	public static byte[] concat(@Nonnull byte[] byteArray, byte[]... byteArrays) {
		if (byteArrays.length == 0) {
			return byteArray;
		}
		int length = 0;
		for (byte[] bytes : byteArrays) {
			length += bytes.length;
		}
		byte[] result = new byte[length];
		int pos = 0;
		for (byte[] bytes : byteArrays) {
			System.arraycopy(bytes, 0, result, pos, bytes.length);
			pos += bytes.length;
		}
		return result;
	}

	// endregion


	// region 检查判断

	public static int indexOf(byte[] array, byte[] target) {
		if (target.length == 0) {
			return 0;
		}

		outer:
		for (int i = 0; i < array.length - target.length + 1; i++) {
			for (int j = 0; j < target.length; j++) {
				if (array[i + j] != target[j]) {
					continue outer;
				}
			}
			return i;
		}
		return -1;
	}

	public static int indexOf(byte[] array, byte value) {
		if (isNotEmpty(array)) {
			for (int i = 0; i < array.length; i++) {
				if (value == array[i]) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * 返回数组中指定元素所在最后的位置，未找到返回 -1
	 *
	 * @param array 数组
	 * @param value 被检查的元素
	 * @return 数组中指定元素所在位置，未找到返回 -1
	 */
	public static int lastIndexOf(byte[] array, byte value) {
		if (isNotEmpty(array)) {
			for (int i = array.length - 1; i >= 0; i--) {
				if (value == array[i]) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * 数组中是否包含元素
	 *
	 * @param array 数组
	 * @param value 被检查的元素
	 * @return 是否包含
	 */
	public static boolean contains(byte[] array, byte value) {
		return indexOf(array, value) > -1;
	}

	/**
	 * 数组是否为空
	 *
	 * @param array 数组
	 * @return 是否为空
	 */
	public static boolean isEmpty(byte[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 数组是否为非空
	 *
	 * @param array 数组
	 * @return 是否为非空
	 */
	public static boolean isNotEmpty(byte[] array) {
		return !isEmpty(array);
	}

	// endregion


	// region 类型转换


	public static byte[] utf8Bytes(CharSequence str) {
		return str.toString().getBytes(StandardCharsets.UTF_8);
	}

	public static byte[] utf8Bytes(String str) {
		return str.getBytes(StandardCharsets.UTF_8);
	}

	public static byte[] bytes(CharSequence str) {
		return str.toString().getBytes();
	}

	public static byte[] bytes(String str) {
		return str.getBytes();
	}

	/**
	 * int转byte
	 *
	 * @param intValue int值
	 * @return byte值
	 */
	public static byte intToByte(int intValue) {
		return (byte) intValue;
	}

	/**
	 * byte转无符号int
	 *
	 * @param byteValue byte值
	 * @return 无符号int值
	 */
	public static int byteToUnsignedInt(byte byteValue) {
		return byteValue & 0xFF;
	}

	/**
	 * byte数组转short<br>
	 * 默认以大端序转换
	 *
	 * @param bytes byte数组
	 * @return short值
	 */
	public static short bytesToShort(byte[] bytes) {
		return bytesToShort(bytes, DEFAULT_ORDER);
	}

	/**
	 * byte数组转short<br>
	 * 自定义端序
	 *
	 * @param bytes     byte数组，长度必须为2
	 * @param byteOrder 端序
	 * @return short值
	 */
	public static short bytesToShort(final byte[] bytes, final ByteOrder byteOrder) {
		return bytesToShort(bytes, 0, byteOrder);
	}

	/**
	 * byte数组转short<br>
	 * 自定义端序
	 *
	 * @param bytes     byte数组，长度必须大于2
	 * @param start     开始位置
	 * @param byteOrder 端序
	 * @return short值
	 */
	public static short bytesToShort(final byte[] bytes, final int start, final ByteOrder byteOrder) {
		if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
			//小端模式，数据的高字节保存在内存的高地址中，而数据的低字节保存在内存的低地址中
			return (short) (bytes[start] & 0xff | (bytes[start + 1] & 0xff) << Byte.SIZE);
		} else {
			return (short) (bytes[start + 1] & 0xff | (bytes[start] & 0xff) << Byte.SIZE);
		}
	}

	/**
	 * short转byte数组<br>
	 * 默认以大端序转换
	 *
	 * @param shortValue short值
	 * @return byte数组
	 */
	public static byte[] shortToBytes(short shortValue) {
		return shortToBytes(shortValue, DEFAULT_ORDER);
	}

	/**
	 * short转byte数组<br>
	 * 自定义端序
	 *
	 * @param shortValue short值
	 * @param byteOrder  端序
	 * @return byte数组
	 */
	public static byte[] shortToBytes(short shortValue, ByteOrder byteOrder) {
		byte[] b = new byte[Short.BYTES];
		if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
			b[0] = (byte) (shortValue & 0xff);
			b[1] = (byte) ((shortValue >> Byte.SIZE) & 0xff);
		} else {
			b[1] = (byte) (shortValue & 0xff);
			b[0] = (byte) ((shortValue >> Byte.SIZE) & 0xff);
		}
		return b;
	}

	/**
	 * byte[]转int值<br>
	 * 默认以大端序转换
	 *
	 * @param bytes byte数组
	 * @return int值
	 */
	public static int bytesToInt(byte[] bytes) {
		return bytesToInt(bytes, DEFAULT_ORDER);
	}

	/**
	 * byte[]转int值<br>
	 * 自定义端序
	 *
	 * @param bytes     byte数组
	 * @param byteOrder 端序
	 * @return int值
	 */
	public static int bytesToInt(byte[] bytes, ByteOrder byteOrder) {
		return bytesToInt(bytes, 0, byteOrder);
	}

	/**
	 * byte[]转int值<br>
	 * 自定义端序
	 *
	 * @param bytes     byte数组
	 * @param start     开始位置（包含）
	 * @param byteOrder 端序
	 * @return int值
	 */
	public static int bytesToInt(byte[] bytes, int start, ByteOrder byteOrder) {
		if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
			return bytes[start] & 0xFF | //
				(bytes[1 + start] & 0xFF) << 8 | //
				(bytes[2 + start] & 0xFF) << 16 | //
				(bytes[3 + start] & 0xFF) << 24; //
		} else {
			return bytes[3 + start] & 0xFF | //
				(bytes[2 + start] & 0xFF) << 8 | //
				(bytes[1 + start] & 0xFF) << 16 | //
				(bytes[start] & 0xFF) << 24; //
		}

	}

	/**
	 * int转byte数组<br>
	 * 默认以大端序转换
	 *
	 * @param intValue int值
	 * @return byte数组
	 */
	public static byte[] intToBytes(int intValue) {
		return intToBytes(intValue, DEFAULT_ORDER);
	}

	/**
	 * int转byte数组<br>
	 * 自定义端序
	 *
	 * @param intValue  int值
	 * @param byteOrder 端序
	 * @return byte数组
	 */
	public static byte[] intToBytes(int intValue, ByteOrder byteOrder) {

		if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
			return new byte[]{ //
				(byte) (intValue & 0xFF), //
				(byte) ((intValue >> 8) & 0xFF), //
				(byte) ((intValue >> 16) & 0xFF), //
				(byte) ((intValue >> 24) & 0xFF) //
			};

		} else {
			return new byte[]{ //
				(byte) ((intValue >> 24) & 0xFF), //
				(byte) ((intValue >> 16) & 0xFF), //
				(byte) ((intValue >> 8) & 0xFF), //
				(byte) (intValue & 0xFF) //
			};
		}

	}

	/**
	 * long转byte数组<br>
	 * 默认以大端序转换<br>
	 * from: https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
	 *
	 * @param longValue long值
	 * @return byte数组
	 */
	public static byte[] longToBytes(long longValue) {
		return longToBytes(longValue, DEFAULT_ORDER);
	}

	/**
	 * long转byte数组<br>
	 * 自定义端序<br>
	 * from: https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
	 *
	 * @param longValue long值
	 * @param byteOrder 端序
	 * @return byte数组
	 */
	public static byte[] longToBytes(long longValue, ByteOrder byteOrder) {
		byte[] result = new byte[Long.BYTES];
		if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
			for (int i = 0; i < result.length; i++) {
				result[i] = (byte) (longValue & 0xFF);
				longValue >>= Byte.SIZE;
			}
		} else {
			for (int i = (result.length - 1); i >= 0; i--) {
				result[i] = (byte) (longValue & 0xFF);
				longValue >>= Byte.SIZE;
			}
		}
		return result;
	}

	/**
	 * byte数组转long<br>
	 * 默认以大端序转换<br>
	 * from: https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
	 *
	 * @param bytes byte数组
	 * @return long值
	 */
	public static long bytesToLong(byte[] bytes) {
		return bytesToLong(bytes, DEFAULT_ORDER);
	}

	/**
	 * byte数组转long<br>
	 * 自定义端序<br>
	 * from: https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
	 *
	 * @param bytes     byte数组
	 * @param byteOrder 端序
	 * @return long值
	 */
	public static long bytesToLong(byte[] bytes, ByteOrder byteOrder) {
		return bytesToLong(bytes, 0, byteOrder);
	}

	/**
	 * byte数组转long<br>
	 * 自定义端序<br>
	 * from: https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
	 *
	 * @param bytes     byte数组
	 * @param start     计算数组开始位置
	 * @param byteOrder 端序
	 * @return long值
	 */
	public static long bytesToLong(byte[] bytes, int start, ByteOrder byteOrder) {
		long values = 0;
		if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
			for (int i = (Long.BYTES - 1); i >= 0; i--) {
				values <<= Byte.SIZE;
				values |= (bytes[i + start] & 0xff);
			}
		} else {
			for (int i = 0; i < Long.BYTES; i++) {
				values <<= Byte.SIZE;
				values |= (bytes[i + start] & 0xff);
			}
		}

		return values;
	}

	/**
	 * float转byte数组，默认以大端序转换<br>
	 *
	 * @param floatValue float值
	 * @return byte数组
	 */
	public static byte[] floatToBytes(float floatValue) {
		return floatToBytes(floatValue, DEFAULT_ORDER);
	}

	/**
	 * float转byte数组，自定义端序<br>
	 *
	 * @param floatValue float值
	 * @param byteOrder  端序
	 * @return byte数组
	 */
	public static byte[] floatToBytes(float floatValue, ByteOrder byteOrder) {
		return intToBytes(Float.floatToIntBits(floatValue), byteOrder);
	}

	/**
	 * byte数组转float<br>
	 * 默认以大端序转换<br>
	 *
	 * @param bytes byte数组
	 * @return float值
	 */
	public static float bytesToFloat(byte[] bytes) {
		return bytesToFloat(bytes, DEFAULT_ORDER);
	}

	/**
	 * byte数组转float<br>
	 * 自定义端序<br>
	 *
	 * @param bytes     byte数组
	 * @param byteOrder 端序
	 * @return float值
	 */
	public static float bytesToFloat(byte[] bytes, ByteOrder byteOrder) {
		return Float.intBitsToFloat(bytesToInt(bytes, byteOrder));
	}

	/**
	 * double转byte数组<br>
	 * 默认以大端序转换<br>
	 *
	 * @param doubleValue double值
	 * @return byte数组
	 */
	public static byte[] doubleToBytes(double doubleValue) {
		return doubleToBytes(doubleValue, DEFAULT_ORDER);
	}

	/**
	 * double转byte数组<br>
	 * 自定义端序<br>
	 * from: https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
	 *
	 * @param doubleValue double值
	 * @param byteOrder   端序
	 * @return byte数组
	 */
	public static byte[] doubleToBytes(double doubleValue, ByteOrder byteOrder) {
		return longToBytes(Double.doubleToLongBits(doubleValue), byteOrder);
	}

	/**
	 * byte数组转Double<br>
	 * 默认以大端序转换<br>
	 *
	 * @param bytes byte数组
	 * @return long值
	 */
	public static double bytesToDouble(byte[] bytes) {
		return bytesToDouble(bytes, DEFAULT_ORDER);
	}

	/**
	 * byte数组转double<br>
	 * 自定义端序<br>
	 *
	 * @param bytes     byte数组
	 * @param byteOrder 端序
	 * @return long值
	 */
	public static double bytesToDouble(byte[] bytes, ByteOrder byteOrder) {
		return Double.longBitsToDouble(bytesToLong(bytes, byteOrder));
	}

	/**
	 * 将{@link Number}转换为
	 *
	 * @param number 数字
	 * @return bytes
	 */
	public static byte[] numberToBytes(Number number) {
		return numberToBytes(number, DEFAULT_ORDER);
	}

	/**
	 * 将{@link Number}转换为
	 *
	 * @param number    数字
	 * @param byteOrder 端序
	 * @return bytes
	 */
	public static byte[] numberToBytes(Number number, ByteOrder byteOrder) {
		if (number instanceof Byte) {
			return new byte[]{number.byteValue()};
		} else if (number instanceof Double) {
			return doubleToBytes((Double) number, byteOrder);
		} else if (number instanceof Long) {
			return longToBytes((Long) number, byteOrder);
		} else if (number instanceof Integer) {
			return intToBytes((Integer) number, byteOrder);
		} else if (number instanceof Short) {
			return shortToBytes((Short) number, byteOrder);
		} else if (number instanceof Float) {
			return floatToBytes((Float) number, byteOrder);
		} else {
			return doubleToBytes(number.doubleValue(), byteOrder);
		}
	}

	/**
	 * byte数组转换为指定类型数字
	 *
	 * @param <T>         数字类型
	 * @param bytes       byte数组
	 * @param targetClass 目标数字类型
	 * @param byteOrder   端序
	 * @return 转换后的数字
	 * @throws IllegalArgumentException 不支持的数字类型，如用户自定义数字类型
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number> T bytesToNumber(byte[] bytes, Class<T> targetClass, ByteOrder byteOrder) throws IllegalArgumentException {
		Number number;
		if (Byte.class == targetClass || byte.class == targetClass) {
			number = bytes[0];
		} else if (Short.class == targetClass || short.class == targetClass) {
			number = bytesToShort(bytes, byteOrder);
		} else if (Integer.class == targetClass || int.class == targetClass) {
			number = bytesToInt(bytes, byteOrder);
		} else if (AtomicInteger.class == targetClass) {
			number = new AtomicInteger(bytesToInt(bytes, byteOrder));
		} else if (Long.class == targetClass || long.class == targetClass) {
			number = bytesToLong(bytes, byteOrder);
		} else if (AtomicLong.class == targetClass) {
			number = new AtomicLong(bytesToLong(bytes, byteOrder));
		} else if (LongAdder.class == targetClass) {
			final LongAdder longValue = new LongAdder();
			longValue.add(bytesToLong(bytes, byteOrder));
			number = longValue;
		} else if (Float.class == targetClass || float.class == targetClass) {
			number = bytesToFloat(bytes, byteOrder);
		} else if (Double.class == targetClass || double.class == targetClass) {
			number = bytesToDouble(bytes, byteOrder);
		} else if (DoubleAdder.class == targetClass) {
			final DoubleAdder doubleAdder = new DoubleAdder();
			doubleAdder.add(bytesToDouble(bytes, byteOrder));
			number = doubleAdder;
		} else if (BigDecimal.class == targetClass) {
			number = Numbers.toBigDecimal(bytesToDouble(bytes, byteOrder));
		} else if (BigInteger.class == targetClass) {
			number = BigInteger.valueOf(bytesToLong(bytes, byteOrder));
		} else if (Number.class == targetClass) {
			// 用户没有明确类型具体类型，默认Double
			number = bytesToDouble(bytes, byteOrder);
		} else {
			// 用户自定义类型不支持
			throw new IllegalArgumentException("Unsupported Number type: " + targetClass.getName());
		}

		return (T) number;
	}

	// endregion


}
