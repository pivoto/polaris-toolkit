package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.date.Dates;
import io.polaris.core.lang.JavaType;
import io.polaris.core.string.Strings;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author Qt
 * @since 1.8
 */
public class NumberConverter<T extends Number> extends AbstractSimpleConverter<T> {
	protected final JavaType<T> targetType;

	public NumberConverter() {
		this.targetType = JavaType.of((Type) Number.class);
	}

	public NumberConverter(@Nonnull Class<T> targetType) {
		if (targetType == null) {
			throw new NullPointerException();
		}
		this.targetType = JavaType.of(targetType);
	}

	@Override
	public JavaType<T> getTargetType() {
		return this.targetType;
	}

	@Override
	protected T doConvert(Object value, JavaType<T> type) {
		if (value instanceof Enum) {
			return doConvert(((Enum<?>) value).ordinal(), type);
		}
		Class<T> targetType = type.getRawClass();
		if (Byte.class == targetType) {
			return (T) toByte(value);
		} else if (Short.class == targetType) {
			return (T) toShort(value);
		} else if (Integer.class == targetType) {
			return (T) toInteger(value);
		} else if (AtomicInteger.class == targetType) {
			return (T) new AtomicInteger(toInteger(value).intValue());
		} else if (Long.class == targetType) {
			return (T) toLong(value);
		} else if (AtomicLong.class == targetType) {
			return (T) new AtomicLong(toLong(value).longValue());
		} else if (LongAdder.class == targetType) {
			LongAdder longValue = new LongAdder();
			longValue.add(toLong(value).longValue());
			return (T) longValue;
		} else if (Float.class == targetType) {
			return (T) toFloat(value);
		} else if (Double.class == targetType) {
			return (T) toDouble(value);
		} else if (DoubleAdder.class == targetType) {
			DoubleAdder doubleAdder = new DoubleAdder();
			doubleAdder.add(toDouble(value).doubleValue());
			return (T) doubleAdder;
		} else if (BigDecimal.class == targetType) {
			return (T) toBigDecimal(value);
		} else if (BigInteger.class == targetType) {
			return (T) toBigInteger(value);
		} else if (Number.class == targetType) {
			if (value instanceof Number) {
				return (T) value;
			} else if (value instanceof Boolean) {
				return (T) (((Boolean) value).booleanValue() ? Integer.valueOf(1) : Integer.valueOf(0));
			}
			return (T) parseNumber(asString(value));
		}
		return null;
	}

	public Byte toByte(Object value) {
		if (value instanceof Number) {
			return (((Number) value).byteValue());
		} else if (value instanceof Boolean) {
			return (((Boolean) value).booleanValue() ? Byte.valueOf((byte) 1) : Byte.valueOf((byte) 0));
		}
		String str = asString(value);
		try {
			return Byte.valueOf(str);
		} catch (NumberFormatException e) {
			return parseNumber(str).byteValue();
		}
	}

	@Nonnull
	public Short toShort(Object value) {
		if (value instanceof Number) {
			return ((Number) value).shortValue();
		} else if (value instanceof Boolean) {
			return (((Boolean) value).booleanValue() ? Short.valueOf((short) 1) : Short.valueOf((short) 0));
		}
		String str = asString(value);
		try {
			return Short.valueOf(str);
		} catch (NumberFormatException e) {
			return parseNumber(str).shortValue();
		}
	}

	@Nonnull
	public Integer toInteger(Object value) {
		if (value instanceof Number) {
			return ((Number) value).intValue();
		} else if (value instanceof Boolean) {
			return (((Boolean) value).booleanValue() ? Integer.valueOf(1) : Integer.valueOf(0));
		} else if (value instanceof Date) {
			return (int) ((Date) value).getTime();
		} else if (value instanceof Calendar) {
			return (int) ((Calendar) value).getTimeInMillis();
		} else if (value instanceof TemporalAccessor) {
			return (int) Dates.toMills(Dates.toLocalDateTime((TemporalAccessor) value));
		}
		String str = asString(value);
		try {
			return Integer.valueOf(str);
		} catch (NumberFormatException e) {
			return parseNumber(str).intValue();
		}
	}

	public Long toLong(Object value){
		if (value instanceof Number) {
			return ((Number) value).longValue();
		} else if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue() ? Long.valueOf(1) : Long.valueOf(0);
		} else if (value instanceof Date) {
			return ((Date) value).getTime();
		} else if (value instanceof Calendar) {
			return ((Calendar) value).getTimeInMillis();
		} else if (value instanceof TemporalAccessor) {
			return Dates.toMills(Dates.toLocalDateTime((TemporalAccessor) value));
		}
		String str = asString(value);
		try {
			return Long.valueOf(str);
		} catch (NumberFormatException e) {
			return parseNumber(str).longValue();
		}
	}

	public Float toFloat(Object value){
		if (value instanceof Number) {
			return ((Number) value).floatValue();
		} else if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue() ? Float.valueOf(1) : Float.valueOf(0);
		}
		String str = asString(value);
		try {
			return Float.valueOf(str);
		} catch (NumberFormatException e) {
			return parseNumber(str).floatValue();
		}
	}
	public Double toDouble(Object value){
		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		} else if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue() ? Double.valueOf(1) : Double.valueOf(0);
		}
		String str = asString(value);
		try {
			return Double.valueOf(str);
		} catch (NumberFormatException e) {
			return parseNumber(str).doubleValue();
		}
	}

	public Number parseNumber(String str) {
		str = Strings.trimToNull(str);
		if (str == null) {
			return Long.valueOf(0);
		}
		try {
			return Long.decode(str);
		} catch (Exception ignore) {
		}
//		try {
//			if (str.startsWith("0x") || str.startsWith("0x")) {
//				return (Long.parseLong(str.substring(2), 16));
//			}
//		} catch (Exception ignore) {
//		}
//		try {
//			if (!str.contains(".") && str.startsWith("0")) {
//				return (Long.parseLong(str.substring(1), 8));
//			}
//		} catch (Exception ignore) {
//		}
		try {
			NumberFormat format = NumberFormat.getInstance();
			if (format instanceof DecimalFormat) {
				// 超出double的精度时，会导致截断，此处使用BigDecimal接收
				((DecimalFormat) format).setParseBigDecimal(true);
			}
			return (format.parse(str));
		} catch (Exception ignore) {
		}
		throw new NumberFormatException(str);
	}

	public BigDecimal toBigDecimal(Object value) {
		if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		} else if (value instanceof Number) {
			return toBigDecimal((Number) value);
		} else if (value instanceof Boolean) {
			return ((boolean) value) ? BigDecimal.ONE : BigDecimal.ZERO;
		}
		String str = asString(value);
		Number number = parseNumber(str);
		if (number != null) {
			return toBigDecimal(number);
		}
		return new BigDecimal(str);
	}

	public BigDecimal toBigDecimal(Number number) {
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
		return new BigDecimal(number.toString());
	}

	public BigInteger toBigInteger(Object value) {
		if (value instanceof BigInteger) {
			return (BigInteger) value;
		} else if (value instanceof Long) {
			return BigInteger.valueOf((Long) value);
		} else if (value instanceof Boolean) {
			return (boolean) value ? BigInteger.ONE : BigInteger.ZERO;
		} else if (value instanceof BigDecimal) {
			return ((BigDecimal) value).toBigInteger();
		} else if (value instanceof Number) {
			return BigInteger.valueOf(((Number) value).longValue());
		}
		String str = Strings.trimToNull(asString(value));
		if (str == null) {
			return BigInteger.ZERO;
		}
		Number number = parseNumber(str);
		if (number != null) {
			if (number instanceof BigDecimal) {
				return ((BigDecimal) number).toBigInteger();
			}
			return BigInteger.valueOf(number.longValue());
		}
		return new BigInteger(str);
	}
}
