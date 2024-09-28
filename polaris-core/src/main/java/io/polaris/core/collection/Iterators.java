package io.polaris.core.collection;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;

import javax.annotation.Nullable;

/**
 * @author Qt
 */
public class Iterators {

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


	public static <E> @Nullable E getNext(Iterator<? extends E> iterator) {
		return getNext(iterator, null);
	}

	public static <E> @Nullable E getNext(Iterator<? extends E> iterator, @Nullable E defaultValue) {
		return iterator.hasNext() ? iterator.next() : defaultValue;
	}

	public static <E> E getLast(Iterator<E> iterator) {
		while (true) {
			E current = iterator.next();
			if (!iterator.hasNext()) {
				return current;
			}
		}
	}

	public static <E> @Nullable E getLast(Iterator<? extends E> iterator, @Nullable E defaultValue) {
		return iterator.hasNext() ? getLast(iterator) : defaultValue;
	}

	public static <E> @Nullable E get(Iterator<? extends E> iterator, int position) {
		return get(iterator, position, null);
	}

	public static <E> @Nullable E get(Iterator<? extends E> iterator, int position, @Nullable E defaultValue) {
		skip(iterator, position);
		return getNext(iterator, defaultValue);
	}

	public static int skip(Iterator<?> iterator, int num) {
		int i;
		for (i = 0; i < num && iterator.hasNext(); i++) {
			iterator.next();
		}
		return i;
	}
}
