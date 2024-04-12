package io.polaris.core.lang.bean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import io.polaris.core.asm.reflect.BeanLambdaAccess;
import io.polaris.core.lang.Objs;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;

/**
 * @author Qt
 * @since 1.8,  Apr 12, 2024
 */
class BeanLambdaMap<T> extends BeanMap<T> {
	private static final ILogger log = ILoggers.of(BeanLambdaMap.class);
	private final BeanLambdaAccess<?> access;
	private final Set<String> setterPropertyNames;
	private final Set<String> getterPropertyNames;
	private final Set<String> fieldNames;

	public BeanLambdaMap(T bean, Class<?> beanType, BeanMapOptions options) {
		super(bean, beanType, options);
		BeanLambdaAccess<?> access = BeanLambdaAccess.get(beanType);
		this.access = access;
		this.setterPropertyNames = access.setterPropertyNames();
		this.getterPropertyNames = access.getterPropertyNames();
		this.fieldNames = access.fieldNames();
	}

	public Type getType(String key) {
		return access.propertyGenericType(key);
	}

	@Override
	public Object get(Object key) {
		if (key instanceof String) {
			// property
			{
				Function<Object, Object> getter = access.getGetter((String) key);
				if (getter != null) {
					return getter.apply(bean);
				}
			}
			// field
			if (includeOpenFields) {
				Function<Object, Object> getter = access.getFieldGetter((String) key);
				if (getter != null) {
					return getter.apply(bean);
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
			BiConsumer<Object, Object> setter = access.getSetter(key);
			if (setter != null) {
				if (hasConverter) {
					Type type = access.propertyGenericType(key);
					value = options.converter().apply(type, value);
				}
				setter.accept(bean, value);
				return null;
			}
		}
		// field
		if (includeOpenFields) {
			BiConsumer<Object, Object> setter = access.getFieldSetter(key);
			if (setter != null) {
				if (hasConverter) {
					Type type = access.propertyGenericType(key);
					value = options.converter().apply(type, value);
				}
				setter.accept(bean, value);
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
		return includeOpenFields ? getterPropertyNames.size() + fieldNames.size()
			: getterPropertyNames.size();
	}

	@Override
	public boolean isEmpty() {
		return getterPropertyNames.isEmpty() && (!includeOpenFields || fieldNames.isEmpty());
	}

	@Override
	public boolean containsKey(Object key) {
		return getterPropertyNames.contains(key) || includeOpenFields && fieldNames.contains(key);
	}

	@Override
	public Set<String> keySet() {
		LinkedHashSet<String> set = new LinkedHashSet<>(getterPropertyNames);
		if (includeOpenFields) {
			set.addAll(fieldNames);
		}
		return set;
	}

	@Override
	public Collection<Object> values() {
		List<Object> values = new ArrayList<>(size());

		for (String name : getterPropertyNames) {
			Object obj = access.getGetter(name).apply(bean);
			values.add(obj);
		}
		if (includeOpenFields) {
			for (String name : fieldNames) {
				Object obj = access.getFieldGetter(name).apply(bean);
				values.add(obj);
			}
		}
		return values;
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		Set<Map.Entry<String, Object>> set = new HashSet<>();
		for (String name : getterPropertyNames) {
			Function<Object, Object> function = access.getGetter(name);
			Map.Entry<String, Object> entry = new Map.Entry<String, Object>() {
				@Override
				public String getKey() {
					return name;
				}

				@Override
				public Object getValue() {
					return function.apply(bean);
				}

				@Override
				public Object setValue(Object value) {
					return put(name, value);
				}
			};
			set.add(entry);
		}
		if (includeOpenFields) {
			for (String name : fieldNames) {
				Function<Object, Object> function = access.getFieldGetter(name);
				Map.Entry<String, Object> entry = new Map.Entry<String, Object>() {
					@Override
					public String getKey() {
						return name;
					}

					@Override
					public Object getValue() {
						return function.apply(bean);
					}

					@Override
					public Object setValue(Object value) {
						return put(name, value);
					}
				};
				set.add(entry);
			}
		}
		return set;
	}

	@Override
	public boolean containsValue(Object value) {
		for (String name : getterPropertyNames) {
			Object obj = access.getGetter(name).apply(bean);
			if (Objs.equals(obj, value)) {
				return true;
			}
		}
		if (includeOpenFields) {
			for (String name : fieldNames) {
				Object obj = access.getFieldGetter(name).apply(bean);
				if (Objs.equals(obj, value)) {
					return true;
				}
			}
		}
		return false;
	}
}
