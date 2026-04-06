package io.polaris.core.collection.sync;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Qt
 * @since Apr 05, 2026
 */
public class SynchronizedMap<K, V> implements Map<K, V> {
	private final Map<K, V> raw;
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
	private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

	public SynchronizedMap(Map<K, V> raw) {
		if (raw == null) throw new NullPointerException();
		this.raw = raw;
	}

	// 读方法
	@Override
	public int size() {
		readLock.lock();
		try {return raw.size();} finally {readLock.unlock();}
	}

	@Override
	public boolean isEmpty() {
		readLock.lock();
		try {return raw.isEmpty();} finally {readLock.unlock();}
	}

	@Override
	public boolean containsKey(Object key) {
		readLock.lock();
		try {return raw.containsKey(key);} finally {readLock.unlock();}
	}

	@Override
	public boolean containsValue(Object value) {
		readLock.lock();
		try {return raw.containsValue(value);} finally {readLock.unlock();}
	}

	@Override
	public V get(Object key) {
		readLock.lock();
		try {return raw.get(key);} finally {readLock.unlock();}
	}

	// 写方法
	@Override
	public V put(K key, V value) {
		writeLock.lock();
		try {return raw.put(key, value);} finally {writeLock.unlock();}
	}

	@Override
	public V remove(Object key) {
		writeLock.lock();
		try {return raw.remove(key);} finally {writeLock.unlock();}
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		writeLock.lock();
		try {raw.putAll(m);} finally {writeLock.unlock();}
	}

	@Override
	public void clear() {
		writeLock.lock();
		try {raw.clear();} finally {writeLock.unlock();}
	}

	// 视图
	@Override
	public Set<K> keySet() {
		readLock.lock();
		try {return new SynchronizedSet<>(raw.keySet(), lock);} finally {readLock.unlock();}
	}

	@Override
	public Collection<V> values() {
		readLock.lock();
		try {return new SynchronizedCollection<>(raw.values(), lock);} finally {readLock.unlock();}
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		readLock.lock();
		try {return new SynchronizedSet<>(raw.entrySet(), lock);} finally {readLock.unlock();}
	}

	// ======================== DEFAULT 方法全部加锁 ========================
	@Override
	public V getOrDefault(Object key, V defaultValue) {
		readLock.lock();
		try {return raw.getOrDefault(key, defaultValue);} finally {readLock.unlock();}
	}

	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		readLock.lock();
		try {raw.forEach(action);} finally {readLock.unlock();}
	}

	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		writeLock.lock();
		try {raw.replaceAll(function);} finally {writeLock.unlock();}
	}

	@Override
	public V putIfAbsent(K key, V value) {
		writeLock.lock();
		try {return raw.putIfAbsent(key, value);} finally {writeLock.unlock();}
	}

	@Override
	public boolean remove(Object key, Object value) {
		writeLock.lock();
		try {return raw.remove(key, value);} finally {writeLock.unlock();}
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		writeLock.lock();
		try {return raw.replace(key, oldValue, newValue);} finally {writeLock.unlock();}
	}

	@Override
	public V replace(K key, V value) {
		writeLock.lock();
		try {return raw.replace(key, value);} finally {writeLock.unlock();}
	}

	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		writeLock.lock();
		try {return raw.computeIfAbsent(key, mappingFunction);} finally {writeLock.unlock();}
	}

	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		writeLock.lock();
		try {return raw.computeIfPresent(key, remappingFunction);} finally {writeLock.unlock();}
	}

	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		writeLock.lock();
		try {return raw.compute(key, remappingFunction);} finally {writeLock.unlock();}
	}

	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		writeLock.lock();
		try {return raw.merge(key, value, remappingFunction);} finally {writeLock.unlock();}
	}


}
