package io.polaris.core.lang.bean;

import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;

/**
 * @author Qt
 * @since 1.8
 */
public class BeanMap<T> extends AbstractMap<String, Object> implements IBeanMap<T>, Map<String, Object> {
	private static final ILogger log = ILoggers.of(BeanMap.class);
	protected final T bean;
	protected final Class<?> beanType;
	protected final BeanMapOptions options;
	protected final boolean includeFields;
	protected final boolean hasConverter;
	protected final boolean hasFallbackSetter;
	protected final boolean hasFallbackGetter;
	protected final boolean ignoreUnknownKeys;
	protected final boolean warnUnknownKeys;

	protected BeanMap(T bean, Class<?> beanType, BeanMapOptions options) {
		beanType = beanType != null ? beanType : bean.getClass();
		options = options == null ? new BeanMapOptions() : options;
		this.bean = bean;
		this.beanType = beanType;
		this.options = options;
		this.includeFields = options.includeFields();
		this.hasConverter = options.enableConverter() && options.converter() != null;
		this.hasFallbackSetter = options.enableFallback() && options.fallbackSetter() != null;
		this.hasFallbackGetter = options.enableFallback() && options.fallbackGetter() != null;
		this.ignoreUnknownKeys = options.ignoreUnknownKeys();
		this.warnUnknownKeys = options.warnUnknownKeys();
	}

	@Override
	public <V> V copyToBean(V bean) {
		BeanMap<V> map = Beans.newBeanMap(bean);
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
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get(Object key) {
		throw new UnsupportedOperationException();
	}


	@Override
	public Object put(String key, Object value) {
		throw new UnsupportedOperationException();
	}


	@Override
	public void putAll(Map<? extends String, ?> m) {
		throw new UnsupportedOperationException();
	}


	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Object> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
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
