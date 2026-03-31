package io.polaris.core.map;

import java.lang.ref.Reference;
import java.util.AbstractMap;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import io.polaris.core.map.reference.ReferenceType;
import io.polaris.core.map.reference.ValueReference;
import io.polaris.core.random.Randoms;
import io.polaris.core.reflect.Reflects;

/**
 * @author Qt
 */
public class Maps {
	/**
	 * 检查Map是否可修改
	 *
	 * @param map 要检查的Map对象
	 * @return true = 可修改; false = 不可修改
	 */
	public static boolean isModifiable(Map<?, ?> map) {
		// 空指针直接返回不可修改
		if (map == null) {
			return false;
		}
		try {
			@SuppressWarnings("unchecked")
			Map<Object, Object> tempMap = (Map<Object, Object>) map;
			// 生成唯一临时key，避免覆盖原数据
			String prefix = "__temp_key_check_modifiable_" + System.nanoTime();
			String tempKey = prefix;
			int i = 0;
			while (tempMap.containsKey(tempKey)) {
				tempKey = prefix + i++;
			}
			// 尝试插入：不可变Map会抛出 UnsupportedOperationException
			tempMap.put(tempKey, new Object());
			// 插入成功 → 可修改，立即删除临时数据
			tempMap.remove(tempKey);
			if (tempMap.containsKey(tempKey)){
				throw new IllegalStateException("检查可修改性时发生异常，原Map已被污染，残留了测试数据！");
			}
			return true;
		} catch (UnsupportedOperationException e) {
			// 标准不可变集合抛出的异常 → 不可修改
			return false;
		} catch (ClassCastException e) {
			// 泛型强转失败（极端情况）→ 视为不可修改
			return false;
		} catch (Exception e) {
			// 其他异常 → 安全起见，视为不可修改
			return false;
		}
	}

	/**
	 * 创建指定类型的Map
	 *
	 * @param mapType
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K, V> Map<K, V> createMap(Class<?> mapType) {
		if (null == mapType || mapType.isAssignableFrom(AbstractMap.class)) {
			return new HashMap<>();
		} else {
			try {
				return (Map<K, V>) Reflects.newInstance(mapType);
			} catch (ReflectiveOperationException e) {
				return new HashMap<>();
			}
		}
	}

	public static <K, V> Map<V, K> reverse(Map<K, V> map, Map<V, K> target) {
		map.forEach((key, value) -> target.put(value, key));
		return target;
	}

	/**
	 * 反转键值生成新的Map对象
	 *
	 * @param map
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K, V> Map<V, K> reverse(Map<K, V> map) {
		Map<V, K> result = createMap(map.getClass());
		map.forEach((key, value) -> result.put(value, key));
		return result;
	}

	public static <K, V> Map<V, K> inverse(Map<K, V> map, Map<V, K> target) {
		return reverse(map, target);
	}

	public static <K, V> Map<V, K> inverse(Map<K, V> map) {
		return reverse(map);
	}


	public static <K, V> FluentMap<K, V> newFluentMap(Map<K, V> map) {
		return new FluentMap<K, V>(map);
	}

	public static <K, V> Map<K, V> newLimitCapacityMap(int maxCapacity) {
		return new LimitedLinkedHashMap<K, V>(maxCapacity);
	}

	public static <K, V> Map<K, V> newLimitCapacityMap(int maxCapacity, boolean accessOrder) {
		return new LimitedLinkedHashMap<K, V>(maxCapacity, accessOrder);
	}

	public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> type) {
		return new EnumMap<K, V>(type);
	}

	public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
		return new LinkedHashMap<K, V>();
	}

	public static <K, V> HashMap<K, V> newHashMap() {
		return new HashMap<K, V>();
	}

	public static <K extends Comparable<K>, V> TreeMap<K, V> newTreeMap() {
		return new TreeMap<K, V>();
	}

	public static <V> Map<String, V> newLowerCaseHashMap() {
		return new CaseInsensitiveMap<>(new HashMap<>(), false);
	}

	public static <V> Map<String, V> newUpperCaseHashMap() {
		return new CaseInsensitiveMap<>(new HashMap<>(), true);
	}

	public static <V> Map<String, V> newLowerCaseLinkedHashMap() {
		return new CaseInsensitiveMap<>(new LinkedHashMap<>(), false);
	}

	public static <V> Map<String, V> newUpperCaseLinkedHashMap() {
		return new CaseInsensitiveMap<>(new LinkedHashMap<>(), true);
	}

	public static <K, V> Map<K, V> newSoftHashMap() {
		return new SoftHashMap<>();
	}

	public static <K, V> Map<K, V> newWeakHashMap() {
		return new WeakHashMap<>();
	}

	public static <K, V> Map<K, V> newSoftKeyHashMap() {
		return new SoftKeyHashMap<>();
	}

	public static <K, V> Map<K, V> newWeakKeyHashMap() {
		return new WeakKeyHashMap<>();
	}

	public static <K, V> Map<K, V> newSoftValueHashMap() {
		return new SoftValueHashMap<>();
	}

	public static <K, V> Map<K, V> newWeakValueHashMap() {
		return new WeakValueHashMap<>();
	}

	public static <K, V> Map<K, V> newSoftMap(Supplier<Map<Reference<K>, ValueReference<Reference<K>, V>>> raw) {
		return new ReferenceMap<>(raw, ReferenceType.SOFT);
	}

	public static <K, V> Map<K, V> newWeakMap(Supplier<Map<Reference<K>, ValueReference<Reference<K>, V>>> raw) {
		return new ReferenceMap<>(raw, ReferenceType.WEAK);
	}

	public static <K, V> Map<K, V> newSoftKeyMap(Supplier<Map<Reference<K>, V>> raw) {
		return new KeyReferenceMap<>(raw, ReferenceType.SOFT);
	}

	public static <K, V> Map<K, V> newWeakKeyMap(Supplier<Map<Reference<K>, V>> raw) {
		return new KeyReferenceMap<>(raw, ReferenceType.WEAK);
	}

	public static <K, V> Map<K, V> newSoftValueMap(Supplier<Map<K, ValueReference<K, V>>> raw) {
		return new ValueReferenceMap<>(raw, ReferenceType.SOFT);
	}

	public static <K, V> Map<K, V> newWeakValueMap(Supplier<Map<K, ValueReference<K, V>>> raw) {
		return new ValueReferenceMap<>(raw, ReferenceType.WEAK);
	}

	public static <K, V> Map<K, V> newSoftMap(Map<Reference<K>, ValueReference<Reference<K>, V>> raw) {
		return new ReferenceMap<>(raw, ReferenceType.SOFT);
	}

	public static <K, V> Map<K, V> newWeakMap(Map<Reference<K>, ValueReference<Reference<K>, V>> raw) {
		return new ReferenceMap<>(raw, ReferenceType.WEAK);
	}

	public static <K, V> Map<K, V> newSoftKeyMap(Map<Reference<K>, V> raw) {
		return new KeyReferenceMap<>(raw, ReferenceType.SOFT);
	}

	public static <K, V> Map<K, V> newWeakKeyMap(Map<Reference<K>, V> raw) {
		return new KeyReferenceMap<>(raw, ReferenceType.WEAK);
	}

	public static <K, V> Map<K, V> newSoftValueMap(Map<K, ValueReference<K, V>> raw) {
		return new ValueReferenceMap<>(raw, ReferenceType.SOFT);
	}

	public static <K, V> Map<K, V> newWeakValueMap(Map<K, ValueReference<K, V>> raw) {
		return new ValueReferenceMap<>(raw, ReferenceType.WEAK);
	}


	public static <K, V> boolean isEmpty(Map<K, V> map) {
		return map == null || map.isEmpty();
	}

	public static <K, V> boolean isNotEmpty(Map<K, V> map) {
		return map != null && !map.isEmpty();
	}


	public static Map<String, String> asMap(Map<String, String> map, String... args) {
		for (int i = 0; i + 1 < args.length; i += 2) {
			map.put(args[i], args[i + 1]);
		}
		return map;
	}

	public static <T extends Map<String, String>> T asMap(Supplier<T> supplier, String... args) {
		T map = supplier.get();
		asMap(map, args);
		return map;
	}

	public static Map<String, String> asMap(String... args) {
		return asMap((Supplier<Map<String, String>>) HashMap::new, args);
	}


	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> asMap(Map<K, V> map, Object... args) {
		for (int i = 0; i + 1 < args.length; i += 2) {
			map.put((K) args[i], (V) args[i + 1]);
		}
		return map;
	}

	public static <T extends Map<K, V>, K, V> T asMap(Supplier<T> supplier, Object... args) {
		T map = supplier.get();
		asMap(map, args);
		return map;
	}

	public static <K, V> Map<K, V> asMap(Object... args) {
		return asMap((Supplier<Map<K, V>>) HashMap::new, args);
	}

}
