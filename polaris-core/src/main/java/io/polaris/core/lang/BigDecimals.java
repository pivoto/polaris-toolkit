package io.polaris.core.lang;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import io.polaris.core.assertion.Assertions;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Nov 21, 2024
 */
public class BigDecimals {

	// region round

	/**
	 * 保留固定位数小数<br>
	 * 例如保留四位小数：123.456789 =》 123.4567
	 *
	 * @param number       数字值
	 * @param scale        保留小数位数，如果传入小于0，则默认0
	 * @param roundingMode 保留小数的模式 {@link RoundingMode}，如果传入null则默认四舍五入
	 * @return 新值
	 */
	public static BigDecimal round(BigDecimal number, int scale, RoundingMode roundingMode) {
		if (null == number) {
			number = BigDecimal.ZERO;
		}
		if (scale < 0) {
			scale = 0;
		}
		if (null == roundingMode) {
			roundingMode = RoundingMode.HALF_UP;
		}

		return number.setScale(scale, roundingMode);
	}

	/**
	 * 保留固定位数小数<br>
	 * 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br>
	 * 例如保留2位小数：123.456789 =》 123.46
	 *
	 * @param number 数字值
	 * @param scale  保留小数位数
	 * @return 新值
	 */
	public static BigDecimal round(BigDecimal number, int scale) {
		return round(number, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 保留固定位数小数<br>
	 * 例如保留四位小数：123.456789 =》 123.4567
	 *
	 * @param numberStr    数字值的字符串表现形式
	 * @param scale        保留小数位数，如果传入小于0，则默认0
	 * @param roundingMode 保留小数的模式 {@link RoundingMode}，如果传入null则默认四舍五入
	 * @return 新值
	 */
	public static BigDecimal round(String numberStr, int scale, RoundingMode roundingMode) {
		Assertions.assertTrue(Strings.isNotBlank(numberStr), "字符串不能为空白");
		if (scale < 0) {
			scale = 0;
		}
		return round(toBigDecimal(numberStr), scale, roundingMode);
	}

	/**
	 * 保留固定位数小数<br>
	 * 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br>
	 * 例如保留2位小数：123.456789 =》 123.46
	 *
	 * @param numberStr 数字值的字符串表现形式
	 * @param scale     保留小数位数
	 * @return 新值
	 */
	public static BigDecimal round(String numberStr, int scale) {
		return round(numberStr, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 保留固定位数小数<br>
	 * 例如保留四位小数：123.456789 =》 123.4567
	 *
	 * @param numberStr    数字值的字符串表现形式
	 * @param scale        保留小数位数
	 * @param roundingMode 保留小数的模式 {@link RoundingMode}
	 * @return 新值
	 */
	public static String roundAsString(String numberStr, int scale, RoundingMode roundingMode) {
		return round(numberStr, scale, roundingMode).toPlainString();
	}

	/**
	 * 保留固定位数小数<br>
	 * 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br>
	 * 例如保留2位小数：123.456789 =》 123.46
	 *
	 * @param numberStr 数字值的字符串表现形式
	 * @param scale     保留小数位数
	 * @return 新值
	 */
	public static String roundAsString(String numberStr, int scale) {
		return round(numberStr, scale).toPlainString();
	}

	/**
	 * 保留固定小数位数，舍去多余位数
	 *
	 * @param value 需要科学计算的数据
	 * @param scale 保留的小数位
	 * @return 结果
	 */
	public static BigDecimal roundDown(BigDecimal value, int scale) {
		return round(value, scale, RoundingMode.DOWN);
	}

	/**
	 * 保留固定小数位数，舍去多余位数
	 *
	 * @param number 需要科学计算的数据
	 * @param scale  保留的小数位
	 * @return 结果
	 */
	public static BigDecimal roundDown(Number number, int scale) {
		return roundDown(toBigDecimal(number), scale);
	}

	/**
	 * 四舍六入五成双计算法
	 * <p>
	 * 四舍六入五成双是一种比较精确比较科学的计数保留法，是一种数字修约规则。
	 * </p>
	 *
	 * <pre>
	 * 算法规则:
	 * 四舍六入五考虑，
	 * 五后非零就进一，
	 * 五后皆零看奇偶，
	 * 五前为偶应舍去，
	 * 五前为奇要进一。
	 * </pre>
	 *
	 * @param value 需要科学计算的数据
	 * @param scale 保留的小数位
	 * @return 结果
	 */
	public static BigDecimal roundHalfEven(BigDecimal value, int scale) {
		return round(value, scale, RoundingMode.HALF_EVEN);
	}

	/**
	 * @see BigDecimals#roundHalfEven(BigDecimal, int)
	 */
	public static BigDecimal roundHalfEven(Number number, int scale) {
		return roundHalfEven(toBigDecimal(number), scale);
	}

	// endregion

	// region convert

	/**
	 * 数字转{@link BigDecimal}<br>
	 * Float、Double等有精度问题，转换为字符串后再转换<br>
	 * null转换为0
	 *
	 * @param number 数字
	 * @return {@link BigDecimal}
	 */
	public static BigDecimal toBigDecimal(Number number) {
		if (null == number) {
			return BigDecimal.ZERO;
		}

		if (number instanceof BigDecimal) {
			return (BigDecimal) number;
		} else if (number instanceof Long) {
			return new BigDecimal((Long) number);
		} else if (number instanceof Integer) {
			return new BigDecimal((Integer) number);
		} else if (number instanceof BigInteger) {
			return new BigDecimal((BigInteger) number);
		}

		// Float、Double等有精度问题，转换为字符串后再转换
		return toBigDecimal(number.toString());
	}

	/**
	 * 数字转{@link BigDecimal}<br>
	 * null或""或空白符转换为0
	 *
	 * @param numberStr 数字字符串
	 * @return {@link BigDecimal}
	 */
	public static BigDecimal toBigDecimal(String numberStr) {
		if (Strings.isBlank(numberStr)) {
			return BigDecimal.ZERO;
		}
		try {
			// 支持类似于 1,234.55 格式的数字
			final Number number = Numbers.parseNumber(numberStr);
			if (number instanceof BigDecimal) {
				return (BigDecimal) number;
			} else {
				return new BigDecimal(number.toString());
			}
		} catch (Exception ignore) {
			// 忽略解析错误
		}
		return new BigDecimal(numberStr);
	}

	/**
	 * {@link BigDecimal}数字转字符串<br>
	 * 调用{@link BigDecimal#toPlainString()}，并去除尾小数点儿后多余的0
	 *
	 * @param bigDecimal A {@link BigDecimal}
	 * @return A String.
	 */
	public static String toString(BigDecimal bigDecimal) {
		return toString(bigDecimal, true);
	}

	/**
	 * {@link BigDecimal}数字转字符串<br>
	 * 调用{@link BigDecimal#toPlainString()}，可选去除尾小数点儿后多余的0
	 *
	 * @param bigDecimal           A {@link BigDecimal}
	 * @param isStripTrailingZeros 是否去除末尾多余0，例如5.0返回5
	 * @return A String.
	 */
	public static String toString(BigDecimal bigDecimal, boolean isStripTrailingZeros) {
		Assertions.assertNotNull(bigDecimal, "BigDecimal is null !");
		if (isStripTrailingZeros) {
			bigDecimal = bigDecimal.stripTrailingZeros();
		}
		return bigDecimal.toPlainString();
	}

	// endregion

	// region 默认值

	public static BigDecimal defaultIfNull(BigDecimal number, BigDecimal defaultValue) {
		return Objs.defaultIfNull(number, defaultValue);
	}


	/**
	 * 空转0
	 *
	 * @param number {@link BigDecimal}，可以为{@code null}
	 * @return {@link BigDecimal}参数为空时返回0的值
	 */
	public static BigDecimal defaultIfNull(BigDecimal number) {
		return Objs.defaultIfNull(number, BigDecimal.ZERO);
	}


	// endregion


}
