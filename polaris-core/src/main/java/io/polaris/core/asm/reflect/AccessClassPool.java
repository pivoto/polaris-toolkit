package io.polaris.core.asm.reflect;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * ASM 访问类缓存池
 * <p>基于 LRU 策略的访问类缓存容器，用于存储和管理动态生成的访问类实例</p>
 *
 * @author Qt
 * @since Apr 11, 2024
 * @param <K> 键类型
 * @param <V> 值类型
 */
class AccessClassPool<K, V> {
	/**
	 * 底层 LRU 映射
	 */
	private final LimitedLinkedHashMap<K, V> raw;
	/**
	 * 线程安全的缓存池
	 */
	private final Map<K, V> pool;

	/**
	 * 构造函数
	 *
	 * @param maxCapacity 最大容量
	 * @param initialCapacity 初始容量
	 */
	public AccessClassPool(int maxCapacity, int initialCapacity) {
		raw = new LimitedLinkedHashMap<>(maxCapacity, true, initialCapacity);
		pool = Collections.synchronizedMap(raw);
	}

	/**
	 * 构造函数（默认初始容量）
	 *
	 * @param maxCapacity 最大容量
	 */
	public AccessClassPool(int maxCapacity) {
		this(maxCapacity, 128);
	}

	/**
	 * 构造函数（默认最大容量 10240）
	 */
	public AccessClassPool() {
		this(10240);
	}

	public V get(K key) {
		return pool.get(key);
	}

	public V put(K key, V value) {
		return pool.put(key, value);
	}

	public V remove(Object key) {
		return pool.remove(key);
	}

	public V putIfAbsent(K key, V value) {
		return pool.putIfAbsent(key, value);
	}

	public boolean remove(Object key, Object value) {
		return pool.remove(key, value);
	}

	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		return pool.computeIfAbsent(key, mappingFunction);
	}

	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return pool.computeIfPresent(key, remappingFunction);
	}

	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return pool.compute(key, remappingFunction);
	}

	public void clear() {
		pool.clear();
	}

	public int size() {
		return pool.size();
	}

	public int getMaxCapacity() {
		return raw.getMaxCapacity();
	}

	public void setMaxCapacity(int maxCapacity) {
		raw.setMaxCapacity(maxCapacity);
	}


	/**
	 * 有限容量的 LinkedHashMap
	 * <p>基于 LRU 策略，当超过最大容量时自动移除最久未使用的元素</p>
	 *
	 * @param <K> 键类型
	 * @param <V> 值类型
	 */
	static class LimitedLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

		/**
		 * 最大容量限制
		 */
		private int maxCapacity = 0;

		/**
		 * 构造函数
		 *
		 * @param maxCapacity 最大容量
		 */
		public LimitedLinkedHashMap(int maxCapacity) {
			this(maxCapacity, true);
		}

		/**
		 * 构造函数
		 *
		 * @param maxCapacity 最大容量
		 * @param accessOrder 是否按访问顺序排序
		 */
		public LimitedLinkedHashMap(int maxCapacity, boolean accessOrder) {
			this(maxCapacity, accessOrder, 128, .75f);
		}

		/**
		 * 构造函数
		 *
		 * @param maxCapacity 最大容量
		 * @param accessOrder 是否按访问顺序排序
		 * @param initialCapacity 初始容量
		 */
		public LimitedLinkedHashMap(int maxCapacity, boolean accessOrder, int initialCapacity) {
			this(maxCapacity, accessOrder, initialCapacity, .75f);
		}

		/**
		 * 构造函数
		 *
		 * @param maxCapacity 最大容量
		 * @param accessOrder 是否按访问顺序排序
		 * @param initialCapacity 初始容量
		 * @param loadFactor 负载因子
		 */
		public LimitedLinkedHashMap(int maxCapacity, boolean accessOrder, int initialCapacity, float loadFactor) {
			super(initialCapacity, loadFactor, accessOrder);
			this.maxCapacity = maxCapacity;
		}

		public void setMaxCapacity(int maxCapacity) {
			this.maxCapacity = maxCapacity;
		}

		public int getMaxCapacity() {
			return maxCapacity;
		}

		@Override
		protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
			return size() > maxCapacity;
		}
	}

}
