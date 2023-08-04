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

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
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
public enum ConverterRegistry {

	INSTANCE;

	private static final ILogger log = ILogger.of(ConverterRegistry.class);
	private final Map<Type, Converter<?>> defaultConverters = new ConcurrentHashMap<>();
	private final Map<Type, Converter<?>> customConverters = new ConcurrentHashMap<>();

	ConverterRegistry() {
		initDefaults();
		loadCustom();
	}

	private void loadCustom() {
		for (Service<Converter> service : ServiceLoader.of(Converter.class)) {
			try {
				Class<? extends Converter> converterClass = service.getServiceClass();
				Converter converter = service.newInstance();
				Type actualType = JavaType.of(converterClass).getActualType(Converter.class, 0);
				customConverters.putIfAbsent(actualType, converter);
			} catch (Exception ignore) {
			}
		}
	}

	private void initDefaults() {
		// 原始类型转换器
		defaultConverters.put(int.class, new PrimitiveConverter(int.class));
		defaultConverters.put(long.class, new PrimitiveConverter(long.class));
		defaultConverters.put(byte.class, new PrimitiveConverter(byte.class));
		defaultConverters.put(short.class, new PrimitiveConverter(short.class));
		defaultConverters.put(float.class, new PrimitiveConverter(float.class));
		defaultConverters.put(double.class, new PrimitiveConverter(double.class));
		defaultConverters.put(char.class, new PrimitiveConverter(char.class));
		defaultConverters.put(boolean.class, new PrimitiveConverter(boolean.class));

		// 包装类转换器
		defaultConverters.put(Number.class, new NumberConverter(Number.class));
		defaultConverters.put(Integer.class, new NumberConverter(Integer.class));
		defaultConverters.put(AtomicInteger.class, new NumberConverter(AtomicInteger.class));
		defaultConverters.put(Long.class, new NumberConverter(Long.class));
		defaultConverters.put(LongAdder.class, new NumberConverter(LongAdder.class));
		defaultConverters.put(AtomicLong.class, new NumberConverter(AtomicLong.class));
		defaultConverters.put(Byte.class, new NumberConverter(Byte.class));
		defaultConverters.put(Short.class, new NumberConverter(Short.class));
		defaultConverters.put(Float.class, new NumberConverter(Float.class));
		defaultConverters.put(Double.class, new NumberConverter(Double.class));
		defaultConverters.put(DoubleAdder.class, new NumberConverter(DoubleAdder.class));
		defaultConverters.put(BigDecimal.class, new NumberConverter(BigDecimal.class));
		defaultConverters.put(BigInteger.class, new NumberConverter(BigInteger.class));

		defaultConverters.put(Character.class, new CharacterConverter());
		defaultConverters.put(Boolean.class, new BooleanConverter());
		defaultConverters.put(AtomicBoolean.class, new AtomicBooleanConverter());
		defaultConverters.put(CharSequence.class, new StringConverter());
		defaultConverters.put(String.class, new StringConverter());

		// URI and URL
		defaultConverters.put(URI.class, new URIConverter());
		defaultConverters.put(URL.class, new URLConverter());

		// 日期时间
		defaultConverters.put(Calendar.class, new CalendarConverter());
		defaultConverters.put(java.util.Date.class, new DateConverter(java.util.Date.class));
		defaultConverters.put(java.sql.Date.class, new DateConverter(java.sql.Date.class));
		defaultConverters.put(java.sql.Time.class, new DateConverter(java.sql.Time.class));
		defaultConverters.put(java.sql.Timestamp.class, new DateConverter(java.sql.Timestamp.class));

		// 日期时间 JDK8+
		defaultConverters.put(TemporalAccessor.class, new TemporalAccessorConverter(Instant.class));
		defaultConverters.put(Instant.class, new TemporalAccessorConverter(Instant.class));
		defaultConverters.put(LocalDateTime.class, new TemporalAccessorConverter(LocalDateTime.class));
		defaultConverters.put(LocalDate.class, new TemporalAccessorConverter(LocalDate.class));
		defaultConverters.put(LocalTime.class, new TemporalAccessorConverter(LocalTime.class));
		defaultConverters.put(ZonedDateTime.class, new TemporalAccessorConverter(ZonedDateTime.class));
		defaultConverters.put(OffsetDateTime.class, new TemporalAccessorConverter(OffsetDateTime.class));
		defaultConverters.put(OffsetTime.class, new TemporalAccessorConverter(OffsetTime.class));
		defaultConverters.put(DayOfWeek.class, new TemporalAccessorConverter(DayOfWeek.class));
		defaultConverters.put(Month.class, new TemporalAccessorConverter(Month.class));
		defaultConverters.put(MonthDay.class, new TemporalAccessorConverter(MonthDay.class));
		defaultConverters.put(Year.class, new TemporalAccessorConverter(MonthDay.class));
		defaultConverters.put(YearMonth.class, new TemporalAccessorConverter(MonthDay.class));
		defaultConverters.put(Period.class, new PeriodConverter());
		defaultConverters.put(ChronoPeriod.class, new PeriodConverter());
		defaultConverters.put(Duration.class, new DurationConverter());

		// Reference
		defaultConverters.put(WeakReference.class, new ReferenceConverter(WeakReference.class));
		defaultConverters.put(SoftReference.class, new ReferenceConverter(SoftReference.class));
		defaultConverters.put(AtomicReference.class, new AtomicReferenceConverter());

		//AtomicXXXArray
		defaultConverters.put(AtomicIntegerArray.class, new AtomicIntegerArrayConverter());
		defaultConverters.put(AtomicLongArray.class, new AtomicLongArrayConverter());

		// 其它类型
		defaultConverters.put(Class.class, new ClassConverter());
		defaultConverters.put(TimeZone.class, new TimeZoneConverter());
		defaultConverters.put(Locale.class, new LocaleConverter());
		defaultConverters.put(Charset.class, new CharsetConverter());
		defaultConverters.put(Path.class, new PathConverter());
		defaultConverters.put(Currency.class, new CurrencyConverter());
		defaultConverters.put(UUID.class, new UUIDConverter());
		defaultConverters.put(Ulid.class, new UlidConverter());
		defaultConverters.put(StackTraceElement.class, new StackTraceElementConverter());
		defaultConverters.put(Optional.class, new OptionalConverter());
	}

	public void addConvert(Type type, Class<? extends Converter<?>> converterClass) {
		addConvert(type, Reflects.newInstanceIfPossible(converterClass));
	}

	public void addConvert(Type type, Converter<?> converter) {
		customConverters.put(type, converter);
	}

	public <T> Converter<T> getDefaultConverter(Type type) {
		return (Converter<T>) defaultConverters.get(type);
	}

	public <T> Converter<T> getConverter(Type type) {
		return (Converter<T>) customConverters.getOrDefault(type, defaultConverters.get(type));
	}

	public <T> Converter<T> getConverterOrDefault(Type type, Converter<T> defaults) {
		return (Converter<T>) customConverters.getOrDefault(type, defaultConverters.getOrDefault(type, defaults));
	}

	public <T> T convert(Type type, Object value) {
		if (value == null) {
			return null;
		}
		return convert(type, value, null);
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
			return convertByPropertyEditor((Class) type, value, defaultValue);
		}
		if (clazz.isInstance(value)) {
			return (T) value;
		}
		return clazz.cast(value);
	}

	public <T> T convertQuietly(Type type, Object value) {
		if (value == null) {
			return null;
		}
		return convertQuietly(type, value.getClass(), value, null);
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

	public <T> T convertByPropertyEditor(Class type, Object value, T defaultValue) {
		PropertyEditor sourceEditor = PropertyEditorManager.findEditor(value.getClass());
		PropertyEditor targetEditor = PropertyEditorManager.findEditor(type);
		if (sourceEditor != null && targetEditor != null) {
			sourceEditor.setValue(value);
			targetEditor.setAsText(sourceEditor.getAsText());
			return (T) targetEditor.getValue();
		}
		return defaultValue;
	}

}
