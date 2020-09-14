package io.polaris.core.map;

import io.polaris.core.map.reference.ReferenceType;
import io.polaris.core.map.reference.ValueReference;
import io.polaris.core.reflect.Reflects;

import java.lang.ref.Reference;
import java.util.*;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public class Maps {


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

	public static <K, V> Map<V, K> inverse(Map<K, V> map) {
		Map<V, K> result = createMap(map.getClass());
		map.forEach((key, value) -> result.put(value, key));
		return result;
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
}
