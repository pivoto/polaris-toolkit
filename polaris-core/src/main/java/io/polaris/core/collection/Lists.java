package io.polaris.core.collection;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Qt
 */
public class Lists {

	public static <E> List<E> newArrayList() {
		return new ArrayList<>();
	}

	public static <E> List<E> newLinkedList() {
		return new LinkedList<>();
	}

	public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
		return new CopyOnWriteArrayList<>();
	}

	public static <E> List<E> asList(Collection<E> collection) {
		return new ArrayList<>(collection);
	}

	public static <E> List<E> asList(Enumeration<E> enumeration) {
		List<E> c = new ArrayList<>();
		while (enumeration.hasMoreElements()) {
			c.add(enumeration.nextElement());
		}
		return c;
	}

	@SafeVarargs
	public static <E> List<E> asList(E... iterable) {
		List<E> c = new ArrayList<>();
		Collections.addAll(c, iterable);
		return c;
	}


	public static <E> List<E> asList(Iterable<E> iterable) {
		List<E> c = new ArrayList<>();
		for (E e : iterable) {
			c.add(e);
		}
		return c;
	}

	public static <E> List<E> asList(Iterator<E> iterator) {
		List<E> c = new ArrayList<>();
		while (iterator.hasNext()) {
			c.add(iterator.next());
		}
		return c;
	}

	public static <E> List<E> asList(Supplier<List<E>> supplier, Enumeration<E> enumeration) {
		List<E> c = supplier.get();
		while (enumeration.hasMoreElements()) {
			c.add(enumeration.nextElement());
		}
		return c;
	}

	@SafeVarargs
	public static <E> List<E> asList(Supplier<List<E>> supplier, E... iterable) {
		List<E> c = supplier.get();
		Collections.addAll(c, iterable);
		return c;
	}


	public static <E> List<E> asList(Supplier<List<E>> supplier, Iterable<E> iterable) {
		List<E> c = supplier.get();
		for (E e : iterable) {
			c.add(e);
		}
		return c;
	}

	public static <E> List<E> asList(Supplier<List<E>> supplier, Iterator<E> iterator) {
		List<E> c = supplier.get();
		while (iterator.hasNext()) {
			c.add(iterator.next());
		}
		return c;
	}


	public static <T> List<T> emptyIfNull(List<T> list) {
		return (null == list) ? Collections.emptyList() : list;
	}

	public static boolean contains(List<?> collection, Object value) {
		return collection != null && !collection.isEmpty() && collection.contains(value);
	}

	public static <T> boolean contains(List<T> collection, Predicate<? super T> predicate) {
		if (collection == null || collection.isEmpty()) {
			return false;
		}
		for (T t : collection) {
			if (predicate.test(t)) {
				return true;
			}
		}
		return false;
	}

	public static boolean containsAny(List<?> coll1, List<?> coll2) {
		if (coll1 == null || coll1.isEmpty() || coll2 == null || coll2.isEmpty()) {
			return false;
		}
		if (coll1.size() < coll2.size()) {
			for (Object object : coll1) {
				if (coll2.contains(object)) {
					return true;
				}
			}
		} else {
			for (Object object : coll2) {
				if (coll1.contains(object)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean containsAll(List<?> coll1, Collection<?> coll2) {
		if (coll1 == null || coll1.isEmpty()) {
			return coll2 == null || coll2.isEmpty();
		}

		if (coll2 == null || coll2.isEmpty()) {
			return true;
		}
		// 参考Apache commons collection4
		// 将时间复杂度降低到O(n + m)
		final Iterator<?> it = coll1.iterator();
		final Set<Object> elementsAlreadySeen = new HashSet<>(coll1.size(), 1);
		for (final Object nextElement : coll2) {
			if (elementsAlreadySeen.contains(nextElement)) {
				continue;
			}

			boolean foundCurrentElement = false;
			while (it.hasNext()) {
				final Object p = it.next();
				elementsAlreadySeen.add(p);
				if (Objects.equals(nextElement, p)) {
					foundCurrentElement = true;
					break;
				}
			}

			if (!foundCurrentElement) {
				return false;
			}
		}
		return true;
	}

	public static <T> T firstNonNull(List<T> collection) {
		return firstMatch(collection, Objects::nonNull);
	}

	public static <T> T firstMatch(List<T> collection, Predicate<T> matcher) {
		if (collection == null || collection.isEmpty()) {
			return null;
		}
		for (T next : collection) {
			if (matcher.test(next)) {
				return next;
			}
		}
		return null;
	}

	public static <T> boolean anyMatch(List<T> collection, Predicate<T> predicate) {
		if (collection == null || collection.isEmpty()) {
			return false;
		}
		return collection.stream().anyMatch(predicate);
	}

	public static <T> boolean allMatch(List<T> collection, Predicate<T> predicate) {
		if (collection == null || collection.isEmpty()) {
			return false;
		}
		return collection.stream().allMatch(predicate);
	}

	public static <T> T get(List<T> collection, int index) {
		if (null == collection) {
			return null;
		}

		final int size = collection.size();
		if (0 == size) {
			return null;
		}

		if (index < 0) {
			index += size;
		}

		// 检查越界
		if (index >= size || index < 0) {
			return null;
		}
		return collection.get(index);
	}


	public static <T> List<T> getAll(List<T> list, int... indexes) {
		final int size = list.size();
		final List<T> result = new ArrayList<>();
		for (int index : indexes) {
			if (index < 0) {
				index += size;
			}
			if (index >= size || index < 0) {
				result.add(null);
			} else {
				result.add(list.get(index));
			}
		}
		return result;
	}

	public static <T> T getFirst(List<T> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

}
