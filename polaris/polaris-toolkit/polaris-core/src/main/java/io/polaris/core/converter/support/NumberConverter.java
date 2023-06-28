package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;
import io.polaris.core.date.Dates;
import io.polaris.core.string.Strings;

import javax.annotation.Nonnull;
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
public class NumberConverter extends AbstractConverter<Number> {
	protected final Class<? extends Number> targetType;

	public NumberConverter() {
		this.targetType = Number.class;
	}

	public NumberConverter(@Nonnull Class<? extends Number> targetType) {
		if (targetType == null) {
			throw new NullPointerException();
		}
		this.targetType = targetType;
	}

	public Class<Number> getTargetType() {
		return (Class<Number>) this.targetType;
	}


	@Override
	protected Number convertInternal(Object value, Class<? extends Number> targetType) {
		if (value instanceof Enum) {
			return convertInternal(((Enum<?>) value).ordinal(), targetType);
		}
		if (Byte.class == targetType) {
			if (value instanceof Number) {
				return ((Number) value).byteValue();
			} else if (value instanceof Boolean) {
				return ((Boolean) value).booleanValue() ? Byte.valueOf((byte) 1) : Byte.valueOf((byte) 0);
			}
			String str = convertToStr(value);
			try {
				return Byte.valueOf(str);
			} catch (NumberFormatException e) {
				return parseNumber(str).byteValue();
			}
		} else if (Short.class == targetType) {
			if (value instanceof Number) {
				return ((Number) value).shortValue();
			} else if (value instanceof Boolean) {
				return ((Boolean) value).booleanValue() ? Short.valueOf((short) 1) : Short.valueOf((short) 0);
			}
			String str = convertToStr(value);
			try {
				return Short.valueOf(str);
			} catch (NumberFormatException e) {
				return parseNumber(str).shortValue();
			}
		} else if (Integer.class == targetType) {
			if (value instanceof Number) {
				return ((Number) value).intValue();
			} else if (value instanceof Boolean) {
				return ((Boolean) value).booleanValue() ? Integer.valueOf(1) : Integer.valueOf(0);
			} else if (value instanceof Date) {
				return (int) ((Date) value).getTime();
			} else if (value instanceof Calendar) {
				return (int) ((Calendar) value).getTimeInMillis();
			} else if (value instanceof TemporalAccessor) {
				return (int) Dates.toMills(Dates.toLocalDateTime((TemporalAccessor) value));
			}
			String str = convertToStr(value);
			try {
				return Integer.valueOf(str);
			} catch (NumberFormatException e) {
				return parseNumber(str).intValue();
			}
		} else if (AtomicInteger.class == targetType) {
			return new AtomicInteger(this.convertInternal(value, Integer.class).intValue());
		} else if (Long.class == targetType) {
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
			String str = convertToStr(value);
			try {
				return Long.valueOf(str);
			} catch (NumberFormatException e) {
				return parseNumber(str).longValue();
			}
		} else if (AtomicLong.class == targetType) {
			return new AtomicLong(this.convertInternal(value, Long.class).longValue());
		} else if (LongAdder.class == targetType) {
			LongAdder longValue = new LongAdder();
			longValue.add(this.convertInternal(value, Long.class).longValue());
			return longValue;
		} else if (Float.class == targetType) {
			if (value instanceof Number) {
				return ((Number) value).floatValue();
			} else if (value instanceof Boolean) {
				return ((Boolean) value).booleanValue() ? Float.valueOf(1) : Float.valueOf(0);
			}
			String str = convertToStr(value);
			try {
				return Float.valueOf(str);
			} catch (NumberFormatException e) {
				return parseNumber(str).floatValue();
			}
		} else if (Double.class == targetType) {
			if (value instanceof Number) {
				return ((Number) value).doubleValue();
			} else if (value instanceof Boolean) {
				return ((Boolean) value).booleanValue() ? Double.valueOf(1) : Double.valueOf(0);
			}
			String str = convertToStr(value);
			try {
				return Double.valueOf(str);
			} catch (NumberFormatException e) {
				return parseNumber(str).doubleValue();
			}
		} else if (DoubleAdder.class == targetType) {
			DoubleAdder doubleAdder = new DoubleAdder();
			doubleAdder.add(this.convertInternal(value, Double.class).doubleValue());
			return doubleAdder;
		} else if (BigDecimal.class == targetType) {
			return toBigDecimal(value);
		} else if (BigInteger.class == targetType) {
			return toBigInteger(value);
		} else if (Number.class == targetType) {
			if (value instanceof Number) {
				return (Number) value;
			} else if (value instanceof Boolean) {
				return ((Boolean) value).booleanValue() ? Integer.valueOf(1) : Integer.valueOf(0);
			}
			return parseNumber(convertToStr(value));
		}
		return null;
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
		String str = convertToStr(value);
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
		String str = Strings.trimToNull(convertToStr(value));
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
