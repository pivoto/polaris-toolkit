package io.polaris.core.lang.bean;

import io.polaris.core.converter.ConverterRegistry;
import io.polaris.core.lang.Objs;
import io.polaris.core.log.ILogger;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8
 */
public class BeanMap<T> extends AbstractMap<String, Object> implements IBeanMap<T>, Map<String, Object> {
	private static final ILogger log = ILogger.of(BeanMap.class);
	protected final BeanMetadata metadata;
	protected final Map<String, Function<Object, Object>> getters;
	protected final Map<String, BiConsumer<Object, Object>> setters;
	protected final Map<String, Type> types;
	protected final boolean compilable;
	protected final T bean;
	protected final Class<?> beanType;
	protected final BiFunction<Object, Type, Object> converter;
	protected final Function<String, Object> fallbackGetter;
	protected final BiConsumer<String, Object> fallbackSetter;
	protected final boolean ignoreUnknownKeys;

	public BeanMap(T bean) {
		this(bean, null, null, null, null, true, false);
	}

	public BeanMap(T bean
		, BiFunction<Object, Type, Object> converter
		, Function<String, Object> fallbackGetter
		, BiConsumer<String, Object> fallbackSetter) {
		this(bean, null, converter, fallbackGetter, fallbackSetter, true, true);
	}

	public BeanMap(T bean, Class<?> beanType
		, BiFunction<Object, Type, Object> converter
		, Function<String, Object> fallbackGetter
		, BiConsumer<String, Object> fallbackSetter) {
		this(bean, beanType, converter, fallbackGetter, fallbackSetter, true, true);
	}

	public BeanMap(T bean, Class<?> beanType
		, BiFunction<Object, Type, Object> converter
		, Function<String, Object> fallbackGetter, BiConsumer<String, Object> fallbackSetter
		, boolean ignoreUnknownKeys, boolean compilable) {
		beanType = beanType != null ? beanType : bean.getClass();
		converter = converter != null ? converter : (o, t) -> ConverterRegistry.INSTANCE.convert(t, o);
		if (fallbackGetter == null) {
			if (bean instanceof Map) {
				//noinspection unchecked
				fallbackGetter = (k) -> ((Map<String, Object>) bean).get(k);
			}
		}
		if (fallbackSetter == null) {
			if (bean instanceof Map) {
				//noinspection unchecked
				fallbackSetter = (k, v) -> ((Map<String, Object>) bean).put(k, v);
			}
		}
		this.bean = bean;
		this.beanType = beanType;
		this.compilable = compilable;
		this.ignoreUnknownKeys = ignoreUnknownKeys;
		this.converter = converter;
		this.fallbackGetter = fallbackGetter;
		this.fallbackSetter = fallbackSetter;
		BeanMetadata metadata = this.getBeanMetadata(beanType);
		this.metadata = metadata;
		this.types = metadata.types();
		this.getters = metadata.getters();
		this.setters = metadata.setters();
	}


	protected BeanMetadata getBeanMetadata(Class<?> clazz) {
		if (compilable) {
			return BeanMetadatas.getMetadata(clazz);
		}
		try {
			Map<String, Type> types = new HashMap<>();
			Map<String, Function<Object, Object>> getters = new LinkedHashMap<>();
			Map<String, BiConsumer<Object, Object>> setters = new LinkedHashMap<>();
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
				String name = pd.getName();
				if (Objs.equals("class", name)) {
					continue;
				}
				Method readMethod = pd.getReadMethod();
				Method writeMethod = pd.getWriteMethod();
				if (readMethod != null) {
					getters.put(name, (bean) -> {
						try {
							return readMethod.invoke(bean);
						} catch (ReflectiveOperationException e) {
							throw new RuntimeException(e);
						}
					});
				}
				if (writeMethod != null) {
					Type type = writeMethod.getGenericParameterTypes()[0];
					types.put(name, type);
					setters.put(name, (bean, o) -> {
						try {
							writeMethod.invoke(bean, o);
						} catch (ReflectiveOperationException e) {
							throw new RuntimeException(e);
						}
					});
				}
			}
			return new BeanMetadatas.BeanMetadataCache(types, getters, setters);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public <V> V copyToBean(V bean) {
		BeanMap<V> map = new BeanMap<>(bean);
		map.putAll(this);
		return map.getBean();
	}

	@Override
	public Map<String, Object> copyToMap(Map<String, Object> map) {
		map.putAll(this);
		return map;
	}

	@Override
	public Map<String, Object> copyToMap() {
		return copyToMap(new HashMap<>());
	}

	@Override
	public String toString() {
		return bean.toString();
	}

	@Override
	public T getBean() {
		return bean;
	}


	public Class<?> getBeanType() {
		return beanType;
	}

	public Type getType(String key) {
		return (Type) this.types.get(key);
	}

	@Override
	public Object get(Object key) {
		if (key instanceof String) {
			Function<Object, Object> function = getters.get(key);
			if (function != null) {
				return function.apply(bean);
			}
			if (fallbackGetter != null) {
				return fallbackGetter.apply((String) key);
			}
		}
		return null;
	}

	@Override
	public Object put(String key, Object value) {
		Object old = get(key);
		BiConsumer<Object, Object> consumer = setters.get(key);
		if (consumer != null) {
			if (converter != null) {
				Type type = types.get(key);
				if (type != null) {
					value = converter.apply(value, type);
				}
			}
			consumer.accept(bean, value);
		} else {
			if (fallbackSetter != null) {
				fallbackSetter.accept(key, value);
			} else {
				if (!ignoreUnknownKeys) {
					throw new IllegalArgumentException("未知属性：" + key);
				} else {
					log.warn("未知属性：{}.{}", bean.getClass().getCanonicalName(), key);
				}
			}
		}
		return old;
	}


	@Override
	public void putAll(Map<? extends String, ?> m) {
		m.forEach(this::put);
	}


	@Override
	public int size() {
		return getters.size();
	}

	@Override
	public boolean isEmpty() {
		return getters.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return getters.containsKey(key);
	}

	@Override
	public Set<String> keySet() {
		return getters.keySet();
	}

	@Override
	public Collection<Object> values() {
		List<Object> values = new ArrayList<>(getters.size());
		for (Entry<String, Function<Object, Object>> e : getters.entrySet()) {
			values.add(e.getValue().apply(bean));
		}
		return values;
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		Set<Map.Entry<String, Object>> set = new HashSet<>();
		for (Entry<String, Function<Object, Object>> e : getters.entrySet()) {
			String key = e.getKey();
			Function<Object, Object> function = e.getValue();
			Map.Entry<String, Object> entry = new Map.Entry<String, Object>() {
				@Override
				public String getKey() {
					return key;
				}

				@Override
				public Object getValue() {
					return function.apply(bean);
				}

				@Override
				public Object setValue(Object value) {
					return put(key, value);
				}
			};
			set.add(entry);
		}
		return set;
	}

	@Override
	public boolean containsValue(Object value) {
		for (Entry<String, Function<Object, Object>> entry : getters.entrySet()) {
			Function<Object, Object> function = entry.getValue();
			Object obj1 = function.apply(bean);
			if (Objs.equals(obj1, value)) {
				return true;
			}
		}
		return true;
	}

	@Override
	public Object remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

}
