package io.polaris.core.collection;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Qt
 */
public class Sets {
	public static <E> Set<E> newHashSet() {
		return new HashSet<>();
	}

	public static <E> Set<E> newLinkedHashSet() {
		return new LinkedHashSet<>();
	}


	public static <E extends Comparable<? super E>> Set<E> newTreeSet() {
		return new TreeSet<>(Comparator.naturalOrder());
	}

	public static <E> Set<E> newTreeSet(Comparator<? super E> comparator) {
		return new TreeSet<>(comparator);
	}

	public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet() {
		return new CopyOnWriteArraySet<E>();
	}


	public static <E> Set<E> asSet(Collection<E> collection) {
		return new HashSet<>(collection);
	}

	public static <E> Set<E> asSet(Enumeration<E> enumeration) {
		Set<E> c = new HashSet<>();
		while (enumeration.hasMoreElements()) {
			c.add(enumeration.nextElement());
		}
		return c;
	}

	@SafeVarargs
	public static <E> Set<E> asSet(E... iterable) {
		Set<E> c = new HashSet<>();
		Collections.addAll(c, iterable);
		return c;
	}

	public static <E> Set<E> asSet(Iterable<E> iterable) {
		Set<E> c = new HashSet<>();
		for (E e : iterable) {
			c.add(e);
		}
		return c;
	}

	public static <E> Set<E> asSet(Iterator<E> iterator) {
		Set<E> c = new HashSet<>();
		while (iterator.hasNext()) {
			c.add(iterator.next());
		}
		return c;
	}

	public static <E> Set<E> asSet(Supplier<Set<E>> supplier, Enumeration<E> enumeration) {
		Set<E> c = supplier.get();
		while (enumeration.hasMoreElements()) {
			c.add(enumeration.nextElement());
		}
		return c;
	}

	@SafeVarargs
	public static <E> Set<E> asSet(Supplier<Set<E>> supplier, E... iterable) {
		Set<E> c = supplier.get();
		Collections.addAll(c, iterable);
		return c;
	}

	public static <E> Set<E> asSet(Supplier<Set<E>> supplier, Iterable<E> iterable) {
		Set<E> c = supplier.get();
		for (E e : iterable) {
			c.add(e);
		}
		return c;
	}

	public static <E> Set<E> asSet(Supplier<Set<E>> supplier, Iterator<E> iterator) {
		Set<E> c = supplier.get();
		while (iterator.hasNext()) {
			c.add(iterator.next());
		}
		return c;
	}

	public static <S, T> Set<T> convert(Set<S> set, Function<S, T> converter, Function<T, S> reconvert) {
		return new Set<T>() {
			@Override
			public int size() {
				return set.size();
			}

			@Override
			public boolean isEmpty() {
				return set.isEmpty();
			}

			@Override
			public boolean contains(Object o) {
				try {
					return set.contains(converter.apply((S) o));
				} catch (ClassCastException e) {
					return false;
				}
			}

			@Override
			public Iterator<T> iterator() {
				return Iterators.convert(set.iterator(), converter);
			}

			@Override
			public Object[] toArray() {
				Object[] origin = set.toArray();
				Object[] array = new Object[origin.length];
				for (int i = 0; i < array.length; i++) {
					array[i] = converter.apply((S) origin[i]);
				}
				return array;
			}

			@Override
			public <E> E[] toArray(E[] a) {
				int size = size();
				if (a.length < size) {
					if (a.getClass() == Object[].class) {
						a = (E[]) new Object[size];
					} else {
						a = (E[]) Array.newInstance(a.getClass().getComponentType(), size);
					}
				}
				Object[] origin = set.toArray();
				for (int i = 0; i < size; i++) {
					a[i] = (E) converter.apply((S) origin[i]);
				}
				return a;
			}

			@Override
			public boolean add(T t) {
				if (reconvert != null) {
					return set.add(reconvert.apply(t));
				} else {
					throw new UnsupportedOperationException();
				}
			}

			@Override
			public boolean remove(Object o) {
				if (reconvert != null) {
					return set.remove(reconvert.apply((T) o));
				} else {
					throw new UnsupportedOperationException();
				}
			}

			@Override
			public boolean containsAll(Collection<?> c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean addAll(Collection<? extends T> c) {
				if (reconvert != null) {
					boolean changed = false;
					for (T t : c) {
						if (set.add(reconvert.apply(t))) {
							changed = true;
						}
					}
					return changed;
				} else {
					throw new UnsupportedOperationException();
				}
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void clear() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public static <T> Set<T> emptyIfNull(Set<T> set) {
		return (null == set) ? Collections.emptySet() : set;
	}

	public static boolean contains(Set<?> collection, Object value) {
		return collection != null && !collection.isEmpty() && collection.contains(value);
	}

	public static <T> boolean contains(Set<T> collection, Predicate<? super T> predicate) {
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

	public static boolean containsAny(Set<?> coll1, Set<?> coll2) {
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

	public static boolean containsAll(Set<?> coll1, Collection<?> coll2) {
		if (coll1 == null || coll1.isEmpty()) {
			return coll2 == null || coll2.isEmpty();
		}

		if (coll2 == null || coll2.isEmpty()) {
			return true;
		}

		// noinspection SuspiciousMethodCalls
		return coll1.containsAll(coll2);
	}

	public static <T> T firstNonNull(Set<T> collection) {
		return firstMatch(collection, Objects::nonNull);
	}

	public static <T> T firstMatch(Set<T> collection, Predicate<T> matcher) {
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

	public static <T> boolean anyMatch(Set<T> collection, Predicate<T> predicate) {
		if (collection == null || collection.isEmpty()) {
			return false;
		}
		return collection.stream().anyMatch(predicate);
	}

	public static <T> boolean allMatch(Set<T> collection, Predicate<T> predicate) {
		if (collection == null || collection.isEmpty()) {
			return false;
		}
		return collection.stream().allMatch(predicate);
	}

	public static <T> T get(Set<T> collection, int index) {
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

		return Iterators.get(collection.iterator(), index);
	}

	public static <T> List<T> getAll(Set<T> collection, int... indexes) {
		final int size = collection.size();
		final List<T> result = new ArrayList<>();
		final Object[] array = collection.toArray();
		for (int index : indexes) {
			if (index < 0) {
				index += size;
			}
			if (index >= size || index < 0) {
				result.add(null);
			} else {
				// noinspection unchecked
				result.add((T) array[index]);
			}
		}
		return result;
	}

	public static <T> T getFirst(Set<T> set) {
		if (set == null || set.isEmpty()) {
			return null;
		}
		return Iterators.getNext(set.iterator());
	}
}
