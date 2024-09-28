package io.polaris.core.collection;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

/**
 * @author Qt
 * @since 1.8
 */
public class Iterables {

	public static <E> Iterator<E> iterator(Enumeration<E> enumeration) {
		return Iterators.iterator(enumeration);
	}

	public static <E> Enumeration<E> enumeration(Iterable<E> iterable) {
		return Iterators.enumeration(iterable);
	}

	public static <E> Enumeration<E> enumeration(Iterator<E> iterator) {
		return Iterators.enumeration(iterator);
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
		return Lists.asList(enumeration);
	}

	public static <E> Set<E> asSet(Enumeration<E> enumeration) {
		return Sets.asSet(enumeration);
	}

	@SafeVarargs
	public static <E> List<E> asList(E... iterable) {
		return Lists.asList(iterable);
	}

	@SafeVarargs
	public static <E> Set<E> asSet(E... iterable) {
		return Sets.asSet(iterable);
	}


	public static <E> List<E> asList(Iterable<E> iterable) {
		return Lists.asList(iterable);
	}

	public static <E> Set<E> asSet(Iterable<E> iterable) {
		return Sets.asSet(iterable);
	}

	public static <E> List<E> asList(Iterator<E> iterator) {
		return Lists.asList(iterator);
	}

	public static <E> Set<E> asSet(Iterator<E> iterator) {
		return Sets.asSet(iterator);
	}

	public static <E> E[] copyOf(E[] array) {
		return Arrays.copyOf(array, array.length);
	}

	public static <S, T> Iterator<T> convert(Iterator<S> iterator, Function<S, T> converter) {
		return Iterators.convert(iterator, converter);
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
		return Sets.convert(set, converter, reconvert);
	}


	public static <S, T> boolean isMatchAll(S[] array1, T[] array2, BiFunction<S, T, Boolean> matcher) {
		if (array1 == null && array2 == null) {
			return true;
		}
		if (array1 == null || array2 == null) {
			return false;
		}
		if (array1.length != array2.length) {
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

	public static String toArrayString(@Nullable Object obj) {
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
