package io.polaris.core.lang;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.polaris.core.annotation.Experimental;
import io.polaris.core.assertion.Assertions;
import io.polaris.core.collection.Iterables;
import io.polaris.core.collection.ObjectArrays;
import io.polaris.core.collection.PrimitiveArrays;
import io.polaris.core.lang.primitive.Chars;
import io.polaris.core.lang.primitive.Doubles;
import io.polaris.core.lang.primitive.Ints;
import io.polaris.core.random.Randoms;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 */
public class Numbers {

	/**
	 * 默认除法运算精度
	 */
	private static final int DEFAULT_DIV_SCALE = 10;

	/**
	 * 0-20对应的阶乘，超过20的阶乘会超过Long.MAX_VALUE
	 */
	private static final long[] FACTORIALS = new long[]{
		1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L, 362880L, 3628800L, 39916800L, 479001600L, 6227020800L,
		87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L,
		2432902008176640000L};


	// region calc

	/**
	 * 计算数学表达式的值，只支持加减乘除和取余<br>
	 * 如：
	 * <pre class="code">
	 *   calculate("(0*1--3)-5/-4-(3*(-2.13))") -》 10.64
	 * </pre>
	 *
	 * @param expression 数学表达式
	 * @return 结果
	 */
	@Experimental
	public static double calculate(String expression) {
		return Calculator.conversion(expression);
	}

	/**
	 * @see Math#floorDiv(long, long)
	 */
	public static long floorDiv(long x, long y) {
		return Math.floorDiv(x, y);
	}

	/**
	 * @see Math#floorDiv(int, int)
	 */
	public static int floorDiv(int x, int y) {
		return Math.floorDiv(x, y);
	}

	public static long ceilDiv(long x, long y) {
		long r = x / y;
		if ((x ^ y) > 0 && (r * y != x)) {
			r++;
		}
		return r;
	}


	/**
	 * 补充Math.ceilDiv() JDK8中添加了和Math.floorDiv()但却没有ceilDiv()
	 *
	 * @param x 被除数
	 * @param y 除数
	 * @return 两个参数的商
	 * @see Math#floorDiv(int, int)
	 */
	public static int ceilDiv(int x, int y) {
		int r = x / y;
		if ((x ^ y) > 0 && (r * y != x)) {
			r++;
		}
		return r;
//		return (int) Math.ceil((double) x / t);
	}

	/**
	 * 提供精确的幂运算
	 *
	 * @param number 底数
	 * @param n      指数
	 * @return 幂的积
	 */
	public static BigDecimal pow(Number number, int n) {
		return pow(toBigDecimal(number), n);
	}

	/**
	 * 提供精确的幂运算
	 *
	 * @param number 底数
	 * @param n      指数
	 * @return 幂的积
	 */
	public static BigDecimal pow(BigDecimal number, int n) {
		return number.pow(n);
	}


	/**
	 * 提供精确的加法运算
	 *
	 * @param v1 被加数
	 * @param v2 加数
	 * @return 和
	 */
	public static double add(float v1, float v2) {
		return add(Float.toString(v1), Float.toString(v2)).doubleValue();
	}

	/**
	 * 提供精确的加法运算
	 *
	 * @param v1 被加数
	 * @param v2 加数
	 * @return 和
	 */
	public static double add(float v1, double v2) {
		return add(Float.toString(v1), Double.toString(v2)).doubleValue();
	}

	/**
	 * 提供精确的加法运算
	 *
	 * @param v1 被加数
	 * @param v2 加数
	 * @return 和
	 */
	public static double add(double v1, float v2) {
		return add(Double.toString(v1), Float.toString(v2)).doubleValue();
	}

	/**
	 * 提供精确的加法运算
	 *
	 * @param v1 被加数
	 * @param v2 加数
	 * @return 和
	 */
	public static double add(double v1, double v2) {
		return add(Double.toString(v1), Double.toString(v2)).doubleValue();
	}

	/**
	 * 提供精确的加法运算
	 *
	 * @param v1 被加数
	 * @param v2 加数
	 * @return 和
	 */
	public static double add(Double v1, Double v2) {
		//noinspection RedundantCast
		return add((Number) v1, (Number) v2).doubleValue();
	}

	/**
	 * 提供精确的加法运算<br>
	 * 如果传入多个值为null或者空，则返回0
	 *
	 * <p>
	 * 需要注意的是，在不同Locale下，数字的表示形式也是不同的，例如：<br>
	 * 德国、荷兰、比利时、丹麦、意大利、罗马尼亚和欧洲大多地区使用`,`区分小数<br>
	 * 也就是说，在这些国家地区，1.20表示120，而非1.2。
	 * </p>
	 *
	 * @param v1 被加数
	 * @param v2 加数
	 * @return 和
	 */
	public static BigDecimal add(Number v1, Number v2) {
		return add(new Number[]{v1, v2});
	}

	/**
	 * 提供精确的加法运算<br>
	 * 如果传入多个值为null或者空，则返回0
	 *
	 * <p>
	 * 需要注意的是，在不同Locale下，数字的表示形式也是不同的，例如：<br>
	 * 德国、荷兰、比利时、丹麦、意大利、罗马尼亚和欧洲大多地区使用`,`区分小数<br>
	 * 也就是说，在这些国家地区，1.20表示120，而非1.2。
	 * </p>
	 *
	 * @param values 多个被加值
	 * @return 和
	 */
	public static BigDecimal add(Number... values) {
		if (values.length == 0) {
			return BigDecimal.ZERO;
		}

		Number value = values[0];
		BigDecimal result = toBigDecimal(value);
		for (int i = 1; i < values.length; i++) {
			value = values[i];
			if (null != value) {
				result = result.add(toBigDecimal(value));
			}
		}
		return result;
	}

	/**
	 * 提供精确的加法运算<br>
	 * 如果传入多个值为null或者空，则返回0
	 *
	 * @param values 多个被加值
	 * @return 和
	 */
	public static BigDecimal add(String... values) {
		if (values.length == 0) {
			return BigDecimal.ZERO;
		}

		String value = values[0];
		BigDecimal result = toBigDecimal(value);
		for (int i = 1; i < values.length; i++) {
			value = values[i];
			if (Strings.isNotBlank(value)) {
				result = result.add(toBigDecimal(value));
			}
		}
		return result;
	}

	/**
	 * 提供精确的加法运算<br>
	 * 如果传入多个值为null或者空，则返回0
	 *
	 * @param values 多个被加值
	 * @return 和
	 */
	public static BigDecimal add(BigDecimal... values) {
		if (values.length == 0) {
			return BigDecimal.ZERO;
		}

		BigDecimal value = values[0];
		BigDecimal result = toBigDecimal(value);
		for (int i = 1; i < values.length; i++) {
			value = values[i];
			if (null != value) {
				result = result.add(value);
			}
		}
		return result;
	}

	/**
	 * 提供精确的减法运算
	 *
	 * @param v1 被减数
	 * @param v2 减数
	 * @return 差
	 */
	public static double sub(float v1, float v2) {
		return sub(Float.toString(v1), Float.toString(v2)).doubleValue();
	}

	/**
	 * 提供精确的减法运算
	 *
	 * @param v1 被减数
	 * @param v2 减数
	 * @return 差
	 */
	public static double sub(float v1, double v2) {
		return sub(Float.toString(v1), Double.toString(v2)).doubleValue();
	}

	/**
	 * 提供精确的减法运算
	 *
	 * @param v1 被减数
	 * @param v2 减数
	 * @return 差
	 */
	public static double sub(double v1, float v2) {
		return sub(Double.toString(v1), Float.toString(v2)).doubleValue();
	}

	/**
	 * 提供精确的减法运算
	 *
	 * @param v1 被减数
	 * @param v2 减数
	 * @return 差
	 */
	public static double sub(double v1, double v2) {
		return sub(Double.toString(v1), Double.toString(v2)).doubleValue();
	}

	/**
	 * 提供精确的减法运算
	 *
	 * @param v1 被减数
	 * @param v2 减数
	 * @return 差
	 */
	public static double sub(Double v1, Double v2) {
		//noinspection RedundantCast
		return sub((Number) v1, (Number) v2).doubleValue();
	}

	/**
	 * 提供精确的减法运算<br>
	 * 如果传入多个值为null或者空，则返回0
	 *
	 * @param v1 被减数
	 * @param v2 减数
	 * @return 差
	 */
	public static BigDecimal sub(Number v1, Number v2) {
		return sub(new Number[]{v1, v2});
	}

	/**
	 * 提供精确的减法运算<br>
	 * 如果传入多个值为null或者空，则返回0
	 *
	 * @param values 多个被减值
	 * @return 差
	 */
	public static BigDecimal sub(Number... values) {
		if (values.length == 0) {
			return BigDecimal.ZERO;
		}

		Number value = values[0];
		BigDecimal result = toBigDecimal(value);
		for (int i = 1; i < values.length; i++) {
			value = values[i];
			if (null != value) {
				result = result.subtract(toBigDecimal(value));
			}
		}
		return result;
	}

	/**
	 * 提供精确的减法运算<br>
	 * 如果传入多个值为null或者空，则返回0
	 *
	 * @param values 多个被减值
	 * @return 差
	 */
	public static BigDecimal sub(String... values) {
		if (values.length == 0) {
			return BigDecimal.ZERO;
		}

		String value = values[0];
		BigDecimal result = toBigDecimal(value);
		for (int i = 1; i < values.length; i++) {
			value = values[i];
			if (Strings.isNotBlank(value)) {
				result = result.subtract(toBigDecimal(value));
			}
		}
		return result;
	}

	/**
	 * 提供精确的减法运算<br>
	 * 如果传入多个值为null或者空，则返回0
	 *
	 * @param values 多个被减值
	 * @return 差
	 */
	public static BigDecimal sub(BigDecimal... values) {
		if (values.length == 0) {
			return BigDecimal.ZERO;
		}

		BigDecimal value = values[0];
		BigDecimal result = toBigDecimal(value);
		for (int i = 1; i < values.length; i++) {
			value = values[i];
			if (null != value) {
				result = result.subtract(value);
			}
		}
		return result;
	}

	/**
	 * 提供精确的乘法运算
	 *
	 * @param v1 被乘数
	 * @param v2 乘数
	 * @return 积
	 */
	public static double mul(float v1, float v2) {
		return mul(Float.toString(v1), Float.toString(v2)).doubleValue();
	}

	/**
	 * 提供精确的乘法运算
	 *
	 * @param v1 被乘数
	 * @param v2 乘数
	 * @return 积
	 */
	public static double mul(float v1, double v2) {
		return mul(Float.toString(v1), Double.toString(v2)).doubleValue();
	}

	/**
	 * 提供精确的乘法运算
	 *
	 * @param v1 被乘数
	 * @param v2 乘数
	 * @return 积
	 */
	public static double mul(double v1, float v2) {
		return mul(Double.toString(v1), Float.toString(v2)).doubleValue();
	}

	/**
	 * 提供精确的乘法运算
	 *
	 * @param v1 被乘数
	 * @param v2 乘数
	 * @return 积
	 */
	public static double mul(double v1, double v2) {
		return mul(Double.toString(v1), Double.toString(v2)).doubleValue();
	}

	/**
	 * 提供精确的乘法运算<br>
	 * 如果传入多个值为null或者空，则返回0
	 *
	 * @param v1 被乘数
	 * @param v2 乘数
	 * @return 积
	 */
	public static double mul(Double v1, Double v2) {
		//noinspection RedundantCast
		return mul((Number) v1, (Number) v2).doubleValue();
	}

	/**
	 * 提供精确的乘法运算<br>
	 * 如果传入多个值为null或者空，则返回0
	 *
	 * @param v1 被乘数
	 * @param v2 乘数
	 * @return 积
	 */
	public static BigDecimal mul(Number v1, Number v2) {
		return mul(new Number[]{v1, v2});
	}

	/**
	 * 提供精确的乘法运算<br>
	 * 如果传入多个值为null或者空，则返回0
	 *
	 * @param values 多个被乘值
	 * @return 积
	 */
	public static BigDecimal mul(Number... values) {
		if (values.length == 0 || Iterables.hasNull(values)) {
			return BigDecimal.ZERO;
		}

		Number value = values[0];
		BigDecimal result = toBigDecimal(value.toString());
		for (int i = 1; i < values.length; i++) {
			value = values[i];
			result = result.multiply(toBigDecimal(value.toString()));
		}
		return result;
	}

	/**
	 * 提供精确的乘法运算
	 *
	 * @param v1 被乘数
	 * @param v2 乘数
	 * @return 积
	 */
	public static BigDecimal mul(String v1, String v2) {
		return mul(toBigDecimal(v1), toBigDecimal(v2));
	}

	/**
	 * 提供精确的乘法运算<br>
	 * 如果传入多个值为null或者空，则返回0
	 *
	 * @param values 多个被乘值
	 * @return 积
	 */
	public static BigDecimal mul(String... values) {
		if (values.length == 0 || Iterables.hasNull(values)) {
			return BigDecimal.ZERO;
		}

		BigDecimal result = toBigDecimal(values[0]);
		for (int i = 1; i < values.length; i++) {
			result = result.multiply(toBigDecimal(values[i]));
		}

		return result;
	}

	/**
	 * 提供精确的乘法运算<br>
	 * 如果传入多个值为null或者空，则返回0
	 *
	 * @param values 多个被乘值
	 * @return 积
	 */
	public static BigDecimal mul(BigDecimal... values) {
		if (values.length == 0 || Iterables.hasNull(values)) {
			return BigDecimal.ZERO;
		}

		BigDecimal result = values[0];
		for (int i = 1; i < values.length; i++) {
			result = result.multiply(values[i]);
		}
		return result;
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
	 *
	 * @param v1 被除数
	 * @param v2 除数
	 * @return 两个参数的商
	 */
	public static double div(float v1, float v2) {
		return div(v1, v2, DEFAULT_DIV_SCALE);
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
	 *
	 * @param v1 被除数
	 * @param v2 除数
	 * @return 两个参数的商
	 */
	public static double div(float v1, double v2) {
		return div(v1, v2, DEFAULT_DIV_SCALE);
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
	 *
	 * @param v1 被除数
	 * @param v2 除数
	 * @return 两个参数的商
	 */
	public static double div(double v1, float v2) {
		return div(v1, v2, DEFAULT_DIV_SCALE);
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
	 *
	 * @param v1 被除数
	 * @param v2 除数
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2) {
		return div(v1, v2, DEFAULT_DIV_SCALE);
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
	 *
	 * @param v1 被除数
	 * @param v2 除数
	 * @return 两个参数的商
	 */
	public static double div(Double v1, Double v2) {
		return div(v1, v2, DEFAULT_DIV_SCALE);
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
	 *
	 * @param v1 被除数
	 * @param v2 除数
	 * @return 两个参数的商
	 */
	public static BigDecimal div(Number v1, Number v2) {
		return div(v1, v2, DEFAULT_DIV_SCALE);
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况的时候,精确到小数点后10位,后面的四舍五入
	 *
	 * @param v1 被除数
	 * @param v2 除数
	 * @return 两个参数的商
	 */
	public static BigDecimal div(String v1, String v2) {
		return div(v1, v2, DEFAULT_DIV_SCALE);
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
	 *
	 * @param v1    被除数
	 * @param v2    除数
	 * @param scale 精确度，如果为负值，取绝对值
	 * @return 两个参数的商
	 */
	public static double div(float v1, float v2, int scale) {
		return div(v1, v2, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
	 *
	 * @param v1    被除数
	 * @param v2    除数
	 * @param scale 精确度，如果为负值，取绝对值
	 * @return 两个参数的商
	 */
	public static double div(float v1, double v2, int scale) {
		return div(v1, v2, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
	 *
	 * @param v1    被除数
	 * @param v2    除数
	 * @param scale 精确度，如果为负值，取绝对值
	 * @return 两个参数的商
	 */
	public static double div(double v1, float v2, int scale) {
		return div(v1, v2, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
	 *
	 * @param v1    被除数
	 * @param v2    除数
	 * @param scale 精确度，如果为负值，取绝对值
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale) {
		return div(v1, v2, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
	 *
	 * @param v1    被除数
	 * @param v2    除数
	 * @param scale 精确度，如果为负值，取绝对值
	 * @return 两个参数的商
	 */
	public static double div(Double v1, Double v2, int scale) {
		return div(v1, v2, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
	 *
	 * @param v1    被除数
	 * @param v2    除数
	 * @param scale 精确度，如果为负值，取绝对值
	 * @return 两个参数的商
	 */
	public static BigDecimal div(Number v1, Number v2, int scale) {
		return div(v1, v2, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度,后面的四舍五入
	 *
	 * @param v1    被除数
	 * @param v2    除数
	 * @param scale 精确度，如果为负值，取绝对值
	 * @return 两个参数的商
	 */
	public static BigDecimal div(String v1, String v2, int scale) {
		return div(v1, v2, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
	 *
	 * @param v1           被除数
	 * @param v2           除数
	 * @param scale        精确度，如果为负值，取绝对值
	 * @param roundingMode 保留小数的模式 {@link RoundingMode}
	 * @return 两个参数的商
	 */
	public static double div(float v1, float v2, int scale, RoundingMode roundingMode) {
		return div(Float.toString(v1), Float.toString(v2), scale, roundingMode).doubleValue();
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
	 *
	 * @param v1           被除数
	 * @param v2           除数
	 * @param scale        精确度，如果为负值，取绝对值
	 * @param roundingMode 保留小数的模式 {@link RoundingMode}
	 * @return 两个参数的商
	 */
	public static double div(float v1, double v2, int scale, RoundingMode roundingMode) {
		return div(Float.toString(v1), Double.toString(v2), scale, roundingMode).doubleValue();
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
	 *
	 * @param v1           被除数
	 * @param v2           除数
	 * @param scale        精确度，如果为负值，取绝对值
	 * @param roundingMode 保留小数的模式 {@link RoundingMode}
	 * @return 两个参数的商
	 */
	public static double div(double v1, float v2, int scale, RoundingMode roundingMode) {
		return div(Double.toString(v1), Float.toString(v2), scale, roundingMode).doubleValue();
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
	 *
	 * @param v1           被除数
	 * @param v2           除数
	 * @param scale        精确度，如果为负值，取绝对值
	 * @param roundingMode 保留小数的模式 {@link RoundingMode}
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale, RoundingMode roundingMode) {
		return div(Double.toString(v1), Double.toString(v2), scale, roundingMode).doubleValue();
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
	 *
	 * @param v1           被除数
	 * @param v2           除数
	 * @param scale        精确度，如果为负值，取绝对值
	 * @param roundingMode 保留小数的模式 {@link RoundingMode}
	 * @return 两个参数的商
	 */
	public static double div(Double v1, Double v2, int scale, RoundingMode roundingMode) {
		//noinspection RedundantCast
		return div((Number) v1, (Number) v2, scale, roundingMode).doubleValue();
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
	 *
	 * @param v1           被除数
	 * @param v2           除数
	 * @param scale        精确度，如果为负值，取绝对值
	 * @param roundingMode 保留小数的模式 {@link RoundingMode}
	 * @return 两个参数的商
	 */
	public static BigDecimal div(Number v1, Number v2, int scale, RoundingMode roundingMode) {
		if (v1 instanceof BigDecimal && v2 instanceof BigDecimal) {
			return div((BigDecimal) v1, (BigDecimal) v2, scale, roundingMode);
		}
		return div(Objs.toString(v1, null), Objs.toString(v2, null), scale, roundingMode);
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
	 *
	 * @param v1           被除数
	 * @param v2           除数
	 * @param scale        精确度，如果为负值，取绝对值
	 * @param roundingMode 保留小数的模式 {@link RoundingMode}
	 * @return 两个参数的商
	 */
	public static BigDecimal div(String v1, String v2, int scale, RoundingMode roundingMode) {
		return div(toBigDecimal(v1), toBigDecimal(v2), scale, roundingMode);
	}

	/**
	 * 提供(相对)精确的除法运算,当发生除不尽的情况时,由scale指定精确度
	 *
	 * @param v1           被除数
	 * @param v2           除数
	 * @param scale        精确度，如果为负值，取绝对值
	 * @param roundingMode 保留小数的模式 {@link RoundingMode}
	 * @return 两个参数的商
	 */
	public static BigDecimal div(BigDecimal v1, BigDecimal v2, int scale, RoundingMode roundingMode) {
		Assertions.assertNotNull(v2, "Divisor must be not null !");
		if (null == v1) {
			return BigDecimal.ZERO;
		}
		if (scale < 0) {
			scale = -scale;
		}
		return v1.divide(v2, scale, roundingMode);
	}

	// endregion

	// region round

	/**
	 * 保留固定位数小数<br>
	 * 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br>
	 * 例如保留2位小数：123.456789 =》 123.46
	 *
	 * @param v     值
	 * @param scale 保留小数位数
	 * @return 新值
	 */
	public static BigDecimal round(double v, int scale) {
		return round(v, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 保留固定位数小数<br>
	 * 采用四舍五入策略 {@link RoundingMode#HALF_UP}<br>
	 * 例如保留2位小数：123.456789 =》 123.46
	 *
	 * @param v     值
	 * @param scale 保留小数位数
	 * @return 新值
	 */
	public static String roundStr(double v, int scale) {
		return round(v, scale).toPlainString();
	}

	/**
	 * @see BigDecimals#round(String, int)
	 */
	public static BigDecimal round(String numberStr, int scale) {
		return BigDecimals.round(numberStr, scale);
	}

	/**
	 * @see BigDecimals#round(BigDecimal, int)
	 */
	public static BigDecimal round(BigDecimal number, int scale) {
		return BigDecimals.round(number, scale);
	}


	/**
	 * 保留固定位数小数<br>
	 * 例如保留四位小数：123.456789 =》 123.4567
	 *
	 * @param v            值
	 * @param scale        保留小数位数
	 * @param roundingMode 保留小数的模式 {@link RoundingMode}
	 * @return 新值
	 */
	public static BigDecimal round(double v, int scale, RoundingMode roundingMode) {
		return round(Double.toString(v), scale, roundingMode);
	}

	/**
	 * 保留固定位数小数<br>
	 * 例如保留四位小数：123.456789 =》 123.4567
	 *
	 * @param v            值
	 * @param scale        保留小数位数
	 * @param roundingMode 保留小数的模式 {@link RoundingMode}
	 * @return 新值
	 */
	public static String roundStr(double v, int scale, RoundingMode roundingMode) {
		return round(v, scale, roundingMode).toPlainString();
	}

	/**
	 * @see BigDecimals#round(String, int, RoundingMode)
	 */
	public static BigDecimal round(String numberStr, int scale, RoundingMode roundingMode) {
		return BigDecimals.round(numberStr, scale, roundingMode);
	}

	/**
	 * @see BigDecimals#round(BigDecimal, int, RoundingMode)
	 */
	public static BigDecimal round(BigDecimal number, int scale, RoundingMode roundingMode) {
		return BigDecimals.round(number, scale, roundingMode);
	}

	/**
	 * @see BigDecimals#roundHalfEven(Number, int)
	 */
	public static BigDecimal roundHalfEven(Number number, int scale) {
		return BigDecimals.roundHalfEven(number, scale);
	}

	/**
	 * @see BigDecimals#roundHalfEven(BigDecimal, int)
	 */
	public static BigDecimal roundHalfEven(BigDecimal value, int scale) {
		return BigDecimals.roundHalfEven(value, scale);
	}

	/**
	 * @see BigDecimals#roundDown(Number, int)
	 */
	public static BigDecimal roundDown(Number number, int scale) {
		return BigDecimals.roundDown(number, scale);
	}

	/**
	 * @see BigDecimals#roundDown(BigDecimal, int)
	 */
	public static BigDecimal roundDown(BigDecimal value, int scale) {
		return BigDecimals.roundDown(value, scale);
	}

	// endregion

	// region decimalFormat

	/**
	 * 格式化double<br>
	 * 对 {@link DecimalFormat} 做封装<br>
	 *
	 * @param pattern 格式 格式中主要以 # 和 0 两种占位符号来指定数字长度。0 表示如果位数不足则以 0 填充，# 表示只要有可能就把数字拉上这个位置。<br>
	 *                <ul>
	 *                <li>0 =》 取一位整数</li>
	 *                <li>0.00 =》 取一位整数和两位小数</li>
	 *                <li>00.000 =》 取两位整数和三位小数</li>
	 *                <li># =》 取所有整数部分</li>
	 *                <li>#.##% =》 以百分比方式计数，并取两位小数</li>
	 *                <li>#.#####E0 =》 显示为科学计数法，并取五位小数</li>
	 *                <li>,### =》 每三位以逗号进行分隔，例如：299,792,458</li>
	 *                <li>光速大小为每秒,###米 =》 将格式嵌入文本</li>
	 *                </ul>
	 * @param value   值
	 * @return 格式化后的值
	 */
	public static String decimalFormat(String pattern, double value) {
		Assertions.assertTrue(isValid(value), "value is NaN or Infinite!");
		return new DecimalFormat(pattern).format(value);
	}

	/**
	 * 格式化double<br>
	 * 对 {@link DecimalFormat} 做封装<br>
	 *
	 * @param pattern 格式 格式中主要以 # 和 0 两种占位符号来指定数字长度。0 表示如果位数不足则以 0 填充，# 表示只要有可能就把数字拉上这个位置。<br>
	 *                <ul>
	 *                <li>0 =》 取一位整数</li>
	 *                <li>0.00 =》 取一位整数和两位小数</li>
	 *                <li>00.000 =》 取两位整数和三位小数</li>
	 *                <li># =》 取所有整数部分</li>
	 *                <li>#.##% =》 以百分比方式计数，并取两位小数</li>
	 *                <li>#.#####E0 =》 显示为科学计数法，并取五位小数</li>
	 *                <li>,### =》 每三位以逗号进行分隔，例如：299,792,458</li>
	 *                <li>光速大小为每秒,###米 =》 将格式嵌入文本</li>
	 *                </ul>
	 * @param value   值
	 * @return 格式化后的值
	 */
	public static String decimalFormat(String pattern, long value) {
		return new DecimalFormat(pattern).format(value);
	}

	/**
	 * 格式化double<br>
	 * 对 {@link DecimalFormat} 做封装<br>
	 *
	 * @param pattern 格式 格式中主要以 # 和 0 两种占位符号来指定数字长度。0 表示如果位数不足则以 0 填充，# 表示只要有可能就把数字拉上这个位置。<br>
	 *                <ul>
	 *                <li>0 =》 取一位整数</li>
	 *                <li>0.00 =》 取一位整数和两位小数</li>
	 *                <li>00.000 =》 取两位整数和三位小数</li>
	 *                <li># =》 取所有整数部分</li>
	 *                <li>#.##% =》 以百分比方式计数，并取两位小数</li>
	 *                <li>#.#####E0 =》 显示为科学计数法，并取五位小数</li>
	 *                <li>,### =》 每三位以逗号进行分隔，例如：299,792,458</li>
	 *                <li>光速大小为每秒,###米 =》 将格式嵌入文本</li>
	 *                </ul>
	 * @param value   值，支持BigDecimal、BigInteger、Number等类型
	 * @return 格式化后的值
	 */
	public static String decimalFormat(String pattern, Object value) {
		return decimalFormat(pattern, value, null);
	}

	/**
	 * 格式化double<br>
	 * 对 {@link DecimalFormat} 做封装<br>
	 *
	 * @param pattern      格式 格式中主要以 # 和 0 两种占位符号来指定数字长度。0 表示如果位数不足则以 0 填充，# 表示只要有可能就把数字拉上这个位置。<br>
	 *                     <ul>
	 *                     <li>0 =》 取一位整数</li>
	 *                     <li>0.00 =》 取一位整数和两位小数</li>
	 *                     <li>00.000 =》 取两位整数和三位小数</li>
	 *                     <li># =》 取所有整数部分</li>
	 *                     <li>#.##% =》 以百分比方式计数，并取两位小数</li>
	 *                     <li>#.#####E0 =》 显示为科学计数法，并取五位小数</li>
	 *                     <li>,### =》 每三位以逗号进行分隔，例如：299,792,458</li>
	 *                     <li>光速大小为每秒,###米 =》 将格式嵌入文本</li>
	 *                     </ul>
	 * @param value        值，支持BigDecimal、BigInteger、Number等类型
	 * @param roundingMode 保留小数的方式枚举
	 * @return 格式化后的值
	 */
	public static String decimalFormat(String pattern, Object value, RoundingMode roundingMode) {
		if (value instanceof Number) {
			Assertions.assertTrue(isValidNumber((Number) value), "value is NaN or Infinite!");
		}
		final DecimalFormat decimalFormat = new DecimalFormat(pattern);
		if (null != roundingMode) {
			decimalFormat.setRoundingMode(roundingMode);
		}
		return decimalFormat.format(value);
	}

	/**
	 * 格式化金额输出，每三位用逗号分隔
	 *
	 * @param value 金额
	 * @return 格式化后的值
	 */
	public static String decimalFormatMoney(double value) {
		return decimalFormat(",##0.00", value);
	}

	/**
	 * 格式化百分比，小数采用四舍五入方式
	 *
	 * @param number 值
	 * @param scale  保留小数位数
	 * @return 百分比
	 */
	public static String formatPercent(double number, int scale) {
		final NumberFormat format = NumberFormat.getPercentInstance();
		format.setMaximumFractionDigits(scale);
		return format.format(number);
	}

	// endregion

	// region isXXX

	/**
	 * 是否为数字，支持包括：
	 *
	 * <pre>
	 * 1、10进制
	 * 2、16进制数字（0x开头）
	 * 3、科学计数法形式（1234E3）
	 * 4、类型标识形式（123D）
	 * 5、正负数标识形式（+123、-234）
	 * </pre>
	 *
	 * @param str 字符串值
	 * @return 是否为数字
	 */
	public static boolean isNumber(CharSequence str) {
		if (Strings.isBlank(str)) {
			return false;
		}
		char[] chars = str.toString().toCharArray();
		int sz = chars.length;
		boolean hasExp = false;
		boolean hasDecPoint = false;
		boolean allowSigns = false;
		boolean foundDigit = false;
		// deal with any possible sign up front
		int start = (chars[0] == '-' || chars[0] == '+') ? 1 : 0;
		if (sz > start + 1) {
			if (chars[start] == '0' && (chars[start + 1] == 'x' || chars[start + 1] == 'X')) {
				int i = start + 2;
				if (i == sz) {
					return false; // str == "0x"
				}
				// checking hex (it can't be anything else)
				for (; i < chars.length; i++) {
					if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f') && (chars[i] < 'A' || chars[i] > 'F')) {
						return false;
					}
				}
				return true;
			}
		}
		sz--; // don't want to loop to the last char, check it afterwords
		// for type qualifiers
		int i = start;
		// loop to the next to last char or to the last char if we need another digit to
		// make a valid number (e.g. chars[0..5] = "1234E")
		while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
			if (chars[i] >= '0' && chars[i] <= '9') {
				foundDigit = true;
				allowSigns = false;

			} else if (chars[i] == '.') {
				if (hasDecPoint || hasExp) {
					// two decimal points or dec in exponent
					return false;
				}
				hasDecPoint = true;
			} else if (chars[i] == 'e' || chars[i] == 'E') {
				// we've already taken care of hex.
				if (hasExp) {
					// two E's
					return false;
				}
				if (false == foundDigit) {
					return false;
				}
				hasExp = true;
				allowSigns = true;
			} else if (chars[i] == '+' || chars[i] == '-') {
				if (!allowSigns) {
					return false;
				}
				allowSigns = false;
				foundDigit = false; // we need a digit after the E
			} else {
				return false;
			}
			i++;
		}
		if (i < chars.length) {
			if (chars[i] >= '0' && chars[i] <= '9') {
				// no type qualifier, OK
				return true;
			}
			if (chars[i] == 'e' || chars[i] == 'E') {
				// can't have an E at the last byte
				return false;
			}
			if (chars[i] == '.') {
				if (hasDecPoint || hasExp) {
					// two decimal points or dec in exponent
					return false;
				}
				// single trailing decimal point after non-exponent is ok
				return foundDigit;
			}
			if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
				return foundDigit;
			}
			if (chars[i] == 'l' || chars[i] == 'L') {
				// not allowing L with an exponent
				return foundDigit && !hasExp;
			}
			// last character is illegal
			return false;
		}
		// allowSigns is true iff the val ends in 'E'
		// found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
		return false == allowSigns && foundDigit;
	}

	/**
	 * 判断String是否是整数<br>
	 * 支持10进制
	 *
	 * @param s String
	 * @return 是否为整数
	 */
	public static boolean isInteger(String s) {
		if (Strings.isBlank(s)) {
			return false;
		}
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * 判断字符串是否是Long类型<br>
	 * 支持10进制
	 *
	 * @param s String
	 * @return 是否为{@link Long}类型
	 */
	public static boolean isLong(String s) {
		if (Strings.isBlank(s)) {
			return false;
		}
		try {
			Long.parseLong(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * 判断字符串是否是浮点数
	 *
	 * @param s String
	 * @return 是否为{@link Double}类型
	 */
	public static boolean isDouble(String s) {
		if (Strings.isBlank(s)) {
			return false;
		}
		try {
			Double.parseDouble(s);
		} catch (NumberFormatException ignore) {
			return false;
		}
		return s.contains(".");
	}

	/**
	 * 是否是质数（素数）<br>
	 * 质数表的质数又称素数。指整数在一个大于1的自然数中,除了1和此整数自身外,没法被其他自然数整除的数。
	 *
	 * @param n 数字
	 * @return 是否是质数
	 */
	public static boolean isPrime(int n) {
		Assertions.assertTrue(n > 1, "The number must be > 1");
		for (int i = 2; i <= Math.sqrt(n); i++) {
			if (n % i == 0) {
				return false;
			}
		}
		return true;
	}

	// endregion

	// region generate

	/**
	 * 生成不重复随机数 根据给定的最小数字和最大数字，以及随机数的个数，产生指定的不重复的数组
	 *
	 * @param begin 最小数字（包含该数）
	 * @param end   最大数字（不包含该数）
	 * @param size  指定产生随机数的个数
	 * @return 随机int数组
	 */
	public static int[] generateRandomNumber(int begin, int end, int size) {
		// 种子你可以随意生成，但不能重复
		final int[] seed = ObjectArrays.range(begin, end);
		return generateRandomNumber(begin, end, size, seed);
	}

	/**
	 * 生成不重复随机数 根据给定的最小数字和最大数字，以及随机数的个数，产生指定的不重复的数组
	 *
	 * @param begin 最小数字（包含该数）
	 * @param end   最大数字（不包含该数）
	 * @param size  指定产生随机数的个数
	 * @param seed  种子，用于取随机数的int池
	 * @return 随机int数组
	 */
	public static int[] generateRandomNumber(int begin, int end, int size, int[] seed) {
		if (begin > end) {
			int temp = begin;
			begin = end;
			end = temp;
		}
		// 加入逻辑判断，确保begin<end并且size不能大于该表示范围
		Assertions.assertTrue((end - begin) >= size, "Size is larger than range between begin and end!");
		Assertions.assertTrue(seed.length >= size, "Size is larger than seed size!");

		final int[] ranArr = new int[size];
		// 数量你可以自己定义。
		for (int i = 0; i < size; i++) {
			// 得到一个位置
			int j = Randoms.randomInt(seed.length - i);
			// 得到那个位置的数值
			ranArr[i] = seed[j];
			// 将最后一个未用的数字放到这里
			seed[j] = seed[seed.length - 1 - i];
		}
		return ranArr;
	}

	/**
	 * 生成不重复随机数 根据给定的最小数字和最大数字，以及随机数的个数，产生指定的不重复的数组
	 *
	 * @param begin 最小数字（包含该数）
	 * @param end   最大数字（不包含该数）
	 * @param size  指定产生随机数的个数
	 * @return 随机int数组
	 */
	public static Integer[] generateBySet(int begin, int end, int size) {
		if (begin > end) {
			int temp = begin;
			begin = end;
			end = temp;
		}
		// 加入逻辑判断，确保begin<end并且size不能大于该表示范围
		if ((end - begin) < size) {
			throw new IllegalArgumentException("Size is larger than range between begin and end!");
		}

		Set<Integer> set = new HashSet<>(size, 1);
		while (set.size() < size) {
			set.add(begin + Randoms.randomInt(end - begin));
		}

		return set.toArray(new Integer[0]);
	}

	// endregion

	// region range

	/**
	 * 从0开始给定范围内的整数列表，步进为1
	 *
	 * @param stop 结束（包含）
	 * @return 整数列表
	 */
	public static int[] range(int stop) {
		return range(0, stop);
	}

	/**
	 * 给定范围内的整数列表，步进为1
	 *
	 * @param start 开始（包含）
	 * @param stop  结束（包含）
	 * @return 整数列表
	 */
	public static int[] range(int start, int stop) {
		return range(start, stop, 1);
	}

	/**
	 * 给定范围内的整数列表
	 *
	 * @param start 开始（包含）
	 * @param stop  结束（包含）
	 * @param step  步进
	 * @return 整数列表
	 */
	public static int[] range(int start, int stop, int step) {
		if (start < stop) {
			step = Math.abs(step);
		} else if (start > stop) {
			step = -Math.abs(step);
		} else {// start == end
			return new int[]{start};
		}

		int size = Math.abs((stop - start) / step) + 1;
		int[] values = new int[size];
		int index = 0;
		for (int i = start; (step > 0) ? i <= stop : i >= stop; i += step) {
			values[index] = i;
			index++;
		}
		return values;
	}

	/**
	 * 将给定范围内的整数添加到已有集合中，步进为1
	 *
	 * @param start  开始（包含）
	 * @param stop   结束（包含）
	 * @param values 集合
	 * @return 集合
	 */
	public static Collection<Integer> appendRange(int start, int stop, Collection<Integer> values) {
		return appendRange(start, stop, 1, values);
	}

	/**
	 * 将给定范围内的整数添加到已有集合中
	 *
	 * @param start  开始（包含）
	 * @param stop   结束（包含）
	 * @param step   步进
	 * @param values 集合
	 * @return 集合
	 */
	public static Collection<Integer> appendRange(int start, int stop, int step, Collection<Integer> values) {
		if (start < stop) {
			step = Math.abs(step);
		} else if (start > stop) {
			step = -Math.abs(step);
		} else {// start == end
			values.add(start);
			return values;
		}

		for (int i = start; (step > 0) ? i <= stop : i >= stop; i += step) {
			values.add(i);
		}
		return values;
	}

	// endregion range

	// region calc

	/**
	 * 计算阶乘
	 * <p>
	 * n! = n * (n-1) * ... * 2 * 1
	 * </p>
	 *
	 * @param n 阶乘起始
	 * @return 结果
	 */
	public static BigInteger factorial(BigInteger n) {
		if (n.equals(BigInteger.ZERO)) {
			return BigInteger.ONE;
		}
		return factorial(n, BigInteger.ZERO);
	}

	/**
	 * 计算范围阶乘
	 * <p>
	 * factorial(start, end) = start * (start - 1) * ... * (end + 1)
	 * </p>
	 *
	 * @param start 阶乘起始（包含）
	 * @param end   阶乘结束，必须小于起始（不包括）
	 * @return 结果
	 */
	public static BigInteger factorial(BigInteger start, BigInteger end) {
		Assertions.assertNotNull(start, "Factorial start must be not null!");
		Assertions.assertNotNull(end, "Factorial end must be not null!");
		if (start.compareTo(BigInteger.ZERO) < 0 || end.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException(Strings.format("Factorial start and end both must be > 0, but got start={}, end={}", start, end));
		}

		if (start.equals(BigInteger.ZERO)) {
			start = BigInteger.ONE;
		}

		if (end.compareTo(BigInteger.ONE) < 0) {
			end = BigInteger.ONE;
		}

		BigInteger result = start;
		end = end.add(BigInteger.ONE);
		while (start.compareTo(end) > 0) {
			start = start.subtract(BigInteger.ONE);
			result = result.multiply(start);
		}
		return result;
	}

	/**
	 * 计算范围阶乘
	 * <p>
	 * factorial(start, end) = start * (start - 1) * ... * (end + 1)
	 * </p>
	 *
	 * @param start 阶乘起始（包含）
	 * @param end   阶乘结束，必须小于起始（不包括）
	 * @return 结果
	 */
	public static long factorial(long start, long end) {
		// 负数没有阶乘
		if (start < 0 || end < 0) {
			throw new IllegalArgumentException(Strings.format("Factorial start and end both must be >= 0, but got start={}, end={}", start, end));
		}
		if (0L == start || start == end) {
			return 1L;
		}
		if (start < end) {
			return 0L;
		}
		return factorialMultiplyAndCheck(start, factorial(start - 1, end));
	}

	/**
	 * 计算范围阶乘中校验中间的计算是否存在溢出，factorial提前做了负数和0的校验，因此这里没有校验数字的正负
	 *
	 * @param a 乘数
	 * @param b 被乘数
	 * @return 如果 a * b的结果没有溢出直接返回，否则抛出异常
	 */
	private static long factorialMultiplyAndCheck(long a, long b) {
		if (a <= Long.MAX_VALUE / b) {
			return a * b;
		}
		throw new IllegalArgumentException(Strings.format("Overflow in multiplication: {} * {}", a, b));
	}

	/**
	 * 计算阶乘
	 * <p>
	 * n! = n * (n-1) * ... * 2 * 1
	 * </p>
	 *
	 * @param n 阶乘起始
	 * @return 结果
	 */
	public static long factorial(long n) {
		if (n < 0 || n > 20) {
			throw new IllegalArgumentException(Strings.format("Factorial must have n >= 0 and n <= 20 for n!, but got n = {}", n));
		}
		return FACTORIALS[(int) n];
	}

	/**
	 * 平方根算法<br>
	 * 推荐使用 {@link Math#sqrt(double)}
	 *
	 * @param x 值
	 * @return 平方根
	 */
	public static long sqrt(long x) {
		long y = 0;
		long b = (~Long.MAX_VALUE) >>> 1;
		while (b > 0) {
			if (x >= y + b) {
				x -= y + b;
				y >>= 1;
				y += b;
			} else {
				y >>= 1;
			}
			b >>= 2;
		}
		return y;
	}

	/**
	 * 可以用于计算双色球、大乐透注数的方法<br>
	 * 比如大乐透35选5可以这样调用processMultiple(7,5); 就是数学中的：C75=7*6/2*1
	 *
	 * @param selectNum 选中小球个数
	 * @param minNum    最少要选中多少个小球
	 * @return 注数
	 */
	public static int processMultiple(int selectNum, int minNum) {
		int result;
		result = mathSubNode(selectNum, minNum) / mathNode(selectNum - minNum);
		return result;
	}

	private static int mathSubNode(int selectNum, int minNum) {
		if (selectNum == minNum) {
			return 1;
		} else {
			return selectNum * mathSubNode(selectNum - 1, minNum);
		}
	}

	private static int mathNode(int selectNum) {
		if (selectNum == 0) {
			return 1;
		} else {
			return selectNum * mathNode(selectNum - 1);
		}
	}

	/**
	 * 最大公约数
	 *
	 * @param m 第一个值
	 * @param n 第二个值
	 * @return 最大公约数
	 */
	public static int divisor(int m, int n) {
		while (m % n != 0) {
			int temp = m % n;
			m = n;
			n = temp;
		}
		return n;
	}

	/**
	 * 最小公倍数
	 *
	 * @param m 第一个值
	 * @param n 第二个值
	 * @return 最小公倍数
	 */
	public static int multiple(int m, int n) {
		return m * n / divisor(m, n);
	}

	// endregion


	// region compare

	/**
	 * 比较两个值的大小
	 *
	 * @param x 第一个值
	 * @param y 第二个值
	 * @return x==y返回0，x&lt;y返回小于0的数，x&gt;y返回大于0的数
	 * @see Character#compare(char, char)
	 */
	public static int compare(char x, char y) {
		return Character.compare(x, y);
	}

	/**
	 * 比较两个值的大小
	 *
	 * @param x 第一个值
	 * @param y 第二个值
	 * @return x==y返回0，x&lt;y返回小于0的数，x&gt;y返回大于0的数
	 * @see Double#compare(double, double)
	 */
	public static int compare(double x, double y) {
		return Double.compare(x, y);
	}

	/**
	 * 比较两个值的大小
	 *
	 * @param x 第一个值
	 * @param y 第二个值
	 * @return x==y返回0，x&lt;y返回小于0的数，x&gt;y返回大于0的数
	 * @see Integer#compare(int, int)
	 */
	public static int compare(int x, int y) {
		return Integer.compare(x, y);
	}

	/**
	 * 比较两个值的大小
	 *
	 * @param x 第一个值
	 * @param y 第二个值
	 * @return x==y返回0，x&lt;y返回小于0的数，x&gt;y返回大于0的数
	 * @see Long#compare(long, long)
	 */
	public static int compare(long x, long y) {
		return Long.compare(x, y);
	}

	/**
	 * 比较两个值的大小
	 *
	 * @param x 第一个值
	 * @param y 第二个值
	 * @return x==y返回0，x&lt;y返回小于0的数，x&gt;y返回大于0的数
	 * @see Short#compare(short, short)
	 */
	public static int compare(short x, short y) {
		return Short.compare(x, y);
	}

	/**
	 * 比较两个值的大小
	 *
	 * @param x 第一个值
	 * @param y 第二个值
	 * @return x==y返回0，x&lt;y返回-1，x&gt;y返回1
	 * @see Byte#compare(byte, byte)
	 */
	public static int compare(byte x, byte y) {
		return Byte.compare(x, y);
	}

	/**
	 * 比较大小，参数1 &gt; 参数2 返回true
	 *
	 * @param bigNum1 数字1
	 * @param bigNum2 数字2
	 * @return 是否大于
	 */
	public static boolean isGreater(BigDecimal bigNum1, BigDecimal bigNum2) {
		Objects.requireNonNull(bigNum1);
		Objects.requireNonNull(bigNum2);
		return bigNum1.compareTo(bigNum2) > 0;
	}

	/**
	 * 比较大小，参数1 &gt;= 参数2 返回true
	 *
	 * @param bigNum1 数字1
	 * @param bigNum2 数字2
	 * @return 是否大于等于
	 */
	public static boolean isGreaterOrEqual(BigDecimal bigNum1, BigDecimal bigNum2) {
		Objects.requireNonNull(bigNum1);
		Objects.requireNonNull(bigNum2);
		return bigNum1.compareTo(bigNum2) >= 0;
	}

	/**
	 * 比较大小，参数1 &lt; 参数2 返回true
	 *
	 * @param bigNum1 数字1
	 * @param bigNum2 数字2
	 * @return 是否小于
	 */
	public static boolean isLess(BigDecimal bigNum1, BigDecimal bigNum2) {
		Objects.requireNonNull(bigNum1);
		Objects.requireNonNull(bigNum2);
		return bigNum1.compareTo(bigNum2) < 0;
	}

	/**
	 * 比较大小，参数1&lt;=参数2 返回true
	 *
	 * @param bigNum1 数字1
	 * @param bigNum2 数字2
	 * @return 是否小于等于
	 */
	public static boolean isLessOrEqual(BigDecimal bigNum1, BigDecimal bigNum2) {
		Objects.requireNonNull(bigNum1);
		Objects.requireNonNull(bigNum2);
		return bigNum1.compareTo(bigNum2) <= 0;
	}

	/**
	 * 检查值是否在指定范围内
	 *
	 * @param value      值
	 * @param minInclude 最小值（包含）
	 * @param maxInclude 最大值（包含）
	 * @return 经过检查后的值
	 */
	public static boolean isIn(final BigDecimal value, final BigDecimal minInclude, final BigDecimal maxInclude) {
		Objects.requireNonNull(value);
		Objects.requireNonNull(minInclude);
		Objects.requireNonNull(maxInclude);
		return isGreaterOrEqual(value, minInclude) && isLessOrEqual(value, maxInclude);
	}

	/**
	 * 比较大小，值相等 返回true<br>
	 * 此方法通过调用{@link Double#doubleToLongBits(double)}方法来判断是否相等<br>
	 * 此方法判断值相等时忽略精度的，即0.00 == 0
	 *
	 * @param num1 数字1
	 * @param num2 数字2
	 * @return 是否相等
	 */
	public static boolean equals(double num1, double num2) {
		return Double.doubleToLongBits(num1) == Double.doubleToLongBits(num2);
	}

	/**
	 * 比较大小，值相等 返回true<br>
	 * 此方法通过调用{@link Float#floatToIntBits(float)}方法来判断是否相等<br>
	 * 此方法判断值相等时忽略精度的，即0.00 == 0
	 *
	 * @param num1 数字1
	 * @param num2 数字2
	 * @return 是否相等
	 */
	public static boolean equals(float num1, float num2) {
		return Float.floatToIntBits(num1) == Float.floatToIntBits(num2);
	}

	/**
	 * 比较大小，值相等 返回true<br>
	 * 此方法修复传入long型数据由于没有本类型重载方法,导致数据精度丢失
	 *
	 * @param num1 数字1
	 * @param num2 数字2
	 * @return 是否相等
	 */
	public static boolean equals(long num1, long num2) {
		return num1 == num2;
	}

	/**
	 * 比较数字值是否相等，相等返回{@code true}<br>
	 * 需要注意的是{@link BigDecimal}需要特殊处理<br>
	 * BigDecimal使用compareTo方式判断，因为使用equals方法也判断小数位数，如2.0和2.00就不相等，<br>
	 * 此方法判断值相等时忽略精度的，即0.00 == 0
	 *
	 * <ul>
	 *     <li>如果用户提供两个Number都是{@link BigDecimal}，则通过调用{@link BigDecimal#compareTo(BigDecimal)}方法来判断是否相等</li>
	 *     <li>其他情况调用{@link Number#equals(Object)}比较</li>
	 * </ul>
	 *
	 * @param number1 数字1
	 * @param number2 数字2
	 * @return 是否相等
	 * @see Objects#equals(Object, Object)
	 */
	public static boolean equals(final Number number1, final Number number2) {
		if (number1 instanceof BigDecimal && number2 instanceof BigDecimal) {
			// BigDecimal使用compareTo方式判断，因为使用equals方法也判断小数位数，如2.0和2.00就不相等
			return equals((BigDecimal) number1, (BigDecimal) number2);
		}
		return Objects.equals(number1, number2);
	}

	/**
	 * 比较大小，值相等 返回true<br>
	 * 此方法通过调用{@link BigDecimal#compareTo(BigDecimal)}方法来判断是否相等<br>
	 * 此方法判断值相等时忽略精度的，即0.00 == 0
	 *
	 * @param bigNum1 数字1
	 * @param bigNum2 数字2
	 * @return 是否相等
	 */
	public static boolean equals(BigDecimal bigNum1, BigDecimal bigNum2) {
		// noinspection all
		if (bigNum1 == bigNum2) {
			return true;
		}
		if (bigNum1 == null || bigNum2 == null) {
			return false;
		}
		return 0 == bigNum1.compareTo(bigNum2);
	}

	/**
	 * 比较两个字符是否相同
	 *
	 * @param c1         字符1
	 * @param c2         字符2
	 * @param ignoreCase 是否忽略大小写
	 * @return 是否相同
	 * @see Chars#equals(char, char, boolean)
	 */
	public static boolean equals(char c1, char c2, boolean ignoreCase) {
		return Chars.equals(c1, c2, ignoreCase);
	}

	/**
	 * 取最小值
	 *
	 * @param <T>         元素类型
	 * @param numberArray 数字数组
	 * @return 最小值
	 * @see ObjectArrays#min(Comparable[])
	 */
	public static <T extends Comparable<? super T>> T min(T[] numberArray) {
		return ObjectArrays.min(numberArray);
	}

	/**
	 * 取最小值
	 *
	 * @param numberArray 数字数组
	 * @return 最小值
	 * @see ObjectArrays#min(long...)
	 */
	public static long min(long... numberArray) {
		return ObjectArrays.min(numberArray);
	}

	/**
	 * 取最小值
	 *
	 * @param numberArray 数字数组
	 * @return 最小值
	 * @see ObjectArrays#min(int...)
	 */
	public static int min(int... numberArray) {
		return ObjectArrays.min(numberArray);
	}

	/**
	 * 取最小值
	 *
	 * @param numberArray 数字数组
	 * @return 最小值
	 * @see ObjectArrays#min(short...)
	 */
	public static short min(short... numberArray) {
		return ObjectArrays.min(numberArray);
	}

	/**
	 * 取最小值
	 *
	 * @param numberArray 数字数组
	 * @return 最小值
	 * @see ObjectArrays#min(double...)
	 */
	public static double min(double... numberArray) {
		return ObjectArrays.min(numberArray);
	}

	/**
	 * 取最小值
	 *
	 * @param numberArray 数字数组
	 * @return 最小值
	 * @see ObjectArrays#min(float...)
	 */
	public static float min(float... numberArray) {
		return ObjectArrays.min(numberArray);
	}

	/**
	 * 取最小值
	 *
	 * @param numberArray 数字数组
	 * @return 最小值
	 * @see ObjectArrays#min(Comparable[])
	 */
	public static BigDecimal min(BigDecimal... numberArray) {
		return ObjectArrays.min(numberArray);
	}

	/**
	 * 取最大值
	 *
	 * @param <T>         元素类型
	 * @param numberArray 数字数组
	 * @return 最大值
	 * @see ObjectArrays#max(Comparable[])
	 */
	public static <T extends Comparable<? super T>> T max(T[] numberArray) {
		return ObjectArrays.max(numberArray);
	}

	/**
	 * 取最大值
	 *
	 * @param numberArray 数字数组
	 * @return 最大值
	 * @see PrimitiveArrays#max(long...)
	 */
	public static long max(long... numberArray) {
		return PrimitiveArrays.max(numberArray);
	}

	/**
	 * 取最大值
	 *
	 * @param numberArray 数字数组
	 * @return 最大值
	 * @see PrimitiveArrays#max(int...)
	 */
	public static int max(int... numberArray) {
		return PrimitiveArrays.max(numberArray);
	}

	/**
	 * 取最大值
	 *
	 * @param numberArray 数字数组
	 * @return 最大值
	 * @see PrimitiveArrays#max(short...)
	 */
	public static short max(short... numberArray) {
		return PrimitiveArrays.max(numberArray);
	}

	/**
	 * 取最大值
	 *
	 * @param numberArray 数字数组
	 * @return 最大值
	 * @see PrimitiveArrays#max(double...)
	 */
	public static double max(double... numberArray) {
		return PrimitiveArrays.max(numberArray);
	}

	/**
	 * 取最大值
	 *
	 * @param numberArray 数字数组
	 * @return 最大值
	 * @see PrimitiveArrays#max(float...)
	 */
	public static float max(float... numberArray) {
		return PrimitiveArrays.max(numberArray);
	}

	/**
	 * 取最大值
	 *
	 * @param numberArray 数字数组
	 * @return 最大值
	 * @see ObjectArrays#max(Comparable[])
	 */
	public static BigDecimal max(BigDecimal... numberArray) {
		return ObjectArrays.max(numberArray);
	}

	// endregion

	// region convert

	/**
	 * 获得数字对应的二进制字符串
	 *
	 * @param number 数字
	 * @return 二进制字符串
	 */
	public static String toBinaryString(Number number) {
		if (number instanceof Long) {
			return Long.toBinaryString((Long) number);
		} else if (number instanceof Integer) {
			return Integer.toBinaryString((Integer) number);
		} else {
			return Long.toBinaryString(number.longValue());
		}
	}

	/**
	 * 数字转字符串<br>
	 * 调用{@link Number#toString()}，并去除尾小数点儿后多余的0
	 *
	 * @param number       A Number
	 * @param defaultValue 如果number参数为{@code null}，返回此默认值
	 * @return A String.
	 */
	public static String toString(Number number, String defaultValue) {
		return (null == number) ? defaultValue : toString(number);
	}

	/**
	 * 数字转字符串<br>
	 * 调用{@link Number#toString()}或 {@link BigDecimal#toPlainString()}，并去除尾小数点儿后多余的0
	 *
	 * @param number A Number
	 * @return A String.
	 */
	public static String toString(Number number) {
		return toString(number, true);
	}

	/**
	 * 数字转字符串<br>
	 * 调用{@link Number#toString()}或 {@link BigDecimal#toPlainString()}，并去除尾小数点儿后多余的0
	 *
	 * @param number               A Number
	 * @param isStripTrailingZeros 是否去除末尾多余0，例如5.0返回5
	 * @return A String.
	 */
	public static String toString(Number number, boolean isStripTrailingZeros) {
		Assertions.assertNotNull(number, "Number is null !");

		// BigDecimal单独处理，使用非科学计数法
		if (number instanceof BigDecimal) {
			return toString((BigDecimal) number, isStripTrailingZeros);
		}

		Assertions.assertTrue(isValidNumber(number), "Number is non-finite!");
		// 去掉小数点儿后多余的0
		String string = number.toString();
		if (isStripTrailingZeros) {
			if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && string.indexOf('E') < 0) {
				while (string.endsWith("0")) {
					string = string.substring(0, string.length() - 1);
				}
				if (string.endsWith(".")) {
					string = string.substring(0, string.length() - 1);
				}
			}
		}
		return string;
	}

	public static String toString(BigDecimal bigDecimal) {
		return BigDecimals.toString(bigDecimal);
	}

	public static String toString(BigDecimal bigDecimal, boolean isStripTrailingZeros) {
		return BigDecimals.toString(bigDecimal, isStripTrailingZeros);
	}

	public static BigDecimal toBigDecimal(Number number) {
		return BigDecimals.toBigDecimal(number);
	}

	public static BigDecimal toBigDecimal(String numberStr) {
		return BigDecimals.toBigDecimal(numberStr);
	}

	public static BigInteger toBigInteger(Number number) {
		return BigIntegers.toBigInteger(number);
	}

	public static BigInteger toBigInteger(String number) {
		return BigIntegers.toBigInteger(number);
	}

	public static BigInteger newBigInteger(String str) {
		return BigIntegers.newBigInteger(str);
	}

	/**
	 * 解析转换数字字符串为int型数字，规则如下：
	 *
	 * <pre>
	 * 1、0x开头的视为16进制数字
	 * 2、0开头的忽略开头的0
	 * 3、其它情况按照10进制转换
	 * 4、空串返回0
	 * 5、.123形式返回0（按照小于0的小数对待）
	 * 6、123.56截取小数点之前的数字，忽略小数部分
	 * </pre>
	 *
	 * @param number 数字，支持0x开头、0开头和普通十进制
	 * @return int
	 * @throws NumberFormatException 数字格式异常
	 */
	public static int parseInt(String number) throws NumberFormatException {
		if (Strings.isBlank(number)) {
			return 0;
		}

		if (Strings.indexOfIgnoreCase(number, "E") >= 0) {
			// 科学计数法忽略支持，科学计数法一般用于表示非常小和非常大的数字，这类数字转换为int后精度丢失，没有意义。
			throw new NumberFormatException(Strings.format("Unsupported int format: [{}]", number));
		}
		if (Strings.startsWithIgnoreCase(number, "0x")) {
			// 0x04表示16进制数
			return Integer.parseInt(number.substring(2), 16);
		}

		try {
			return Integer.parseInt(number);
		} catch (NumberFormatException e) {
			return parseNumber(number).intValue();
		}
	}

	/**
	 * 解析转换数字字符串为long型数字，规则如下：
	 *
	 * <pre>
	 * 1、0x开头的视为16进制数字
	 * 2、0开头的忽略开头的0
	 * 3、空串返回0
	 * 4、其它情况按照10进制转换
	 * 5、.123形式返回0（按照小于0的小数对待）
	 * 6、123.56截取小数点之前的数字，忽略小数部分
	 * </pre>
	 *
	 * @param number 数字，支持0x开头、0开头和普通十进制
	 * @return long
	 */
	public static long parseLong(String number) {
		if (Strings.isBlank(number)) {
			return 0L;
		}

		if (number.startsWith("0x")) {
			// 0x04表示16进制数
			return Long.parseLong(number.substring(2), 16);
		}

		try {
			return Long.parseLong(number);
		} catch (NumberFormatException e) {
			return parseNumber(number).longValue();
		}
	}

	/**
	 * 解析转换数字字符串为long型数字，规则如下：
	 *
	 * <pre>
	 * 1、0开头的忽略开头的0
	 * 2、空串返回0
	 * 3、其它情况按照10进制转换
	 * 4、.123形式返回0.123（按照小于0的小数对待）
	 * </pre>
	 *
	 * @param number 数字，支持0x开头、0开头和普通十进制
	 * @return long
	 */
	public static float parseFloat(String number) {
		if (Strings.isBlank(number)) {
			return 0f;
		}

		try {
			return Float.parseFloat(number);
		} catch (NumberFormatException e) {
			return parseNumber(number).floatValue();
		}
	}

	/**
	 * 解析转换数字字符串为long型数字，规则如下：
	 *
	 * <pre>
	 * 1、0开头的忽略开头的0
	 * 2、空串返回0
	 * 3、其它情况按照10进制转换
	 * 4、.123形式返回0.123（按照小于0的小数对待）
	 * </pre>
	 *
	 * @param number 数字，支持0x开头、0开头和普通十进制
	 * @return long
	 */
	public static double parseDouble(String number) {
		if (Strings.isBlank(number)) {
			return 0D;
		}

		try {
			return Double.parseDouble(number);
		} catch (NumberFormatException e) {
			return parseNumber(number).doubleValue();
		}
	}

	/**
	 * 将指定字符串转换为{@link Number} 对象<br>
	 * 此方法不支持科学计数法
	 *
	 * <p>
	 * 需要注意的是，在不同Locale下，数字的表示形式也是不同的，例如：<br>
	 * 德国、荷兰、比利时、丹麦、意大利、罗马尼亚和欧洲大多地区使用`,`区分小数<br>
	 * 也就是说，在这些国家地区，1.20表示120，而非1.2。
	 * </p>
	 *
	 * @param numberStr Number字符串
	 * @return Number对象
	 * @throws NumberFormatException 包装了{@link ParseException}，当给定的数字字符串无法解析时抛出
	 */
	public static Number parseNumber(String numberStr) throws NumberFormatException {
		if (Strings.startsWithIgnoreCase(numberStr, "0x")) {
			// 0x04表示16进制数
			return Long.parseLong(numberStr.substring(2), 16);
		} else if (numberStr.startsWith("+")) {
			numberStr = numberStr.substring(1);
		}

		try {
			final NumberFormat format = NumberFormat.getInstance();
			if (format instanceof DecimalFormat) {
				// issue#1818@Github
				// 当字符串数字超出double的长度时，会导致截断，此处使用BigDecimal接收
				((DecimalFormat) format).setParseBigDecimal(true);
			}
			return format.parse(numberStr);
		} catch (ParseException e) {
			final NumberFormatException nfe = new NumberFormatException(e.getMessage());
			nfe.initCause(e);
			throw nfe;
		}
	}

	/**
	 * 解析转换数字字符串为 {@link java.lang.Integer } 规则如下：
	 *
	 * <pre>
	 * 1、0x开头的视为16进制数字
	 * 2、0开头的忽略开头的0
	 * 3、其它情况按照10进制转换
	 * 4、空串返回0
	 * 5、.123形式返回0（按照小于0的小数对待）
	 * 6、123.56截取小数点之前的数字，忽略小数部分
	 * 7、解析失败返回默认值
	 * </pre>
	 *
	 * @param numberStr    数字字符串，支持0x开头、0开头和普通十进制
	 * @param defaultValue 如果解析失败, 将返回defaultValue, 允许null
	 * @return Integer
	 */
	public static Integer parseInt(String numberStr, Integer defaultValue) {
		if (Strings.isBlank(numberStr)) {
			return defaultValue;
		}

		try {
			return parseInt(numberStr);
		} catch (NumberFormatException ignore) {

		}

		return defaultValue;
	}

	/**
	 * 解析转换数字字符串为 {@link java.lang.Long } 规则如下：
	 *
	 * <pre>
	 * 1、0x开头的视为16进制数字
	 * 2、0开头的忽略开头的0
	 * 3、其它情况按照10进制转换
	 * 4、空串返回0
	 * 5、.123形式返回0（按照小于0的小数对待）
	 * 6、123.56截取小数点之前的数字，忽略小数部分
	 * 7、解析失败返回默认值
	 * </pre>
	 *
	 * @param numberStr    数字字符串，支持0x开头、0开头和普通十进制
	 * @param defaultValue 如果解析失败, 将返回defaultValue, 允许null
	 * @return Long
	 */
	public static Long parseLong(String numberStr, Long defaultValue) {
		if (Strings.isBlank(numberStr)) {
			return defaultValue;
		}

		try {
			return parseLong(numberStr);
		} catch (NumberFormatException ignore) {

		}

		return defaultValue;
	}

	/**
	 * 解析转换数字字符串为 {@link java.lang.Float } 规则如下：
	 *
	 * <pre>
	 * 1、0开头的忽略开头的0
	 * 2、空串返回0
	 * 3、其它情况按照10进制转换
	 * 4、.123形式返回0.123（按照小于0的小数对待）
	 * </pre>
	 *
	 * @param numberStr    数字字符串，支持0x开头、0开头和普通十进制
	 * @param defaultValue 如果解析失败, 将返回defaultValue, 允许null
	 * @return Float
	 */
	public static Float parseFloat(String numberStr, Float defaultValue) {
		if (Strings.isBlank(numberStr)) {
			return defaultValue;
		}

		try {
			return parseFloat(numberStr);
		} catch (NumberFormatException ignore) {

		}

		return defaultValue;
	}

	/**
	 * 解析转换数字字符串为 {@link java.lang.Double } 规则如下：
	 *
	 * <pre>
	 * 1、0开头的忽略开头的0
	 * 2、空串返回0
	 * 3、其它情况按照10进制转换
	 * 4、.123形式返回0.123（按照小于0的小数对待）
	 * </pre>
	 *
	 * @param numberStr    数字字符串，支持0x开头、0开头和普通十进制
	 * @param defaultValue 如果解析失败, 将返回defaultValue, 允许null
	 * @return Double
	 */
	public static Double parseDouble(String numberStr, Double defaultValue) {
		if (Strings.isBlank(numberStr)) {
			return defaultValue;
		}

		try {
			return parseDouble(numberStr);
		} catch (NumberFormatException ignore) {

		}

		return defaultValue;
	}

	/**
	 * 将指定字符串转换为{@link Number }
	 * 此方法不支持科学计数法
	 *
	 * @param numberStr    Number字符串
	 * @param defaultValue 如果解析失败, 将返回defaultValue, 允许null
	 * @return Number对象
	 */
	public static Number parseNumber(String numberStr, Number defaultValue) {
		if (Strings.isBlank(numberStr)) {
			return defaultValue;
		}

		try {
			return parseNumber(numberStr);
		} catch (NumberFormatException ignore) {

		}

		return defaultValue;
	}

	public static byte[] toBytes(int value) {
		return Ints.toBytes(value);
	}

	public static int toInt(byte[] bytes) {
		return Ints.toInt(bytes);
	}

	public static byte[] toUnsignedByteArray(BigInteger value) {
		return BigIntegers.toUnsignedByteArray(value);
	}

	public static byte[] toUnsignedByteArray(int length, BigInteger value) {
		return BigIntegers.toUnsignedByteArray(length, value);
	}

	public static BigInteger fromUnsignedByteArray(byte[] buf) {
		return BigIntegers.fromUnsignedByteArray(buf);
	}

	public static BigInteger fromUnsignedByteArray(byte[] buf, int off, int length) {
		return BigIntegers.fromUnsignedByteArray(buf, off, length);
	}

	/**
	 * Number值转换为double<br>
	 * float强制转换存在精度问题，此方法避免精度丢失
	 *
	 * @param value 被转换的float值
	 * @return double值
	 */
	public static double toDouble(Number value) {
		return Doubles.toDouble(value);
	}

	// endregion




	// region check

	/**
	 * 判断两个数字是否相邻，例如1和2相邻，1和3不相邻<br>
	 * 判断方法为做差取绝对值判断是否为1
	 *
	 * @param number1 数字1
	 * @param number2 数字2
	 * @return 是否相邻
	 */
	public static boolean isBeside(long number1, long number2) {
		return Math.abs(number1 - number2) == 1;
	}

	/**
	 * 判断两个数字是否相邻，例如1和2相邻，1和3不相邻<br>
	 * 判断方法为做差取绝对值判断是否为1
	 *
	 * @param number1 数字1
	 * @param number2 数字2
	 * @return 是否相邻
	 */
	public static boolean isBeside(int number1, int number2) {
		return Math.abs(number1 - number2) == 1;
	}

	/**
	 * 把给定的总数平均分成N份，返回每份的个数<br>
	 * 当除以分数有余数时每份+1
	 *
	 * @param total     总数
	 * @param partCount 份数
	 * @return 每份的个数
	 */
	public static int partValue(int total, int partCount) {
		return partValue(total, partCount, true);
	}

	/**
	 * 把给定的总数平均分成N份，返回每份的个数<br>
	 * 如果isPlusOneWhenHasRem为true，则当除以分数有余数时每份+1，否则丢弃余数部分
	 *
	 * @param total               总数
	 * @param partCount           份数
	 * @param isPlusOneWhenHasRem 在有余数时是否每份+1
	 * @return 每份的个数
	 */
	public static int partValue(int total, int partCount, boolean isPlusOneWhenHasRem) {
		int partValue = total / partCount;
		if (isPlusOneWhenHasRem && total % partCount > 0) {
			partValue++;
		}
		return partValue;
	}

	/**
	 * 判断一个整数是否是2的幂
	 *
	 * @param n 待验证的整数
	 * @return 如果n是2的幂返回true, 反之返回false
	 */
	public static boolean isPowerOfTwo(long n) {
		return (n > 0) && ((n & (n - 1)) == 0);
	}

	/**
	 * 检查是否为有效的数字<br>
	 * 检查Double和Float是否为无限大，或者Not a Number<br>
	 * 非数字类型和Null将返回true
	 *
	 * @param number 被检查类型
	 * @return 检查结果，非数字类型和Null将返回true
	 */
	public static boolean isValidNumber(Number number) {
		if (null == number) {
			return false;
		}
		if (number instanceof Double) {
			return (!((Double) number).isInfinite()) && (!((Double) number).isNaN());
		} else if (number instanceof Float) {
			return (!((Float) number).isInfinite()) && (!((Float) number).isNaN());
		}
		return true;
	}

	/**
	 * 检查是否为有效的数字<br>
	 * 检查double否为无限大，或者Not a Number（NaN）<br>
	 *
	 * @param number 被检查double
	 * @return 检查结果
	 */
	public static boolean isValid(double number) {
		return !(Double.isNaN(number) || Double.isInfinite(number));
	}

	/**
	 * 检查是否为有效的数字<br>
	 * 检查double否为无限大，或者Not a Number（NaN）<br>
	 *
	 * @param number 被检查double
	 * @return 检查结果
	 */
	public static boolean isValid(float number) {
		return !(Float.isNaN(number) || Float.isInfinite(number));
	}

	/**
	 * 检查是否为奇数<br>
	 *
	 * @param num 被判断的数值
	 * @return 是否是奇数
	 */
	public static boolean isOdd(int num) {
		return (num & 1) == 1;
	}

	/**
	 * 检查是否为偶数<br>
	 *
	 * @param num 被判断的数值
	 * @return 是否是偶数
	 */
	public static boolean isEven(int num) {
		return !isOdd(num);
	}

	// endregion


}
