package io.polaris.core.lang.bean;

import io.polaris.core.converter.Converters;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.lang.Types;
import io.polaris.core.log.ILogger;
import io.polaris.core.map.Maps;
import io.polaris.core.reflect.Reflects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8,  Dec 28, 2023
 */
public class MetaObject<T> {
	private static final ILogger log = ILogger.of(MetaObject.class);
	private static final Map<JavaType<?>, MetaObject<?>> CACHE = Maps.newWeakKeyMap(new ConcurrentHashMap<>());
	private final JavaType<T> beanType;
	private int state = 0;

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
	private Map<String, Accessor> properties;


	@SuppressWarnings("unchecked")
	public static <T> MetaObject<T> of(JavaType<T> beanType) {
		MetaObject<T> metaObject = (MetaObject<T>) CACHE.computeIfAbsent(beanType, k -> new MetaObject<>(beanType));
		if (metaObject.state == 0) {
			synchronized (metaObject) {
				if (metaObject.state == 0) {
					metaObject.parse();
				}
			}
		}
		return metaObject;
	}

	public static <T> MetaObject<T> of(Class<T> beanType) {
		return of(JavaType.of(beanType));
	}

	public static <T> MetaObject<T> of(TypeRef<T> beanType) {
		return of(JavaType.of(beanType));
	}

	public static <T> MetaObject<T> of(Type beanType) {
		return of(JavaType.of(beanType));
	}

	private MetaObject(JavaType<T> beanType) {
		this.beanType = beanType;
	}

	private void parse() {
		state = 1;
		try {
			Class<T> rawClass = beanType.getRawClass();
			if (rawClass.isPrimitive()) {
				this.isPrimitive = true;
				this.isBasic = true;
			} else if (Types.isPrimitiveWrapper(rawClass)) {
				this.isPrimitiveWrapper = true;
				this.isBasic = true;
			} else if (rawClass.isEnum()) {
				this.isEnum = true;
			} else if (rawClass.isArray()) {
				this.isArray = true;
				this.elementType = MetaObject.of(JavaType.of(rawClass.getComponentType()));
			} else if (Map.class.isAssignableFrom(rawClass)) {
				this.isMap = true;
				this.keyType = MetaObject.of(JavaType.of(beanType.getActualType(Map.class, 0)));
				this.elementType = MetaObject.of(JavaType.of(beanType.getActualType(Map.class, 1)));
			} else if (Collection.class.isAssignableFrom(rawClass)) {
				this.isCollection = true;
				this.elementType = MetaObject.of(JavaType.of(beanType.getActualType(Collection.class, 0)));
			} else {
				BeanMetadata metadata = BeanMetadatas.getMetadata(rawClass);
				Map<String, Type> types = metadata.types();
				int size = types.size();
				if (size > 0) {
					this.properties = new HashMap<>(size * 2);
					Map<String, Function<Object, Object>> getterMethods = metadata.getters();
					Map<String, BiConsumer<Object, Object>> setterMethods = metadata.setters();
					for (Map.Entry<String, Type> entry : types.entrySet()) {
						String key = entry.getKey();
						MetaObject<?> meta = MetaObject.of(JavaType.of(entry.getValue()));
						Function<Object, Object> getterMethod = getterMethods.get(key);
						BiConsumer<Object, Object> setterMethod = setterMethods.get(key);
						properties.put(key, new Accessor(meta, getterMethod, setterMethod));
					}
					this.isBean = true;
				} else {
					this.isBasic = true;
				}
			}
		} finally {
			state = 2;
		}
	}

	public T newInstance() {
		return Reflects.newInstanceIfPossible(this.beanType.getRawClass());
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public Object getPropertyOrSetDefault(@Nonnull T o, @Nonnull String property) {
		if (isBasic || isEnum) {
			log.warn("不支持的属性：{}:{}", beanType.getTypeName(), property);
			return null;
		}
		if (isArray) {
			int idx = 0;
			try {
				idx = Integer.parseInt(property);
			} catch (NumberFormatException e) {
				log.warn("数组下标属性错误：{}:{}", beanType.getTypeName(), property);
				return null;
			}
			int length = Array.getLength(o);
			if (idx >= length) {
				log.warn("数组下标属性越界：{}:{}", beanType.getTypeName(), property);
				return null;
			}
			Object val = Array.get(o, idx);
			if (val == null) {
				val = this.elementType.newInstance();
				Array.set(o, idx, val);
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
				log.warn("集合下标属性错误：{}:{}", beanType.getTypeName(), property);
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
		Accessor accessor = properties.get(property);
		if (accessor == null || accessor.getter == null || accessor.setter == null) {
			log.warn("不支持的属性：{}:{}", beanType.getTypeName(), property);
			return null;
		}
		Object val = accessor.getter.apply(o);
		if (val == null) {
			val = accessor.meta.newInstance();
			accessor.setter.accept(o, val);
		}
		return val;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public Object setProperty(@Nonnull T o, @Nonnull String property, Object val) {
		if (isBasic || isEnum) {
			log.warn("不支持的属性：{}:{}", beanType.getTypeName(), property);
			return null;
		}
		if (isArray) {
			int idx = 0;
			try {
				idx = Integer.parseInt(property);
			} catch (NumberFormatException e) {
				log.warn("数组下标属性错误：{}:{}", beanType.getTypeName(), property);
				return null;
			}
			int length = Array.getLength(o);
			if (idx >= length) {
				log.warn("数组下标属性越界：{}:{}", beanType.getTypeName(), property);
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
				log.warn("集合下标属性越界：{}:{}", beanType.getTypeName(), property);
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
		Accessor accessor = properties.get(property);
		if (accessor == null || accessor.setter == null) {
			log.warn("不支持的属性：{}:{}", beanType.getTypeName(), property);
			return null;
		}
		accessor.setter.accept(o, val = Converters.convertQuietly(accessor.meta.beanType, val));
		return val;
	}

	public MetaObject<?> getProperty(@Nonnull String property) {
		if (isBasic || isEnum) {
			log.warn("不支持的属性：{}:{}", beanType.getTypeName(), property);
			return null;
		}
		if (isArray || isCollection || isMap) {
			return this.elementType;
		}
		Accessor accessor = properties.get(property);
		if (accessor == null) {
			log.warn("不支持的属性：{}:{}", beanType.getTypeName(), property);
			return null;
		}
		return accessor.meta;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public Object getProperty(@Nonnull T o, @Nonnull String property) {
		if (isBasic || isEnum) {
			log.warn("不支持的属性：{}:{}", beanType.getTypeName(), property);
			return null;
		}
		if (isArray) {
			int idx = 0;
			try {
				idx = Integer.parseInt(property);
			} catch (NumberFormatException e) {
				log.warn("数组下标属性错误：{}:{}", beanType.getTypeName(), property);
				return null;
			}
			int length = Array.getLength(o);
			if (idx >= length) {
				log.warn("数组下标属性越界：{}:{}", beanType.getTypeName(), property);
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
				log.warn("集合下标属性错误：{}:{}", beanType.getTypeName(), property);
				return null;
			}
			int size = ((Collection) o).size();
			if (idx >= size) {
				log.warn("集合下标属性越界：{}:{}", beanType.getTypeName(), property);
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
		Accessor accessor = properties.get(property);
		if (accessor == null || accessor.getter == null) {
			log.warn("不支持的属性：{}:{}", beanType.getTypeName(), property);
			return null;
		}
		return accessor.getter.apply(o);
	}

	/** 是否有属性值 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public boolean hasProperty(@Nonnull T o, @Nonnull String property) {
		if (isBasic || isEnum) {
			return false;
		}
		if (isArray) {
			int idx = 0;
			try {
				idx = Integer.parseInt(property);
			} catch (NumberFormatException e) {
				log.warn("数组下标属性错误：{}:{}", beanType.getTypeName(), property);
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
				log.warn("集合下标属性越界：{}:{}", beanType.getTypeName(), property);
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
		Accessor accessor = properties.get(property);
		if (accessor == null || accessor.getter == null) {
			return false;
		}
		return accessor.getter.apply(o) != null;
	}


	public Object getPathProperty(@Nonnull T o, @Nonnull String property) {
		return getPathProperty(o, Beans.parseProperty(property));
	}

	public Object setPathProperty(@Nonnull T o, @Nonnull String property, Object val) {
		return setPathProperty(o, Beans.parseProperty(property), val);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private Object setPathProperty(@Nonnull T o, Deque<String> properties, Object val) {
		if (val == null) {
			return null;
		}
		String property = properties.pollLast();
		if (properties.isEmpty()) {
			return setProperty(o, property, val);
		}
		PropertyInfo info = getRequiredPathProperty(o, properties);
		if (info == null) {
			return null;
		}
		if (info.propertyMeta.isArray) {
			return setArrayElement(info, property, info.propertyMeta.elementType, val);
		} else {
			return info.propertyMeta.setProperty(info.propertyObj, property, val);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private Object getPathProperty(T obj, Deque<String> properties) {
		Object target = obj;
		MetaObject meta = this;
		for (String property : properties) {
			target = meta.getProperty(target, property);
			if (target == null) {
				break;
			}
			meta = meta.getProperty(property);
		}
		return target;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private PropertyInfo getRequiredPathProperty(T obj, Deque<String> properties) {
		PropertyInfo info = new PropertyInfo("", obj, this, null, null);
		for (String property : properties) {
			Object propVal = info.propertyMeta.getPropertyOrSetDefault(info.propertyObj, property);
			MetaObject propMeta = info.propertyMeta.getProperty(property);
			if (propVal == null) {
				propVal = setArrayElement(info, property, propMeta, null);
				if (propVal == null) {
					return null;
				}
			}
			info.propertyName = property;
			info.parentObj = info.propertyObj;
			info.parentMeta = info.propertyMeta;
			info.propertyObj = propVal;
			info.propertyMeta = propMeta;
		}
		return info;
	}


	private Object setArrayElement(PropertyInfo info, String property, MetaObject propMeta, Object propVal) {
		if (info.parentMeta != null && info.propertyMeta.isArray) {
			int idx = Integer.parseInt(property);
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

	@AllArgsConstructor
	@Getter
	@Setter
	static class PropertyInfo {
		private String propertyName;
		private Object propertyObj;
		private MetaObject propertyMeta;
		private Object parentObj;
		private MetaObject parentMeta;
	}

	@AllArgsConstructor
	@Getter
	@Setter
	static class Accessor {
		private MetaObject<?> meta;
		private Function<Object, Object> getter;
		private BiConsumer<Object, Object> setter;
	}

	public JavaType<T> getBeanType() {
		return beanType;
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
}
