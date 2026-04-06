package io.polaris.core.collection.sync;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Qt
 * @since Apr 05, 2026
 */
public class SynchronizedCollections {

	public static <E> Set<E> synchronizedSet(Set<E> set) {
		return new SynchronizedSet<>(set);
	}

	public static <E> List<E> synchronizedList(List<E> list) {
		return new SynchronizedList<>(list);
	}

	public static <E> Collection<E> synchronizedCollection(Collection<E> c) {
		return new SynchronizedCollection<>(c);
	}

	public static <K, V> Map<K, V> synchronizedMap(Map<K, V> map) {
		return new SynchronizedMap<>(map);
	}

}
