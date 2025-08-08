package io.polaris.core.lang.bean;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.converter.Converters;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.lang.Types;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.Loggers;
import io.polaris.core.reflect.Reflects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qt
 * @since Dec 28, 2023
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class MetaObject<T> {
	public static final int INIT = 0;
	public static final int PARSING = 1;
	public static final int READY = 2;
	private static final ILogger log = Loggers.of(MetaObject.class);
	private static final Set<Class> basicTypes;
	private final JavaType<T> beanType;
	private int state = INIT;

	private boolean isObject;
	private boolean isBasic;
	private boolean isPrimitive;
	private boolean isPrimitiveWrapper;
	private boolean isEnum;
	private boolean isArray;
	private boolean isMap;
	private boolean isCollection;
	private boolean isBean;

	private MetaObject<?> keyType;
	private MetaObject<?> elementType;

	static {
		Set<Class> set = new HashSet<>();
		set.add(Class.class);
		set.add(String.class);
		set.add(CharSequence.class);
		set.add(StringBuilder.class);
		set.add(StringBuffer.class);
		set.add(AtomicInteger.class);
		set.add(AtomicLong.class);
		set.add(AtomicBoolean.class);
		set.add(LongAdder.class);
		set.add(DoubleAdder.class);
		set.add(BigDecimal.class);
		set.add(BigInteger.class);

		/* set.add(Date.class);
		set.add(java.sql.Date.class);
		set.add(java.sql.Time.class);
		set.add(java.sql.Timestamp.class);
		set.add(Instant.class);
		set.add(LocalDateTime.class);
		set.add(LocalDate.class);
		set.add(LocalTime.class);
		set.add(ZonedDateTime.class);
		set.add(OffsetDateTime.class);
		set.add(OffsetTime.class);
		set.add(DayOfWeek.class);
		set.add(Month.class);
		set.add(MonthDay.class);
		set.add(Year.class);
		set.add(YearMonth.class);
		set.add(Period.class);
		set.add(ChronoPeriod.class);
		set.add(Duration.class); */
		basicTypes = Collections.unmodifiableSet(set);
	}

	protected MetaObject(JavaType<T> beanType) {
		this.beanType = beanType;
	}


	// region inherit or inner

	protected final int state() {
		return state;
	}

	protected final void parse() {
		state = PARSING;
		try {
			Class<T> rawClass = beanType.getRawClass();
			if (Object.class.equals(rawClass)) {
				this.isObject = true;
			} else if (rawClass.isPrimitive()) {
				this.isPrimitive = true;
				this.isBasic = true;
			} else if (Types.isPrimitiveWrapper(rawClass)) {
				this.isPrimitiveWrapper = true;
				this.isBasic = true;
			} else if (basicTypes.contains(rawClass)) {
				this.isBasic = true;
			} else if (rawClass.isEnum()) {
				this.isEnum = true;
			} else if (rawClass.isArray()) {
				this.isArray = true;
				this.elementType = createMetaObject(JavaType.of(rawClass.getComponentType()));
			} else if (Map.class.isAssignableFrom(rawClass)) {
				this.isMap = true;
				this.keyType = createMetaObject(JavaType.of(beanType.getActualType(Map.class, 0)));
				this.elementType = createMetaObject(JavaType.of(beanType.getActualType(Map.class, 1)));
			} else if (Collection.class.isAssignableFrom(rawClass)) {
				this.isCollection = true;
				this.elementType = createMetaObject(JavaType.of(beanType.getActualType(Collection.class, 0)));
			} else {
				if (initBeanAccessor(rawClass)) {
					this.isBean = true;
				} else {
					this.isBasic = true;
				}
			}
		} finally {
			state = READY;
		}
	}

	/**
	 * 初始化Bean访问器并返回是否成功，失败表示非常规Bean，视为基础类型
	 */
	protected boolean initBeanAccessor(Class<T> rawClass) {
		return false;
	}

	protected abstract <E> MetaObject<E> createMetaObject(JavaType<E> rawClass);

	protected abstract Object getBeanPropertyOrSetDefault(@Nonnull T o, CaseModeOption caseMode, @Nonnull String property);

	protected abstract Object setBeanProperty(@Nonnull T o, CaseModeOption caseMode, @Nonnull String property, Object val);

	protected abstract MetaObject<?> getBeanProperty(CaseModeOption caseMode, @Nonnull String property);

	protected abstract Object getBeanProperty(@Nonnull T o, CaseModeOption caseMode, @Nonnull String property);

	protected abstract boolean hasBeanProperty(@Nonnull T o, CaseModeOption caseMode, @Nonnull String property);

	// endregion


	// region factory


	public static <T> MetaObject<T> of(BeanAccessMode mode, JavaType<T> beanType) {
		if (mode == null) {
			mode = BeanAccessMode.INDEXED;
		}
		switch (mode) {
			case LAMBDA:
				return LambdaMetaObject.of(beanType);
			case INDEXED:
			default:
				return IndexedMetaObject.of(beanType);
		}
	}

	public static <T> MetaObject<T> of(JavaType<T> beanType) {
		return of(BeanAccessMode.INDEXED, beanType);
	}

	public static <T> MetaObject<T> of(Class<T> beanType) {
		return of(BeanAccessMode.INDEXED, JavaType.of(beanType));
	}

	public static <T> MetaObject<T> of(TypeRef<T> beanType) {
		return of(BeanAccessMode.INDEXED, JavaType.of(beanType));
	}

	public static <T> MetaObject<T> of(Type beanType) {
		return of(BeanAccessMode.INDEXED, JavaType.of(beanType));
	}

	// endregion

	@Nullable
	public T newInstance() {
		return Reflects.newInstanceIfPossible(this.beanType.getRawClass());
	}

	public Object getPropertyOrSetDefault(@Nonnull T o, @Nonnull String property) {
		return getPropertyOrSetDefault(o, CaseModeOption.empty(), property);
	}

	public Object getPropertyOrSetDefault(@Nonnull T o, CaseModeOption caseMode, @Nonnull String property) {
		if (isObject || isBasic || isEnum) {
			MetaObject runtimeMeta = createMetaObject(JavaType.of(o.getClass()));
			if (this.equals(runtimeMeta) || runtimeMeta.isObject || runtimeMeta.isBasic || runtimeMeta.isEnum) {
				log.debug("Unsupported property：{}:{}", beanType.getTypeName(), property);
				return null;
			} else {
				return runtimeMeta.getPropertyOrSetDefault(o, caseMode, property);
			}
		}
		if (isArray) {
			int idx = 0;
			try {
				idx = Integer.parseInt(property);
			} catch (NumberFormatException e) {
				log.debug("Array index number format error：{}:{}", beanType.getTypeName(), property);
				return null;
			}
			int length = Array.getLength(o);
			if (idx >= length) {
				log.debug("Array index out of bounds：{}:{}", beanType.getTypeName(), property);
				return null;
			}
			Object val = Array.get(o, idx);
			if (val == null) {
				val = this.elementType.newInstance();
				if (val != null) {
					Array.set(o, idx, val);
				}
			}
			return val;
		}
		if (isMap) {
			Object k = Converters.convertQuietly(keyType.beanType, property);
			Object val = ((Map) o).get(k);
			if (val == null) {
				val = this.elementType.newInstance();
				((Map) o).put(k, val);
			}
			return val;
		}
		if (isCollection) {
			int idx = 0;
			try {
				idx = Integer.parseInt(property);
			} catch (NumberFormatException e) {
				log.debug("Collection index number format error：{}:{}", beanType.getTypeName(), property);
				return null;
			}
			int size = ((Collection) o).size();
			if (idx >= size) {
				for (int i = size; i < idx; i++) {
					// 补位
					((Collection) o).add(null);
				}
				Object val = this.elementType.newInstance();
				((Collection) o).add(val);
				return val;
			}
			if (o instanceof List) {
				Object val = ((List) o).get(idx);
				if (val == null) {
					val = this.elementType.newInstance();
					((List) o).set(idx, val);
				}
				return val;
			}
			List list = new ArrayList<>(((Collection) o));
			Object val = list.get(idx);
			if (val == null) {
				val = this.elementType.newInstance();
				list.set(idx, val);
				((Collection) o).clear();
				((Collection) o).addAll(list);
			}
			return val;
		}

		return getBeanPropertyOrSetDefault(o, caseMode, property);
	}

	public Object setProperty(@Nonnull T o, @Nonnull String property, Object val) {
		return setProperty(o, CaseModeOption.empty(), property, val);
	}

	public Object setProperty(@Nonnull T o, CaseModeOption caseMode, @Nonnull String property, Object val) {
		if (isObject || isBasic || isEnum) {
			MetaObject runtimeMeta = createMetaObject(JavaType.of(o.getClass()));
			if (this.equals(runtimeMeta) || runtimeMeta.isObject || runtimeMeta.isBasic || runtimeMeta.isEnum) {
				log.debug("Unsupported property：{}:{}", beanType.getTypeName(), property);
				return null;
			} else {
				return runtimeMeta.setProperty(o, caseMode, property, val);
			}
		}
		if (isArray) {
			int idx = 0;
			try {
				idx = Integer.parseInt(property);
			} catch (NumberFormatException e) {
				log.debug("Array index number format error：{}:{}", beanType.getTypeName(), property);
				return null;
			}
			int length = Array.getLength(o);
			if (idx >= length) {
				log.debug("Array index out of bounds：{}:{}", beanType.getTypeName(), property);
				return null;
			}
			Array.set(o, idx, val = Converters.convertQuietly(elementType.beanType, val));
			return val;
		}
		if (isMap) {
			Object k = Converters.convertQuietly(keyType.beanType, property);
			((Map) o).put(k, val = Converters.convertQuietly(elementType.beanType, val));
			return val;
		}

		if (isCollection) {
			int idx = 0;
			try {
				idx = Integer.parseInt(property);
			} catch (NumberFormatException e) {
				log.debug("Collection index out of bounds：{}:{}", beanType.getTypeName(), property);
				return null;
			}
			int size = ((Collection) o).size();
			if (idx >= size) {
				for (int i = size; i < idx; i++) {
					// 补空位
					((Collection) o).add(Types.getDefaultValue(elementType.beanType.getRawClass()));
				}
				((Collection) o).add(val = Converters.convertQuietly(elementType.beanType, val));
				return val;
			}
			if (o instanceof List) {
				((List) o).set(idx, val = Converters.convertQuietly(elementType.beanType, val));
				return val;
			}
			List list = new ArrayList<>(((Collection) o));
			list.set(idx, val = Converters.convertQuietly(elementType.beanType, val));
			((Collection) o).clear();
			((Collection) o).addAll(list);
			return val;
		}

		return setBeanProperty(o, caseMode, property, val);
	}

	public MetaObject<?> getProperty(@Nonnull String property) {
		return getProperty(CaseModeOption.empty(), property);
	}

	public MetaObject<?> getProperty(CaseModeOption caseMode, @Nonnull String property) {
		if (isObject || isBasic || isEnum) {
			log.debug("Unsupported property：{}:{}", beanType.getTypeName(), property);
			return null;
		}
		if (isArray || isCollection || isMap) {
			return this.elementType;
		}
		return getBeanProperty(caseMode, property);
	}

	public MetaObject<?> getPathProperty(@Nonnull String property) {
		return getPathProperty(CaseModeOption.empty(), property);
	}


	public MetaObject<?> getPathProperty(CaseModeOption caseMode, @Nonnull String property) {
		Deque<String> properties = Beans.parseProperty(property);
		MetaObject meta = this;
		for (String key : properties) {
			meta = meta.getProperty(caseMode, key);
			if (meta == null) {
				break;
			}
		}
		return meta;
	}

	public Object getProperty(@Nonnull T o, @Nonnull String property) {
		return getProperty(o, CaseModeOption.empty(), property);
	}

	public Object getProperty(@Nonnull T o, CaseModeOption caseMode, @Nonnull String property) {
		if (isObject || isBasic || isEnum) {
			MetaObject runtimeMeta = createMetaObject(JavaType.of(o.getClass()));
			if (this.equals(runtimeMeta) || runtimeMeta.isObject || runtimeMeta.isBasic || runtimeMeta.isEnum) {
				log.debug("Unsupported property：{}:{}", beanType.getTypeName(), property);
				return null;
			} else {
				return runtimeMeta.getProperty(o, caseMode, property);
			}
		}
		if (isArray) {
			int idx = 0;
			try {
				idx = Integer.parseInt(property);
			} catch (NumberFormatException e) {
				log.debug("Array index number format error：{}:{}", beanType.getTypeName(), property);
				return null;
			}
			int length = Array.getLength(o);
			if (idx >= length) {
				log.debug("Array index out of bounds：{}:{}", beanType.getTypeName(), property);
				return null;
			}
			return Array.get(o, idx);
		}
		if (isMap) {
			Object k = Converters.convertQuietly(keyType.beanType, property);
			return ((Map) o).get(k);
		}
		if (isCollection) {
			int idx = 0;
			try {
				idx = Integer.parseInt(property);
			} catch (NumberFormatException e) {
				log.debug("Collection index number format error：{}:{}", beanType.getTypeName(), property);
				return null;
			}
			int size = ((Collection) o).size();
			if (idx >= size) {
				log.debug("Collection index out of bounds：{}:{}", beanType.getTypeName(), property);
				return null;
			}
			if (o instanceof List) {
				return ((List) o).get(idx);
			}
			Iterator iter = ((Collection) o).iterator();
			for (int i = 0; i < idx; i++) {
				iter.next();
			}
			return iter.next();
		}
		return getBeanProperty(o, caseMode, property);
	}

	public boolean hasProperty(@Nonnull T o, @Nonnull String property) {
		return hasProperty(o, CaseModeOption.empty(), property);
	}

	public boolean hasProperty(@Nonnull T o, CaseModeOption caseMode, @Nonnull String property) {
		if (isObject || isBasic || isEnum) {
			MetaObject runtimeMeta = createMetaObject(JavaType.of(o.getClass()));
			if (this.equals(runtimeMeta) || runtimeMeta.isObject || runtimeMeta.isBasic || runtimeMeta.isEnum) {
				return false;
			} else {
				return runtimeMeta.hasProperty(o, caseMode, property);
			}
		}
		if (isArray) {
			int idx = 0;
			try {
				idx = Integer.parseInt(property);
			} catch (NumberFormatException e) {
				log.debug("Array index number format error：{}:{}", beanType.getTypeName(), property);
				return false;
			}
			int length = Array.getLength(o);
			if (idx >= length) {
				return false;
			}
			return Array.get(o, idx) != null;
		}
		if (isMap) {
			Object k = Converters.convertQuietly(keyType.beanType, property);
			return ((Map) o).get(k) != null;
		}
		if (isCollection) {
			int idx = 0;
			try {
				idx = Integer.parseInt(property);
			} catch (NumberFormatException e) {
				log.debug("Collection index out of bounds：{}:{}", beanType.getTypeName(), property);
				return false;
			}
			int size = ((Collection) o).size();
			if (idx >= size) {
				return false;
			}
			if (o instanceof List) {
				return ((List) o).get(idx) != null;
			}
			Iterator iter = ((Collection) o).iterator();
			for (int i = 0; i < idx; i++) {
				iter.next();
			}
			return iter.next() != null;
		}

		return hasBeanProperty(o, caseMode, property);
	}


	public Object getPathProperty(@Nonnull T o, @Nonnull String property) {
		return getPathProperty(o, CaseModeOption.empty(), Beans.parseProperty(property));
	}

	public Object getPathProperty(@Nonnull T o, CaseModeOption caseMode, @Nonnull String property) {
		return getPathProperty(o, caseMode, Beans.parseProperty(property));
	}

	public Object setPathProperty(@Nonnull T o, @Nonnull String property, Object val) {
		return setPathProperty(o, CaseModeOption.empty(), Beans.parseProperty(property), val);
	}

	public Object setPathProperty(@Nonnull T o, CaseModeOption caseMode, @Nonnull String property, Object val) {
		return setPathProperty(o, caseMode, Beans.parseProperty(property), val);
	}

	private Object setPathProperty(@Nonnull T o, CaseModeOption caseMode, Deque<String> properties, Object val) {
		if (val == null) {
			return null;
		}
		String property = properties.pollLast();
		if (properties.isEmpty()) {
			return setProperty(o, caseMode, property, val);
		}
		PropertyInfo info = getRequiredPathProperty(o, caseMode, properties);
		if (info == null) {
			return null;
		}
		if (info.propertyMeta.isArray) {
			return setArrayElement(info, property, info.propertyMeta.elementType, val);
		} else {
			return info.propertyMeta.setProperty(info.propertyObj, caseMode, property, val);
		}
	}

	private Object getPathProperty(T obj, CaseModeOption caseMode, Deque<String> properties) {
		Object target = obj;
		MetaObject meta = this;
		for (String property : properties) {
			target = meta.getProperty(target, caseMode, property);
			if (target == null) {
				break;
			}
			meta = meta.getProperty(caseMode, property);
		}
		return target;
	}

	private PropertyInfo getRequiredPathProperty(T obj, CaseModeOption caseMode, Deque<String> properties) {
		PropertyInfo info = new PropertyInfo("", obj, this, null, null);
		for (String property : properties) {
			Object propVal = info.propertyMeta.getPropertyOrSetDefault(info.propertyObj, caseMode, property);
			MetaObject propMeta = info.propertyMeta.getProperty(caseMode, property);
			if (propVal == null) {
				if (propMeta != null) {
					// 尝试设置数组元素
					propVal = setArrayElement(info, property, propMeta, null);
				}
				if (propVal == null) {
					return null;
				}
			}
			info.propertyName = property;
			info.parentObj = info.propertyObj;
			info.parentMeta = info.propertyMeta;
			info.propertyObj = propVal;
			// 使用运行期属性类型，因为数组、集合、泛型类等可能在运行时类型不一致
			info.propertyMeta = createMetaObject(JavaType.of(propVal.getClass()));
			//info.propertyMeta = propMeta;
		}
		return info;
	}


	private Object setArrayElement(PropertyInfo info, String indexProperty, MetaObject propMeta, Object propVal) {
		if (info.parentMeta != null && info.propertyMeta.isArray) {
			int idx = Integer.parseInt(indexProperty);
			Object array = extendArrayLength(info, idx + 1);
			if (propVal == null) {
				propVal = propMeta.newInstance();
			} else {
				propVal = Converters.convert(info.propertyMeta.elementType.beanType, propVal);
			}
			Array.set(array, idx, propVal);
		}
		return propVal;
	}


	private Object extendArrayLength(PropertyInfo info, int minLength) {
		if (info.propertyMeta.isArray) {
			int length = Array.getLength(info.propertyObj);
			if (length < minLength) {
				Object newArray = Array.newInstance(info.propertyMeta.elementType.beanType.getRawClass(), minLength);
				info.parentMeta.setProperty(info.parentObj, info.propertyName, newArray);
				System.arraycopy(info.propertyObj, 0, newArray, 0, length);
				return newArray;
			}
		}
		return info.propertyObj;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MetaObject<?> that = (MetaObject<?>) o;
		return Objects.equals(beanType, that.beanType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(beanType);
	}

	public JavaType<T> getBeanType() {
		return beanType;
	}

	public boolean isObject() {
		return isObject;
	}

	public boolean isBasic() {
		return isBasic;
	}

	public boolean isPrimitive() {
		return isPrimitive;
	}

	public boolean isPrimitiveWrapper() {
		return isPrimitiveWrapper;
	}

	public boolean isEnum() {
		return isEnum;
	}

	public boolean isArray() {
		return isArray;
	}

	public boolean isMap() {
		return isMap;
	}

	public boolean isCollection() {
		return isCollection;
	}

	public boolean isBean() {
		return isBean;
	}

	public MetaObject<?> getKeyType() {
		return keyType;
	}

	public MetaObject<?> getElementType() {
		return elementType;
	}

	@AllArgsConstructor
	@Getter
	@Setter
	private static class PropertyInfo {
		private String propertyName;
		private Object propertyObj;
		private MetaObject propertyMeta;
		private Object parentObj;
		private MetaObject parentMeta;
	}

}
