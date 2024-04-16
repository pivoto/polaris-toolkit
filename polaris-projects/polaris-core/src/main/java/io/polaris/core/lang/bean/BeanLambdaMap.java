package io.polaris.core.lang.bean;

import java.lang.reflect.Type;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
	private final Map<String, Function<Object, Object>> getters;
	private final Map<String, BiConsumer<Object, Object>> setters;
	private transient Set<Map.Entry<String, Object>> entrySet;
	private transient Collection<Object> values;
	private transient Set<String> keySet;

	BeanLambdaMap(T bean, Class<?> beanType, BeanMapOptions options) {
		super(bean, beanType, options);
		BeanLambdaAccess<?> access = BeanLambdaAccess.get(beanType);
		this.access = access;
		if (includeFields) {
			Map<String, Function<Object, Object>> getters = new HashMap<>();
			Map<String, BiConsumer<Object, Object>> setters = new HashMap<>();
			getters.putAll(access.propertyGetters());
			getters.putAll(access.fieldGetters());
			setters.putAll(access.propertySetters());
			setters.putAll(access.fieldSetters());
			this.getters = Collections.unmodifiableMap(getters);
			this.setters = Collections.unmodifiableMap(setters);
		} else {
			this.getters = access.propertyGetters();
			this.setters = access.propertySetters();
		}
	}

	public Type getType(String key) {
		return access.propertyGenericType(key);
	}

	@Override
	public Object get(Object key) {
		if (key instanceof String) {
			// property
			{
				Function<Object, Object> getter = getters.get((String) key);
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
			BiConsumer<Object, Object> setter = setters.get(key);
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
			throw new IllegalArgumentException("未知属性：" + key);
		}
		if (warnUnknownKeys) {
			log.warn("未知属性：{}.{}", beanType.getCanonicalName(), key);
		}
		return null;
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
		Collection<Object> vs = values;
		if (vs == null) {
			vs = new InnerValues();
			values = vs;
		}
		return vs;
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		Set<Map.Entry<String, Object>> es;
		return (es = entrySet) == null ? (entrySet = new InnerEntrySet()) : es;
	}

	@Override
	public boolean containsValue(Object value) {
		for (Entry<String, Function<Object, Object>> entry : getters.entrySet()) {
			Function<Object, Object> function = entry.getValue();
			Object obj = function.apply(bean);
			if (Objs.equals(obj, value)) {
				return true;
			}
		}
		return false;
	}


	final class InnerValues extends AbstractCollection<Object> {
		@Override
		public final int size() {
			return BeanLambdaMap.this.size();
		}

		@Override
		public final void clear() {
			BeanLambdaMap.this.clear();
		}

		@Override
		public final Iterator<Object> iterator() {
			return new Iterator<Object>() {
				private final Set<Entry<String, Function<Object, Object>>> entrySet = BeanLambdaMap.this.getters.entrySet();
				private final Iterator<Entry<String, Function<Object, Object>>> it = entrySet.iterator();

				@Override
				public boolean hasNext() {
					return it.hasNext();
				}

				@Override
				public Object next() {
					Entry<String, Function<Object, Object>> entry = it.next();
					return entry.getValue().apply(BeanLambdaMap.this.bean);
				}
			};
		}

		@Override
		public final boolean contains(Object o) {
			return BeanLambdaMap.this.containsValue(o);
		}
	}

	final class InnerEntrySet extends AbstractSet<Entry<String, Object>> {
		@Override
		public int size() {
			return BeanLambdaMap.this.size();
		}

		@Override
		public void clear() {
			BeanLambdaMap.this.clear();
		}

		@Override
		public Iterator<Entry<String, Object>> iterator() {
			return new Iterator<Entry<String, Object>>() {
				Set<Entry<String, Function<Object, Object>>> entrySet = BeanLambdaMap.this.getters.entrySet();
				Iterator<Entry<String, Function<Object, Object>>> it = entrySet.iterator();

				@Override
				public boolean hasNext() {
					return it.hasNext();
				}

				@Override
				public Entry<String, Object> next() {
					Entry<String, Function<Object, Object>> next = it.next();
					return new Map.Entry<String, Object>() {
						@Override
						public String getKey() {
							return next.getKey();
						}

						@Override
						public Object getValue() {
							return next.getValue().apply(BeanLambdaMap.this.bean);
						}

						@Override
						public Object setValue(Object value) {
							return BeanLambdaMap.this.put(next.getKey(), value);
						}
					};
				}
			};
		}

		@Override
		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}
			@SuppressWarnings("unchecked")
			Map.Entry<String, Object> e = (Map.Entry<String, Object>) o;
			String key = e.getKey();
			if (!BeanLambdaMap.this.containsKey(key)) {
				return false;
			}
			Object val = BeanLambdaMap.this.get(key);
			return Objs.equals(val, e.getValue());
		}

		@Override
		public final boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}
	}

}
