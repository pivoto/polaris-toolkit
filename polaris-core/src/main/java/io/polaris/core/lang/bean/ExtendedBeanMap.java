package io.polaris.core.lang.bean;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import io.polaris.core.lang.Objs;

/**
 * @author Qt
 * @since Aug 04, 2024
 */
public class ExtendedBeanMap<T> implements Map<String, Object> {
	protected final BeanMap<T> beanMap;
	protected final Map<String, Object> addition;
	private Collection<Object> values;
	private InnerEntrySet entrySet;

	public ExtendedBeanMap(BeanMap<T> beanMap) {
		this(beanMap, new HashMap<>());
	}

	public ExtendedBeanMap(BeanMap<T> beanMap, Map<String, Object> addition) {
		this.beanMap = beanMap;
		this.addition = addition;
	}

	@Override
	public String toString() {
		return beanMap.toString();
	}

	@Override
	public int size() {
		return beanMap.size() + addition.size();
	}

	@Override
	public boolean isEmpty() {
		return beanMap.isEmpty() && addition.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return beanMap.containsKey(key) || addition.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return beanMap.containsValue(value) || addition.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		Object o = beanMap.get(key);
		if (o == null) {
			o = addition.get(key);
		}
		return o;
	}

	@Override
	public Object put(String key, Object value) {
		if (beanMap.containsKey(key)) {
			return beanMap.put(key, value);
		}
		return addition.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		if (beanMap.containsKey(key)) {
			return beanMap.remove(key);
		}
		return addition.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		m.forEach(this::put);
	}

	@Override
	public void clear() {
		beanMap.clear();
		addition.clear();
	}

	@Override
	public Set<String> keySet() {
		if (addition.isEmpty()) {
			return beanMap.keySet();
		}
		Set<String> keys = new LinkedHashSet<>();
		keys.addAll(beanMap.keySet());
		keys.addAll(addition.keySet());
		return keys;
	}

	@Override
	public Collection<Object> values() {
		if (addition.isEmpty()) {
			return beanMap.values();
		}
		Collection<Object> vs = values;
		if (vs == null) {
			vs = new InnerValues();
			values = vs;
		}
		return vs;
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		if (addition.isEmpty()) {
			return beanMap.entrySet();
		}
		Set<Map.Entry<String, Object>> es;
		return (es = entrySet) == null ? (entrySet = new InnerEntrySet()) : es;
	}


	final class InnerValues extends AbstractCollection<Object> {
		@Override
		public final int size() {
			return ExtendedBeanMap.this.size();
		}

		@Override
		public final void clear() {
			ExtendedBeanMap.this.clear();
		}

		@Override
		public final Iterator<Object> iterator() {
			return new Iterator<Object>() {
				private final Set<Entry<String, Object>> entrySet = ExtendedBeanMap.this.beanMap.entrySet();
				private final Iterator<Entry<String, Object>> it = entrySet.iterator();
				private final Set<Entry<String, Object>> additionSet = ExtendedBeanMap.this.addition.entrySet();
				private final Iterator<Entry<String, Object>> additionIt = additionSet.iterator();

				@Override
				public boolean hasNext() {
					if (it.hasNext()) {
						return true;
					}
					return additionIt.hasNext();
				}

				@Override
				public Object next() {
					if (it.hasNext()) {
						return it.next();
					}
					return additionIt.next();
				}
			};
		}

		@Override
		public final boolean contains(Object o) {
			return ExtendedBeanMap.this.containsValue(o);
		}
	}

	final class InnerEntrySet extends AbstractSet<Entry<String, Object>> {
		@Override
		public int size() {
			return ExtendedBeanMap.this.size();
		}

		@Override
		public void clear() {
			ExtendedBeanMap.this.clear();
		}

		@Override
		public Iterator<Entry<String, Object>> iterator() {
			return new Iterator<Entry<String, Object>>() {
				private final Set<Entry<String, Object>> entrySet = ExtendedBeanMap.this.beanMap.entrySet();
				private final Iterator<Entry<String, Object>> it = entrySet.iterator();
				private final Set<Entry<String, Object>> additionSet = ExtendedBeanMap.this.addition.entrySet();
				private final Iterator<Entry<String, Object>> additionIt = additionSet.iterator();

				@Override
				public boolean hasNext() {
					if (it.hasNext()) {
						return true;
					}
					return additionIt.hasNext();
				}

				@Override
				public Entry<String, Object> next() {
					if (it.hasNext()) {
						return it.next();
					}
					return additionIt.next();
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
			if (!ExtendedBeanMap.this.containsKey(key)) {
				return false;
			}
			Object val = ExtendedBeanMap.this.get(key);
			return Objs.equals(val, e.getValue());
		}

		@Override
		public final boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}
	}

}
