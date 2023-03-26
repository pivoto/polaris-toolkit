package io.polaris.core.collection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

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

	public static <E> List<E> asList(Enumeration<E> enumeration) {
		List<E> list = new ArrayList<>();
		while (enumeration.hasMoreElements()) {
			list.add(enumeration.nextElement());
		}
		return list;
	}

	public static <E> List<E> asList(Iterator<E> iterator) {
		List<E> list = new ArrayList<>();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list;
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
}
