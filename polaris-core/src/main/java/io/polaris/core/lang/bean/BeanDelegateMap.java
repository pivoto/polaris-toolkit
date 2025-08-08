package io.polaris.core.lang.bean;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;

/**
 * @author Qt
 * @since  Apr 12, 2024
 */
class BeanDelegateMap<T> extends BeanMap<T> {
	private static final Logger log = Loggers.of(BeanDelegateMap.class);
	protected final Map<String, Object> raw;

	public BeanDelegateMap(T raw, Class<?> beanType, BeanMapOptions options) {
		super(raw, beanType, options);
		this.raw = (Map<String, Object>) raw;
	}

	@Override
	public <V> V copyToBean(V bean) {
		BeanMap<V> map = Beans.newBeanMap(bean);
		map.putAll(this.raw);
		return map.getBean();
	}

	@Override
	public Map<String, Object> copyToMap(Map<String, Object> map) {
		map.putAll(this.raw);
		return map;
	}

	@Override
	public Map<String, Object> copyToMap() {
		return copyToMap(new HashMap<>());
	}

	public Type getType(String key) {
		return Object.class;
	}

	@Override
	public Object get(Object key) {
		return this.raw.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		return this.raw.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ?> m) {
		this.raw.putAll(m);
	}

	@Override
	public int size() {
		return this.raw.size();
	}

	@Override
	public boolean isEmpty() {
		return this.raw.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.raw.containsKey(key);
	}

	@Override
	public Set<String> keySet() {
		return this.raw.keySet();
	}

	@Override
	public Collection<Object> values() {
		return this.raw.values();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return this.raw.entrySet();
	}

	@Override
	public boolean containsValue(Object value) {
		return this.raw.containsValue(value);
	}

	@Override
	public Object remove(Object key) {
		return this.raw.remove(key);
	}

	@Override
	public void clear() {
		this.raw.clear();
	}
}
