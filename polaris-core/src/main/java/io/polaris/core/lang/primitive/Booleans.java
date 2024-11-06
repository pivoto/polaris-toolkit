package io.polaris.core.lang.primitive;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

import io.polaris.core.random.Randoms;

/**
 * @author Qt
 * @since Sep 28, 2024
 */
public class Booleans {

	/**
	 * 检查 {@code Boolean} 值是否为 {@code true}
	 *
	 * <pre>
	 *   BooleanUtil.isTrue(Boolean.TRUE)  = true
	 *   BooleanUtil.isTrue(Boolean.FALSE) = false
	 *   BooleanUtil.isTrue(null)          = false
	 * </pre>
	 *
	 * @param bool 被检查的Boolean值
	 * @return 当值为true且非null时返回{@code true}
	 */
	public static boolean isTrue(Boolean bool) {
		return Boolean.TRUE.equals(bool);
	}

	/**
	 * 检查 {@code Boolean} 值是否为 {@code false}
	 *
	 * <pre>
	 *   BooleanUtil.isFalse(Boolean.TRUE)  = false
	 *   BooleanUtil.isFalse(Boolean.FALSE) = true
	 *   BooleanUtil.isFalse(null)          = false
	 * </pre>
	 *
	 * @param bool 被检查的Boolean值
	 * @return 当值为false且非null时返回{@code true}
	 */
	public static boolean isFalse(Boolean bool) {
		return Boolean.FALSE.equals(bool);
	}

	/**
	 * 数组是否为空
	 *
	 * @param array 数组
	 * @return 是否为空
	 */
	public static boolean isEmpty(boolean[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 数组是否为非空
	 *
	 * @param array 数组
	 * @return 是否为非空
	 */
	public static boolean isNotEmpty(boolean[] array) {
		return !isEmpty(array);
	}

	/**
	 * 将多个数组合并在一起<br>
	 * 忽略null的数组
	 *
	 * @param arrays 数组集合
	 * @return 合并后的数组
	 */
	public static boolean[] join(boolean[]... arrays) {
		if (arrays.length == 1) {
			return arrays[0];
		}

		// 计算总长度
		int length = 0;
		for (boolean[] array : arrays) {
			if (isNotEmpty(array)) {
				length += array.length;
			}
		}

		final boolean[] result = new boolean[length];
		length = 0;
		for (boolean[] array : arrays) {
			if (isNotEmpty(array)) {
				System.arraycopy(array, 0, result, length, array.length);
				length += array.length;
			}
		}
		return result;
	}

	/**
	 * 返回数组中指定元素所在位置，未找到返回 -1
	 *
	 * @param array 数组
	 * @param value 被检查的元素
	 * @return 数组中指定元素所在位置，未找到返回 -1
	 */
	public static int indexOf(boolean[] array, boolean value) {
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
	public static int lastIndexOf(boolean[] array, boolean value) {
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
	public static boolean contains(boolean[] array, boolean value) {
		return indexOf(array, value) > -1;
	}

	/**
	 * 将原始类型数组包装为包装类型
	 *
	 * @param values 原始类型数组
	 * @return 包装类型数组
	 */
	public static Boolean[] wrap(boolean... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new Boolean[0];
		}

		final Boolean[] array = new Boolean[length];
		for (int i = 0; i < length; i++) {
			array[i] = values[i];
		}
		return array;
	}

	/**
	 * 包装类数组转为原始类型数组<br>
	 * {@code null} 按照 {@code false} 对待
	 *
	 * @param values 包装类型数组
	 * @return 原始类型数组
	 */
	public static boolean[] unwrap(Boolean... values) {
		if (null == values) {
			return null;
		}
		final int length = values.length;
		if (0 == length) {
			return new boolean[0];
		}

		final boolean[] array = new boolean[length];
		for (int i = 0; i < length; i++) {
			array[i] = Optional.ofNullable(values[i]).orElse(false);
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
	public static boolean[] sub(boolean[] array, int start, int end) {
		int length = array.length;
		if (start < 0) {
			start += length;
		}
		if (end < 0) {
			end += length;
		}
		if (start == length) {
			return new boolean[0];
		}
		if (start > end) {
			int tmp = start;
			start = end;
			end = tmp;
		}
		if (end > length) {
			if (start >= length) {
				return new boolean[0];
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
	public static boolean[] remove(boolean[] array, int index) throws IllegalArgumentException {
		if (null == array) {
			return null;
		}
		int length = array.length;
		if (index < 0 || index >= length) {
			return array;
		}

		final boolean[] result = new boolean[length - 1];
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
	public static boolean[] removeElement(boolean[] array, boolean element) throws IllegalArgumentException {
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
	public static boolean[] reverse(boolean[] array, final int startIndexInclusive, final int endIndexExclusive) {
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
	public static boolean[] reverse(boolean[] array) {
		return reverse(array, 0, array.length);
	}

	/**
	 * 打乱数组顺序，会变更原数组
	 *
	 * @param array 数组，会变更
	 * @return 打乱后的数组
	 */
	public static boolean[] shuffle(boolean[] array) {
		return shuffle(array, Randoms.getRandom());
	}

	/**
	 * 打乱数组顺序，会变更原数组
	 *
	 * @param array  数组，会变更
	 * @param random 随机数生成器
	 * @return 打乱后的数组
	 */
	public static boolean[] shuffle(boolean[] array, Random random) {
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
	public static boolean[] swap(boolean[] array, int index1, int index2) {
		if (isEmpty(array)) {
			throw new IllegalArgumentException("Number array must not empty !");
		}
		boolean tmp = array[index1];
		array[index1] = array[index2];
		array[index2] = tmp;
		return array;
	}
}
