package io.polaris.core.lang.bean;

import io.polaris.core.converter.Converters;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.lang.Types;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.map.CaseInsensitiveMap;
import io.polaris.core.map.Maps;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.StringCases;
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
@SuppressWarnings({"rawtypes", "unchecked"})
public class MetaObject<T> {
	public static final int INIT = 0;
	public static final int PARSING = 1;
	public static final int READY = 2;
	public static final int CASE_INSENSITIVE = 1;
	public static final int CASE_CAMEL = 2;
	private static final ILogger log = ILoggers.of(MetaObject.class);
	private static final Map<JavaType<?>, MetaObject<?>> CACHE = Maps.newWeakKeyMap(new ConcurrentHashMap<>());
	private final JavaType<T> beanType;
	private int state = INIT;

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
	private Map<String, Accessor> propertiesCaseInsensitive;


	private MetaObject(JavaType<T> beanType) {
		this.beanType = beanType;
	}

	private void parse() {
		state = PARSING;
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
					this.propertiesCaseInsensitive = new CaseInsensitiveMap<>(new HashMap<>(size * 2), true);

					Map<String, Function<Object, Object>> getterMethods = metadata.getters();
					Map<String, BiConsumer<Object, Object>> setterMethods = metadata.setters();
					for (Map.Entry<String, Type> entry : types.entrySet()) {
						String key = entry.getKey();
						MetaObject<?> meta = MetaObject.of(JavaType.of(entry.getValue()));
						Function<Object, Object> getterMethod = getterMethods.get(key);
						BiConsumer<Object, Object> setterMethod = setterMethods.get(key);
						Accessor accessor = new Accessor(meta, getterMethod, setterMethod);
						properties.put(key, accessor);
						propertiesCaseInsensitive.put(key, accessor);
					}
					this.properties = Collections.unmodifiableMap(this.properties);
					this.propertiesCaseInsensitive = Collections.unmodifiableMap(this.propertiesCaseInsensitive);
					this.isBean = true;
				} else {
					this.isBasic = true;
				}
			}
		} finally {
			state = READY;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> MetaObject<T> of(JavaType<T> beanType) {
		MetaObject<T> metaObject = (MetaObject<T>) CACHE.computeIfAbsent(beanType, k -> new MetaObject<>(beanType));
		if (metaObject.state == INIT) {
			synchronized (metaObject) {
				if (metaObject.state == INIT) {
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

	public static int buildPropertyCaseModel(boolean caseInsensitive, boolean caseCamel) {
		return (caseInsensitive ? CASE_INSENSITIVE : 0) | (caseCamel ? CASE_CAMEL : 0);
	}

	public static boolean isCaseInsensitive(int propertyCaseModel) {
		return (CASE_INSENSITIVE & propertyCaseModel) != 0;
	}

	public static boolean isCaseCamel(int propertyCaseModel) {
		return (CASE_CAMEL & propertyCaseModel) != 0;
	}

	public T newInstance() {
		return Reflects.newInstanceIfPossible(this.beanType.getRawClass());
	}

	public Object getPropertyOrSetDefault(@Nonnull T o, @Nonnull String property) {
		return getPropertyOrSetDefault(o, 0, property);
	}

	public Object getPropertyOrSetDefault(@Nonnull T o, int propertyCaseModel, @Nonnull String property) {
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
			if (isCaseInsensitive(propertyCaseModel)) {
				accessor = propertiesCaseInsensitive.get(property);
			}
			if (accessor == null || accessor.getter == null || accessor.setter == null) {
				if (isCaseCamel(propertyCaseModel)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(property);
					accessor = properties.get(propertyCamelCase);
					if (accessor == null || accessor.getter == null || accessor.setter == null) {
						if (isCaseInsensitive(propertyCaseModel)) {
							accessor = propertiesCaseInsensitive.get(propertyCamelCase);
						}
					}
				}
			}
			if (accessor == null || accessor.getter == null || accessor.setter == null) {
				log.warn("不支持的属性：{}:{}", beanType.getTypeName(), property);
				return null;
			}
		}
		Object val = accessor.getter.apply(o);
		if (val == null) {
			val = accessor.meta.newInstance();
			accessor.setter.accept(o, val);
		}
		return val;
	}

	public Object setProperty(@Nonnull T o, @Nonnull String property, Object val) {
		return setProperty(o, 0, property, val);
	}

	public Object setProperty(@Nonnull T o, int propertyCaseModel, @Nonnull String property, Object val) {
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
			if (isCaseInsensitive(propertyCaseModel)) {
				accessor = propertiesCaseInsensitive.get(property);
			}
			if (accessor == null || accessor.setter == null) {
				if (isCaseCamel(propertyCaseModel)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(property);
					accessor = properties.get(propertyCamelCase);
					if (accessor == null || accessor.setter == null) {
						if (isCaseInsensitive(propertyCaseModel)) {
							accessor = propertiesCaseInsensitive.get(propertyCamelCase);
						}
					}
				}
			}
			if (accessor == null || accessor.setter == null) {
				log.warn("不支持的属性：{}:{}", beanType.getTypeName(), property);
				return null;
			}
		}
		accessor.setter.accept(o, val = Converters.convertQuietly(accessor.meta.beanType, val));
		return val;
	}

	public MetaObject<?> getProperty(@Nonnull String property) {
		return getProperty(0, property);
	}

	public MetaObject<?> getProperty(int propertyCaseModel, @Nonnull String property) {
		if (isBasic || isEnum) {
			log.warn("不支持的属性：{}:{}", beanType.getTypeName(), property);
			return null;
		}
		if (isArray || isCollection || isMap) {
			return this.elementType;
		}
		Accessor accessor = properties.get(property);
		if (accessor == null) {
			if (isCaseInsensitive(propertyCaseModel)) {
				accessor = propertiesCaseInsensitive.get(property);
			}
			if (accessor == null) {
				if (isCaseCamel(propertyCaseModel)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(property);
					accessor = properties.get(propertyCamelCase);
					if (accessor == null) {
						if (isCaseInsensitive(propertyCaseModel)) {
							accessor = propertiesCaseInsensitive.get(propertyCamelCase);
						}
					}
				}
			}
			if (accessor == null) {
				log.warn("不支持的属性：{}:{}", beanType.getTypeName(), property);
				return null;
			}
		}
		return accessor.meta;
	}

	public MetaObject<?> getPathProperty(int propertyCaseModel, @Nonnull String property) {
		Deque<String> properties = Beans.parseProperty(property);
		MetaObject meta = this;
		for (String key : properties) {
			meta = meta.getProperty(propertyCaseModel, key);
			if (meta == null) {
				break;
			}
		}
		return meta;
	}

	public Object getProperty(@Nonnull T o, @Nonnull String property) {
		return getProperty(o, 0, property);
	}

	public Object getProperty(@Nonnull T o, int propertyCaseModel, @Nonnull String property) {
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
			if (isCaseInsensitive(propertyCaseModel)) {
				accessor = propertiesCaseInsensitive.get(property);
			}
			if (accessor == null || accessor.getter == null) {
				if (isCaseCamel(propertyCaseModel)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(property);
					accessor = properties.get(propertyCamelCase);
					if (accessor == null || accessor.getter == null) {
						if (isCaseInsensitive(propertyCaseModel)) {
							accessor = propertiesCaseInsensitive.get(propertyCamelCase);
						}
					}
				}
			}
			if (accessor == null || accessor.getter == null) {
				log.warn("不支持的属性：{}:{}", beanType.getTypeName(), property);
				return null;
			}
		}
		return accessor.getter.apply(o);
	}

	public boolean hasProperty(@Nonnull T o, @Nonnull String property) {
		return hasProperty(o, 0, property);
	}

	public boolean hasProperty(@Nonnull T o, int propertyCaseModel, @Nonnull String property) {
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
			if (isCaseInsensitive(propertyCaseModel)) {
				accessor = propertiesCaseInsensitive.get(property);
			}
			if (accessor == null || accessor.getter == null) {
				if (isCaseCamel(propertyCaseModel)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(property);
					accessor = properties.get(propertyCamelCase);
					if (accessor == null || accessor.getter == null) {
						if (isCaseInsensitive(propertyCaseModel)) {
							accessor = propertiesCaseInsensitive.get(propertyCamelCase);
						}
					}
				}
			}
			if (accessor == null || accessor.getter == null) {
				return false;
			}
		}
		return accessor.getter.apply(o) != null;
	}


	public Object getPathProperty(@Nonnull T o, @Nonnull String property) {
		return getPathProperty(o, 0, Beans.parseProperty(property));
	}

	public Object getPathProperty(@Nonnull T o, int propertyCaseModel, @Nonnull String property) {
		return getPathProperty(o, propertyCaseModel, Beans.parseProperty(property));
	}

	public Object setPathProperty(@Nonnull T o, @Nonnull String property, Object val) {
		return setPathProperty(o, 0, Beans.parseProperty(property), val);
	}

	public Object setPathProperty(@Nonnull T o, int propertyCaseModel, @Nonnull String property, Object val) {
		return setPathProperty(o, propertyCaseModel, Beans.parseProperty(property), val);
	}

	private Object setPathProperty(@Nonnull T o, int propertyCaseModel, Deque<String> properties, Object val) {
		if (val == null) {
			return null;
		}
		String property = properties.pollLast();
		if (properties.isEmpty()) {
			return setProperty(o, propertyCaseModel, property, val);
		}
		PropertyInfo info = getRequiredPathProperty(o, propertyCaseModel, properties);
		if (info == null) {
			return null;
		}
		if (info.propertyMeta.isArray) {
			return setArrayElement(info, property, info.propertyMeta.elementType, val);
		} else {
			return info.propertyMeta.setProperty(info.propertyObj, propertyCaseModel, property, val);
		}
	}

	private Object getPathProperty(T obj, int propertyCaseModel, Deque<String> properties) {
		Object target = obj;
		MetaObject meta = this;
		for (String property : properties) {
			target = meta.getProperty(target, propertyCaseModel, property);
			if (target == null) {
				break;
			}
			meta = meta.getProperty(propertyCaseModel,property);
		}
		return target;
	}

	private PropertyInfo getRequiredPathProperty(T obj, int propertyCaseModel, Deque<String> properties) {
		PropertyInfo info = new PropertyInfo("", obj, this, null, null);
		for (String property : properties) {
			Object propVal = info.propertyMeta.getPropertyOrSetDefault(info.propertyObj, propertyCaseModel, property);
			MetaObject propMeta = info.propertyMeta.getProperty(propertyCaseModel,property);
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

	public MetaObject<?> getKeyType() {
		return keyType;
	}

	public MetaObject<?> getElementType() {
		return elementType;
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

}
