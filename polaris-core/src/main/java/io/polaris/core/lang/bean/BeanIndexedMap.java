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

import io.polaris.core.asm.reflect.BeanAccess;
import io.polaris.core.lang.Objs;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;

/**
 * @author Qt
 * @since  Apr 12, 2024
 */
class BeanIndexedMap<T> extends BeanMap<T> {
	private static final Logger log = Loggers.of(BeanIndexedMap.class);
	private final BeanAccess<?> access;
	private final Map<String, Integer> setterIndices;
	private final Map<String, Integer> getterIndices;
	private final int fieldStartIndex;
	private transient Set<Map.Entry<String, Object>> entrySet;
	private transient Collection<Object> values;

	BeanIndexedMap(T bean, Class<?> beanType, BeanMapOptions options) {
		super(bean, beanType, options);
		BeanAccess<?> access = BeanAccess.get(beanType);
		this.access = access;
		if (includeFields) {
			Map<String, Integer> setterIndices = access.setterIndices();
			Map<String, Integer> getterIndices = access.getterIndices();
			Map<String, Integer> fieldIndices = access.fieldIndices();
			this.fieldStartIndex = Integer.max(setterIndices.size(), getterIndices.size());
			Map<String, Integer> setterIndicesTmp = new HashMap<>(setterIndices.size() + fieldIndices.size());
			Map<String, Integer> getterIndicesTmp = new HashMap<>(getterIndices.size() + fieldIndices.size());
			setterIndicesTmp.putAll(setterIndices);
			getterIndicesTmp.putAll(getterIndices);
			for (Entry<String, Integer> entry : fieldIndices.entrySet()) {
				setterIndicesTmp.put(entry.getKey(), fieldStartIndex + entry.getValue());
				getterIndicesTmp.put(entry.getKey(), fieldStartIndex + entry.getValue());
			}
			this.setterIndices = Collections.unmodifiableMap(setterIndicesTmp);
			this.getterIndices = Collections.unmodifiableMap(getterIndicesTmp);
		} else {
			this.setterIndices = access.setterIndices();
			this.getterIndices = access.getterIndices();
			this.fieldStartIndex = 0;
		}
	}

	public Type getType(String key) {
		return access.propertyGenericType(key);
	}

	@Override
	public Object get(Object key) {
		if (key instanceof String) {
			Integer idx = getterIndices.get((String) key);
			if (idx != null) {
				if (idx < fieldStartIndex) {
					return access.getIndexProperty(bean, idx);
				} else {
					return access.getIndexField(bean, idx - fieldStartIndex);
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
		{
			Integer idx = setterIndices.get(key);
			if (idx != null) {
				try {
					if (hasConverter) {
						if (value != null) {
							Type type = access.propertyGenericType(key);
							value = options.converter().apply(type, value);
						}
					}
					if (idx < fieldStartIndex) {
						access.setIndexProperty(bean, idx, value);
					} else {
						access.setIndexField(bean, idx - fieldStartIndex, value);
					}
				} catch (Exception e) {
					if (!ignoreError) {
						throw new IllegalArgumentException("属性写入失败：" + beanType.getCanonicalName() + "." + key, e);
					}
				}
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
		return getterIndices.size();
	}

	@Override
	public boolean isEmpty() {
		return getterIndices.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return getterIndices.containsKey(key);
	}

	@Override
	public Set<String> keySet() {
		return getterIndices.keySet();
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
		for (Entry<String, Integer> entry : getterIndices.entrySet()) {
			Integer idx = entry.getValue();
			Object obj;

			if (idx < fieldStartIndex) {
				obj = (access.getIndexProperty(bean, idx));
			} else {
				obj = (access.getIndexField(bean, idx - fieldStartIndex));
			}
			if (Objs.equals(obj, value)) {
				return true;
			}
		}
		return false;
	}

	final class InnerValues extends AbstractCollection<Object> {
		@Override
		public final int size() {
			return BeanIndexedMap.this.size();
		}

		@Override
		public final void clear() {
			BeanIndexedMap.this.clear();
		}

		@Override
		public final Iterator<Object> iterator() {
			return new Iterator<Object>() {
				private Set<Entry<String, Integer>> entrySet = BeanIndexedMap.this.getterIndices.entrySet();
				private Iterator<Entry<String, Integer>> it = entrySet.iterator();

				@Override
				public boolean hasNext() {
					return it.hasNext();
				}

				@Override
				public Object next() {
					Entry<String, Integer> next = it.next();
					Integer idx = next.getValue();
					if (idx < BeanIndexedMap.this.fieldStartIndex) {
						return (BeanIndexedMap.this.access.getIndexProperty(BeanIndexedMap.this.bean, idx));
					} else {
						return (BeanIndexedMap.this.access.getIndexField(BeanIndexedMap.this.bean, idx - BeanIndexedMap.this.fieldStartIndex));
					}
				}
			};
		}

		@Override
		public final boolean contains(Object o) {
			return BeanIndexedMap.this.containsValue(o);
		}
	}

	final class InnerEntrySet extends AbstractSet<Entry<String, Object>> {

		@Override
		public int size() {
			return BeanIndexedMap.this.size();
		}

		@Override
		public void clear() {
			BeanIndexedMap.this.clear();
		}

		@Override
		public Iterator<Entry<String, Object>> iterator() {
			return new Iterator<Entry<String, Object>>() {
				private final Set<Entry<String, Integer>> entrySet = BeanIndexedMap.this.getterIndices.entrySet();
				private final Iterator<Entry<String, Integer>> it = entrySet.iterator();

				@Override
				public boolean hasNext() {
					return it.hasNext();
				}

				@Override
				public Entry<String, Object> next() {
					Entry<String, Integer> next = it.next();
					return new Map.Entry<String, Object>() {
						@Override
						public String getKey() {
							return next.getKey();
						}

						@Override
						public Object getValue() {
							Integer idx = next.getValue();
							if (idx < BeanIndexedMap.this.fieldStartIndex) {
								return (BeanIndexedMap.this.access.getIndexProperty(BeanIndexedMap.this.bean, idx));
							} else {
								return (BeanIndexedMap.this.access.getIndexField(BeanIndexedMap.this.bean, idx - BeanIndexedMap.this.fieldStartIndex));
							}
						}

						@Override
						public Object setValue(Object value) {
							return BeanIndexedMap.this.put(next.getKey(), value);
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
			if (!BeanIndexedMap.this.containsKey(key)) {
				return false;
			}
			Object val = BeanIndexedMap.this.get(key);
			return Objs.equals(val, e.getValue());
		}

		@Override
		public final boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}
	}

}
