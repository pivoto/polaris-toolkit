package io.polaris.core.lang.bean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.polaris.core.asm.reflect.BeanAccess;
import io.polaris.core.lang.Objs;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;

/**
 * @author Qt
 * @since 1.8,  Apr 12, 2024
 */
class BeanIndexedMap<T> extends BeanMap<T> {
	private static final ILogger log = ILoggers.of(BeanIndexedMap.class);
	private final BeanAccess<?> access;
	private final Map<String, Integer> setterIndices;
	private final Map<String, Integer> getterIndices;
	private final Map<String, Integer> fieldIndices;

	public BeanIndexedMap(T bean, Class<?> beanType, BeanMapOptions options) {
		super(bean, beanType, options);
		BeanAccess<?> access = BeanAccess.get(beanType);
		this.access = access;
		this.setterIndices = access.setterIndices();
		this.getterIndices = access.getterIndices();
		this.fieldIndices = access.fieldIndices();
	}

	public Type getType(String key) {
		return access.propertyGenericType(key);
	}

	@Override
	public Object get(Object key) {
		if (key instanceof String) {
			// property
			{
				Integer idx = getterIndices.get((String) key);
				if (idx != null) {
					return access.getIndexProperty(bean, idx);
				}
			}
			// field
			if (includeOpenFields) {
				Integer idx = fieldIndices.get((String) key);
				if (idx != null) {
					return access.getIndexField(bean, idx);
				}
			}
			// fallback
			if (hasFallbackGetter) {
				return options.fallbackGetter().apply((String) key);
			}
		}
		return null;
	}


	@Override
	public Object put(String key, Object value) {
		// property
		{
			Integer idx = setterIndices.get(key);
			if (idx != null) {
				if (hasConverter) {
					Type type = access.propertyGenericType(key);
					value = options.converter().apply(type, value);
				}
				access.setIndexProperty(bean, idx, value);
				return null;
			}
		}
		// field
		if (includeOpenFields) {
			Integer idx = fieldIndices.get(key);
			if (idx != null) {
				if (hasConverter) {
					Type type = access.propertyGenericType(key);
					value = options.converter().apply(type, value);
				}
				access.setIndexField(bean, idx, value);
				return null;
			}
		}
		// fallback
		if (hasFallbackSetter) {
			options.fallbackSetter().accept(key, value);
			return null;
		}
		// noop
		if (!ignoreUnknownKeys) {
			throw new IllegalArgumentException("Unknown key：" + key);
		}
		if (warnUnknownKeys) {
			log.warn("Unknown key：{}.{}", beanType.getCanonicalName(), key);
		}
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ?> m) {
		m.forEach(this::put);
	}

	@Override
	public int size() {
		return includeOpenFields ? getterIndices.size() + fieldIndices.size()
			: getterIndices.size();
	}

	@Override
	public boolean isEmpty() {
		return getterIndices.isEmpty() && (!includeOpenFields || fieldIndices.isEmpty());
	}

	@Override
	public boolean containsKey(Object key) {
		return getterIndices.containsKey(key) || includeOpenFields && fieldIndices.containsKey(key);
	}

	@Override
	public Set<String> keySet() {
		LinkedHashSet<String> set = new LinkedHashSet<>(getterIndices.keySet());
		if (includeOpenFields) {
			set.addAll(fieldIndices.keySet());
		}
		return set;
	}

	@Override
	public Collection<Object> values() {
		List<Object> values = new ArrayList<>(size());

		getterIndices.forEach((name, idx) -> {
			Object obj = access.getIndexProperty(bean, idx);
			values.add(obj);
		});
		if (includeOpenFields) {
			fieldIndices.forEach((name, idx) -> {
				Object obj = access.getIndexField(bean, idx);
				values.add(obj);
			});
		}
		return values;
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		Set<Map.Entry<String, Object>> set = new HashSet<>();
		getterIndices.forEach((name, idx) -> {
			Map.Entry<String, Object> entry = new Map.Entry<String, Object>() {
				@Override
				public String getKey() {
					return name;
				}

				@Override
				public Object getValue() {
					return access.getIndexProperty(bean, idx);
				}

				@Override
				public Object setValue(Object value) {
					return put(name, value);
				}
			};
			set.add(entry);
		});
		if (includeOpenFields) {
			fieldIndices.forEach((name, idx) -> {
				Map.Entry<String, Object> entry = new Map.Entry<String, Object>() {
					@Override
					public String getKey() {
						return name;
					}

					@Override
					public Object getValue() {
						return access.getIndexField(bean, idx);
					}

					@Override
					public Object setValue(Object value) {
						return put(name, value);
					}
				};
				set.add(entry);
			});
		}
		return set;
	}

	@Override
	public boolean containsValue(Object value) {
		for (Entry<String, Integer> entry : getterIndices.entrySet()) {
			Object obj = access.getIndexProperty(bean, entry.getValue());
			if (Objs.equals(obj, value)) {
				return true;
			}
		}
		if (includeOpenFields) {
			for (Entry<String, Integer> entry : fieldIndices.entrySet()) {
				Object obj = access.getIndexField(bean, entry.getValue());
				if (Objs.equals(obj, value)) {
					return true;
				}
			}
		}
		return false;
	}
}
