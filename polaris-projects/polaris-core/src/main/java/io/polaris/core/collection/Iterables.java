package io.polaris.core.collection;

import javax.annotation.Nonnull;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public class Iterables {


	public static <E> Iterator<E> iterator(Enumeration<E> enumeration) {
		return new Iterator<E>() {
			@Override
			public boolean hasNext() {
				return enumeration.hasMoreElements();
			}

			@Override
			public E next() {
				return enumeration.nextElement();
			}
		};
	}

	public static <E> Enumeration<E> enumeration(Iterable<E> iterable) {
		return enumeration(iterable.iterator());
	}

	public static <E> Enumeration<E> enumeration(Iterator<E> iterator) {
		return new Enumeration<E>() {
			@Override
			public boolean hasMoreElements() {
				return iterator.hasNext();
			}

			@Override
			public E nextElement() {
				return iterator.next();
			}
		};
	}

	public static <C extends Collection<E>, E> C asCollection(Supplier<C> supplier, Enumeration<E> enumeration) {
		C c = supplier.get();
		while (enumeration.hasMoreElements()) {
			c.add(enumeration.nextElement());
		}
		return c;
	}

	public static <C extends Collection<E>, E> C asCollection(Supplier<C> supplier, E... iterable) {
		C c = supplier.get();
		Collections.addAll(c, iterable);
		return c;
	}

	public static <C extends Collection<E>, E> C asCollection(Supplier<C> supplier, Iterable<E> iterable) {
		C c = supplier.get();
		for (E e : iterable) {
			c.add(e);
		}
		return c;
	}

	public static <C extends Collection<E>, E> C asCollection(Supplier<C> supplier, Iterator<E> iterator) {
		C c = supplier.get();
		while (iterator.hasNext()) {
			c.add(iterator.next());
		}
		return c;
	}

	public static <E> List<E> asList(Enumeration<E> enumeration) {
		List<E> c = new ArrayList<>();
		while (enumeration.hasMoreElements()) {
			c.add(enumeration.nextElement());
		}
		return c;
	}

	public static <E> Set<E> asSet(Enumeration<E> enumeration) {
		Set<E> c = new HashSet<>();
		while (enumeration.hasMoreElements()) {
			c.add(enumeration.nextElement());
		}
		return c;
	}

	public static <E> List<E> asList(E... iterable) {
		List<E> c = new ArrayList<>();
		Collections.addAll(c, iterable);
		return c;
	}

	public static <E> Set<E> asSet(E... iterable) {
		Set<E> c = new HashSet<>();
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

	public static <E> Set<E> asSet(Iterable<E> iterable) {
		Set<E> c = new HashSet<>();
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

	public static <E> Set<E> asSet(Iterator<E> iterator) {
		Set<E> c = new HashSet<>();
		while (iterator.hasNext()) {
			c.add(iterator.next());
		}
		return c;
	}

	public static <E> E[] copyOf(E[] array) {
		return Arrays.copyOf(array, array.length);
	}

	public static <S, T> Iterator<T> convert(Iterator<S> iterator, Function<S, T> converter) {
		return new Iterator<T>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public T next() {
				return converter.apply(iterator.next());
			}

			@Override
			public void remove() {
				iterator.next();
			}
		};
	}

	public static <S, T> T[] convert(S[] array, T[] target, Function<S, T> converter) {
		if (target.length < array.length) {
			if (target.getClass() == Object[].class) {
				target = (T[]) new Object[array.length];
			} else {
				target = (T[]) Array.newInstance(target.getClass().getComponentType(), array.length);
			}
		}
		for (int i = 0; i < array.length; i++) {
			target[i] = (T) converter.apply((S) array[i]);
		}
		return target;
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
				return convert(set.iterator(), converter);
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


	public static <S, T> boolean isMatchAll(S[] array1, T[] array2, BiFunction<S, T, Boolean> matcher) {
		if (array1 == null && array2 == null) {
			return true;
		}
		if (array1 == null || array2 == null) {
			return false;
		}
		if (array1.length != array1.length) {
			return false;
		}
		for (int i = 0; i < array1.length; i++) {
			Boolean matched = matcher.apply(array1[i], array2[i]);
			if (!matched) {
				return false;
			}
		}
		return true;
	}

	public static <E> boolean isEmpty(Collection<E> array) {
		return array == null || array.isEmpty();
	}

	public static <E> boolean isEmpty(E[] array) {
		return array == null || array.length == 0;
	}

	public static <E> boolean isNotEmpty(Collection<E> array) {
		return array != null && !array.isEmpty();
	}

	public static <E> boolean isNotEmpty(E[] array) {
		return array != null && array.length > 0;
	}

	public static <E> boolean hasNull(E[] array) {
		if (array == null) {
			return false;
		}
		for (E e : array) {
			if (e == null) {
				return true;
			}
		}
		return false;
	}

	public static <E> boolean hasNull(Iterable<E> array) {
		if (array == null) {
			return false;
		}
		for (E e : array) {
			if (e == null) {
				return true;
			}
		}
		return false;
	}

	public static <E> boolean isMatchAny(E[] array, Function<E, Boolean> matcher) {
		if (array == null) {
			return false;
		}
		for (E e : array) {
			if (matcher.apply(e)) {
				return true;
			}
		}
		return false;
	}

	public static <E> boolean isMatchAny(Iterable<E> array, Function<E, Boolean> matcher) {
		if (array == null) {
			return false;
		}
		for (E e : array) {
			if (matcher.apply(e)) {
				return true;
			}
		}
		return false;
	}

	public static <E> boolean isMatchAll(E[] array, Function<E, Boolean> matcher) {
		if (array == null) {
			return false;
		}
		for (E e : array) {
			if (!matcher.apply(e)) {
				return false;
			}
		}
		return true;
	}

	public static <E> boolean isMatchAll(Iterable<E> array, Function<E, Boolean> matcher) {
		if (array == null) {
			return false;
		}
		for (E e : array) {
			if (!matcher.apply(e)) {
				return false;
			}
		}
		return true;
	}

	public static String toArrayString(@Nonnull Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof long[]) {
			return Arrays.toString((long[]) obj);
		} else if (obj instanceof int[]) {
			return Arrays.toString((int[]) obj);
		} else if (obj instanceof short[]) {
			return Arrays.toString((short[]) obj);
		} else if (obj instanceof char[]) {
			return Arrays.toString((char[]) obj);
		} else if (obj instanceof byte[]) {
			return Arrays.toString((byte[]) obj);
		} else if (obj instanceof boolean[]) {
			return Arrays.toString((boolean[]) obj);
		} else if (obj instanceof float[]) {
			return Arrays.toString((float[]) obj);
		} else if (obj instanceof double[]) {
			return Arrays.toString((double[]) obj);
		} else if (obj.getClass().isArray()) {
			try {
				return Arrays.deepToString((Object[]) obj);
			} catch (Exception ignore) {
			}
		}
		return obj.toString();
	}
}
