package io.polaris.core.converter;

import io.polaris.core.converter.support.*;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.log.ILogger;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.service.Service;
import io.polaris.core.service.ServiceLoader;
import io.polaris.core.ulid.Ulid;

import javax.annotation.Nullable;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.*;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.*;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings({"all"})
public class ConverterRegistry {
	private static final ILogger log = ILogger.of(ConverterRegistry.class);
	private static final Map<Type, Converter<?>> standardConverters = new ConcurrentHashMap<>();
	private static final Map<Type, Converter<?>> serviceConverters = new ConcurrentHashMap<>();
	private final Map<Type, Converter<?>> customConverters = new ConcurrentHashMap<>();

	static {
		initStandard();
		loadServices();
	}

	public ConverterRegistry() {
	}

	private static void initStandard() {
		// 原始类型转换器
		standardConverters.put(int.class, new PrimitiveConverter(int.class));
		standardConverters.put(long.class, new PrimitiveConverter(long.class));
		standardConverters.put(byte.class, new PrimitiveConverter(byte.class));
		standardConverters.put(short.class, new PrimitiveConverter(short.class));
		standardConverters.put(float.class, new PrimitiveConverter(float.class));
		standardConverters.put(double.class, new PrimitiveConverter(double.class));
		standardConverters.put(char.class, new PrimitiveConverter(char.class));
		standardConverters.put(boolean.class, new PrimitiveConverter(boolean.class));

		// 包装类转换器
		standardConverters.put(Number.class, new NumberConverter(Number.class));
		standardConverters.put(Integer.class, new NumberConverter(Integer.class));
		standardConverters.put(AtomicInteger.class, new NumberConverter(AtomicInteger.class));
		standardConverters.put(Long.class, new NumberConverter(Long.class));
		standardConverters.put(LongAdder.class, new NumberConverter(LongAdder.class));
		standardConverters.put(AtomicLong.class, new NumberConverter(AtomicLong.class));
		standardConverters.put(Byte.class, new NumberConverter(Byte.class));
		standardConverters.put(Short.class, new NumberConverter(Short.class));
		standardConverters.put(Float.class, new NumberConverter(Float.class));
		standardConverters.put(Double.class, new NumberConverter(Double.class));
		standardConverters.put(DoubleAdder.class, new NumberConverter(DoubleAdder.class));
		standardConverters.put(BigDecimal.class, new NumberConverter(BigDecimal.class));
		standardConverters.put(BigInteger.class, new NumberConverter(BigInteger.class));

		standardConverters.put(Character.class, new CharacterConverter());
		standardConverters.put(Boolean.class, new BooleanConverter());
		standardConverters.put(AtomicBoolean.class, new AtomicBooleanConverter());
		standardConverters.put(CharSequence.class, new StringConverter());
		standardConverters.put(String.class, new StringConverter());

		// URI and URL
		standardConverters.put(URI.class, new URIConverter());
		standardConverters.put(URL.class, new URLConverter());

		// 日期时间
		standardConverters.put(Calendar.class, new CalendarConverter());
		standardConverters.put(java.util.Date.class, new DateConverter(java.util.Date.class));
		standardConverters.put(java.sql.Date.class, new DateConverter(java.sql.Date.class));
		standardConverters.put(java.sql.Time.class, new DateConverter(java.sql.Time.class));
		standardConverters.put(java.sql.Timestamp.class, new DateConverter(java.sql.Timestamp.class));

		// 日期时间 JDK8+
		standardConverters.put(TemporalAccessor.class, new TemporalAccessorConverter(Instant.class));
		standardConverters.put(Instant.class, new TemporalAccessorConverter(Instant.class));
		standardConverters.put(LocalDateTime.class, new TemporalAccessorConverter(LocalDateTime.class));
		standardConverters.put(LocalDate.class, new TemporalAccessorConverter(LocalDate.class));
		standardConverters.put(LocalTime.class, new TemporalAccessorConverter(LocalTime.class));
		standardConverters.put(ZonedDateTime.class, new TemporalAccessorConverter(ZonedDateTime.class));
		standardConverters.put(OffsetDateTime.class, new TemporalAccessorConverter(OffsetDateTime.class));
		standardConverters.put(OffsetTime.class, new TemporalAccessorConverter(OffsetTime.class));
		standardConverters.put(DayOfWeek.class, new TemporalAccessorConverter(DayOfWeek.class));
		standardConverters.put(Month.class, new TemporalAccessorConverter(Month.class));
		standardConverters.put(MonthDay.class, new TemporalAccessorConverter(MonthDay.class));
		standardConverters.put(Year.class, new TemporalAccessorConverter(MonthDay.class));
		standardConverters.put(YearMonth.class, new TemporalAccessorConverter(MonthDay.class));
		standardConverters.put(Period.class, new PeriodConverter());
		standardConverters.put(ChronoPeriod.class, new PeriodConverter());
		standardConverters.put(Duration.class, new DurationConverter());

		// Reference
		standardConverters.put(WeakReference.class, new ReferenceConverter(WeakReference.class));
		standardConverters.put(SoftReference.class, new ReferenceConverter(SoftReference.class));
		standardConverters.put(AtomicReference.class, new AtomicReferenceConverter());

		//AtomicXXXArray
		standardConverters.put(AtomicIntegerArray.class, new AtomicIntegerArrayConverter());
		standardConverters.put(AtomicLongArray.class, new AtomicLongArrayConverter());

		// 其它类型
		standardConverters.put(Class.class, new ClassConverter());
		standardConverters.put(TimeZone.class, new TimeZoneConverter());
		standardConverters.put(Locale.class, new LocaleConverter());
		standardConverters.put(Charset.class, new CharsetConverter());
		standardConverters.put(Path.class, new PathConverter());
		standardConverters.put(Currency.class, new CurrencyConverter());
		standardConverters.put(UUID.class, new UUIDConverter());
		standardConverters.put(Ulid.class, new UlidConverter());
		standardConverters.put(StackTraceElement.class, new StackTraceElementConverter());
		standardConverters.put(Optional.class, new OptionalConverter());
	}

	private static void loadServices() {
		for (Service<Converter> service : ServiceLoader.of(Converter.class)) {
			try {
				Class<? extends Converter> converterClass = service.getServiceClass();
				Converter<?> converter = service.newInstance();
				Type actualType = JavaType.of(converterClass).getActualType(Converter.class, 0);
				serviceConverters.putIfAbsent(actualType, converter);
			} catch (Exception ignore) {
			}
		}
	}


	@Nullable
	public <T> Converter<T> getConverter(Type type) {
		Converter<T> converter = (Converter<T>) customConverters.get(type);
		if (converter != null) {
			return converter;
		}
		converter = (Converter<T>) serviceConverters.get(type);
		if (converter != null) {
			return converter;
		}
		converter = (Converter<T>) standardConverters.get(type);
		return converter;
	}

	public <T> Converter<T> getConverterOrDefault(Type type, Converter<T> defaults) {
		Converter<T> converter = getConverter(type);
		return converter != null ? converter : defaults;
	}

	public void addConvert(Type type, Class<? extends Converter<?>> converterClass) {
		addConvert(type, Reflects.newInstanceIfPossible(converterClass));
	}

	public void addConvert(Type type, Converter<?> converter) {
		customConverters.put(type, converter);
	}

	public <T> T convert(Type type, Type valueType, Object value, T defaultValue) {
		// check
		if (value == null) {
			return defaultValue;
		}
		if (type == Object.class) {
			return (T) value;
		}
		/*if (Types.isUnknown(type)) {
			if (defaultValue == null) {
				// 未知类型
				return (T) value;
			} else {
				type = defaultValue.getClass();
			}
		}*/
		if (type instanceof TypeRef) {
			type = ((TypeRef<?>) type).getType();
			return convert(type, valueType, value, defaultValue);
		}
		JavaType<Object> sourceJavaType = JavaType.of(valueType);
		if (!sourceJavaType.isInstance(value)) {
			throw new IllegalArgumentException();
		}
		JavaType<T> targetJavaType = JavaType.of(type);
		valueType = sourceJavaType.getRawType();
		type = targetJavaType.getRawType();
		if (valueType == type) {
			return (T) value;
		}
		Class<T> clazz = targetJavaType.getRawClass();
		if (type instanceof Class && clazz.isInstance(value)) {
			return (T) value;
		}

		Converter<T> converter = getConverter(type);
		if (converter != null) {
			return converter.convertOrDefault(valueType, value, defaultValue);
		}

		if (clazz.isEnum()) {
			// EnumConverter
			return (T) new EnumConverter(targetJavaType.getRawClass()).convertOrDefault(valueType, value, defaultValue);
		}
		if (Collection.class.isAssignableFrom(clazz)) {
			// CollectionConverter
			CollectionConverter collectionConverter = new CollectionConverter(type);
			return (T) collectionConverter.convertOrDefault(valueType, value, (Collection<?>) defaultValue);
		}
		if (Map.class.isAssignableFrom(clazz)) {
			// MapConverter
			MapConverter mapConverter = new MapConverter(type);
			return (T) mapConverter.convertOrDefault(valueType, value, (Map<?, ?>) defaultValue);
		}
		if (clazz.isArray()) {
			// ArrayConverter
			ArrayConverter arrayConverter = new ArrayConverter(clazz);
			return (T) arrayConverter.convertOrDefault(valueType, value, defaultValue);
		}
		if (Beans.isBeanClass(clazz)) {
			// BeanConverter
			return new BeanConverter<T>(type).convertOrDefault(valueType, value, defaultValue);
		}

		if (type instanceof Class) {
			return Converters.convertByPropertyEditor((Class) type, value, defaultValue);
		}
		if (clazz.isInstance(value)) {
			return (T) value;
		}
		return clazz.cast(value);
	}


	public <T> T convert(Type type, Type valueType, Object value) {
		return convert(type, valueType, value, null);
	}

	public <T> T convert(Type type, Object value, T defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return convert(type, value.getClass(), value, defaultValue);
	}

	public <T> T convert(Type type, Object value) {
		if (value == null) {
			return null;
		}
		return convert(type, value, null);
	}

	public <T> T convertQuietly(Type type, Type valueType, Object value, T defaultValue) {
		try {
			return convert(type, valueType, value, defaultValue);
		} catch (Exception e) {
			log.warn("类型转换失败：{}", e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			return defaultValue;
		}
	}

	public <T> T convertQuietly(Type type, Type valueType, Object value) {
		return convertQuietly(type, valueType, value, null);
	}

	public <T> T convertQuietly(Type type, Object value, T defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return convertQuietly(type, value.getClass(), value, defaultValue);
	}

	public <T> T convertQuietly(Type type, Object value) {
		if (value == null) {
			return null;
		}
		return convertQuietly(type, value.getClass(), value, null);
	}
}
