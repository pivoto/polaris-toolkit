package io.polaris.core.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Qt
 * @since Nov 06, 2024
 */
public class Streams {

	public static <T> Stream<T> stream(Iterable<T> iterable) {
		if (iterable == null) {
			return Stream.empty();
		}
		if (iterable instanceof Collection) {
			return ((Collection<T>) iterable).stream();
		}
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	public static <T> Stream<T> stream(Collection<T> collection) {
		if (collection == null) {
			return Stream.empty();
		}
		return collection.stream();
	}

	public static <T> Stream<T> stream(T[] array) {
		if (array == null) {
			return Stream.empty();
		}
		return Arrays.stream(array);
	}


	public static <T> List<T> filterList(Stream<T> from, Predicate<T> predicate) {
		if (from == null) {
			return new ArrayList<>();
		}
		return from.filter(predicate).collect(Collectors.toList());
	}

	public static <T> List<T> filterList(Iterable<T> from, Predicate<T> predicate) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return filterList(stream(from), predicate);
	}

	public static <T> List<T> filterList(T[] from, Predicate<T> predicate) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return filterList(stream(from), predicate);
	}

	public static <T> List<T> filterList(Collection<T> from, Predicate<T> predicate) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return filterList(stream(from), predicate);
	}


	public static <T, U> List<U> convertList(Stream<T> from, Function<T, U> func) {
		if (from == null) {
			return new ArrayList<>();
		}
		return from.map(func)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	public static <T, U> List<U> convertList(Iterable<T> from, Function<T, U> func) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertList(stream(from), func);
	}

	public static <T, U> List<U> convertList(T[] from, Function<T, U> func) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertList(stream(from), func);
	}

	public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertList(stream(from), func);
	}


	public static <T, U> List<U> convertList(Stream<T> from, Function<T, U> func, Predicate<T> filter) {
		if (from == null) {
			return new ArrayList<>();
		}
		return from
			.filter(filter)
			.map(func)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	public static <T, U> List<U> convertList(Iterable<T> from, Function<T, U> func, Predicate<T> filter) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertList(stream(from), func, filter);
	}

	public static <T, U> List<U> convertList(T[] from, Function<T, U> func, Predicate<T> filter) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertList(stream(from), func, filter);
	}

	public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertList(stream(from), func, filter);
	}


	public static <T, U> List<U> convertListByFlatMap(Stream<T> from,
		Function<T, ? extends Stream<? extends U>> func) {
		if (from == null) {
			return new ArrayList<>();
		}
		return from
			.filter(Objects::nonNull)
			.flatMap(func)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	public static <T, U> List<U> convertListByFlatMap(Iterable<T> from,
		Function<T, ? extends Stream<? extends U>> func) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertListByFlatMap(stream(from), func);
	}

	public static <T, U> List<U> convertListByFlatMap(T[] from,
		Function<T, ? extends Stream<? extends U>> func) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertListByFlatMap(stream(from), func);
	}

	public static <T, U> List<U> convertListByFlatMap(Collection<T> from,
		Function<T, ? extends Stream<? extends U>> func) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertListByFlatMap(stream(from), func);
	}


	public static <T, U, R> List<R> convertListByFlatMap(Stream<T> from,
		Function<? super T, ? extends U> mapper,
		Function<U, ? extends Stream<? extends R>> func) {
		if (from == null) {
			return new ArrayList<>();
		}
		return from
			.map(mapper)
			.filter(Objects::nonNull)
			.flatMap(func)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	public static <T, U, R> List<R> convertListByFlatMap(Iterable<T> from,
		Function<? super T, ? extends U> mapper,
		Function<U, ? extends Stream<? extends R>> func) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertListByFlatMap(stream(from), mapper, func);
	}

	public static <T, U, R> List<R> convertListByFlatMap(T[] from,
		Function<? super T, ? extends U> mapper,
		Function<U, ? extends Stream<? extends R>> func) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertListByFlatMap(stream(from), mapper, func);
	}

	public static <T, U, R> List<R> convertListByFlatMap(Collection<T> from,
		Function<? super T, ? extends U> mapper,
		Function<U, ? extends Stream<? extends R>> func) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertListByFlatMap(stream(from), mapper, func);
	}


	public static <K, V> List<V> mergeValuesFromMap(Map<K, List<V>> map) {
		return map.values()
			.stream()
			.flatMap(List::stream)
			.collect(Collectors.toList());
	}


	public static <T, U> Set<U> convertSet(Stream<T> from, Function<T, U> func) {
		if (from == null) {
			return new HashSet<>();
		}
		return from
			.map(func)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
	}

	public static <T, U> Set<U> convertSet(Iterable<T> from, Function<T, U> func) {
		if (Iterables.isEmpty(from)) {
			return new HashSet<>();
		}
		return convertSet(stream(from), func);
	}

	public static <T, U> Set<U> convertSet(T[] from, Function<T, U> func) {
		if (Iterables.isEmpty(from)) {
			return new HashSet<>();
		}
		return convertSet(stream(from), func);
	}

	public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func) {
		if (Iterables.isEmpty(from)) {
			return new HashSet<>();
		}
		return convertSet(stream(from), func);
	}

	public static <T> Set<T> convertSet(Stream<T> from) {
		return convertSet(from, Function.identity());
	}

	public static <T> Set<T> convertSet(Iterable<T> from) {
		return convertSet(from, Function.identity());
	}

	public static <T> Set<T> convertSet(T[] from) {
		return convertSet(from, Function.identity());
	}

	public static <T> Set<T> convertSet(Collection<T> from) {
		return convertSet(from, Function.identity());
	}


	public static <T, U> Set<U> convertSet(Stream<T> from, Function<T, U> func, Predicate<T> filter) {
		if (from == null) {
			return new HashSet<>();
		}
		return from
			.filter(filter)
			.map(func)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
	}

	public static <T, U> Set<U> convertSet(Iterable<T> from, Function<T, U> func, Predicate<T> filter) {
		if (Iterables.isEmpty(from)) {
			return new HashSet<>();
		}
		return convertSet(stream(from), func, filter);
	}

	public static <T, U> Set<U> convertSet(T[] from, Function<T, U> func, Predicate<T> filter) {
		if (Iterables.isEmpty(from)) {
			return new HashSet<>();
		}
		return convertSet(stream(from), func, filter);
	}

	public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
		if (Iterables.isEmpty(from)) {
			return new HashSet<>();
		}
		return convertSet(stream(from), func, filter);
	}


	public static <T, K> Map<K, T> convertMapByFilter(Stream<T> from, Predicate<T> filter, Function<T, K> keyFunc) {
		if (from == null) {
			return new HashMap<>();
		}
		return from
			.filter(filter)
			.collect(Collectors.toMap(keyFunc, v -> v));
	}

	public static <T, K> Map<K, T> convertMapByFilter(Iterable<T> from, Predicate<T> filter, Function<T, K> keyFunc) {
		if (Iterables.isEmpty(from)) {
			return new HashMap<>();
		}
		return convertMapByFilter(stream(from), filter, keyFunc);
	}

	public static <T, K> Map<K, T> convertMapByFilter(T[] from, Predicate<T> filter, Function<T, K> keyFunc) {
		if (Iterables.isEmpty(from)) {
			return new HashMap<>();
		}
		return convertMapByFilter(stream(from), filter, keyFunc);
	}

	public static <T, K> Map<K, T> convertMapByFilter(Collection<T> from, Predicate<T> filter, Function<T, K> keyFunc) {
		if (Iterables.isEmpty(from)) {
			return new HashMap<>();
		}
		return convertMapByFilter(stream(from), filter, keyFunc);
	}


	public static <T, U> Set<U> convertSetByFlatMap(Stream<T> from,
		Function<T, ? extends Stream<? extends U>> func) {
		if (from == null) {
			return new HashSet<>();
		}
		return from
			.filter(Objects::nonNull)
			.flatMap(func)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
	}

	public static <T, U> Set<U> convertSetByFlatMap(Iterable<T> from,
		Function<T, ? extends Stream<? extends U>> func) {
		if (Iterables.isEmpty(from)) {
			return new HashSet<>();
		}
		return convertSetByFlatMap(stream(from), func);
	}

	public static <T, U> Set<U> convertSetByFlatMap(T[] from,
		Function<T, ? extends Stream<? extends U>> func) {
		if (Iterables.isEmpty(from)) {
			return new HashSet<>();
		}
		return convertSetByFlatMap(stream(from), func);
	}

	public static <T, U> Set<U> convertSetByFlatMap(Collection<T> from,
		Function<T, ? extends Stream<? extends U>> func) {
		if (Iterables.isEmpty(from)) {
			return new HashSet<>();
		}
		return convertSetByFlatMap(stream(from), func);
	}


	public static <T, U, R> Set<R> convertSetByFlatMap(Stream<T> from,
		Function<? super T, ? extends U> mapper,
		Function<U, ? extends Stream<? extends R>> func) {
		if (from == null) {
			return new HashSet<>();
		}
		return from
			.map(mapper)
			.filter(Objects::nonNull)
			.flatMap(func)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
	}

	public static <T, U, R> Set<R> convertSetByFlatMap(Iterable<T> from,
		Function<? super T, ? extends U> mapper,
		Function<U, ? extends Stream<? extends R>> func) {
		if (Iterables.isEmpty(from)) {
			return new HashSet<>();
		}
		return convertSetByFlatMap(stream(from), mapper, func);
	}

	public static <T, U, R> Set<R> convertSetByFlatMap(T[] from,
		Function<? super T, ? extends U> mapper,
		Function<U, ? extends Stream<? extends R>> func) {
		if (Iterables.isEmpty(from)) {
			return new HashSet<>();
		}
		return convertSetByFlatMap(stream(from), mapper, func);
	}

	public static <T, U, R> Set<R> convertSetByFlatMap(Collection<T> from,
		Function<? super T, ? extends U> mapper,
		Function<U, ? extends Stream<? extends R>> func) {
		if (Iterables.isEmpty(from)) {
			return new HashSet<>();
		}
		return convertSetByFlatMap(stream(from), mapper, func);
	}


	public static <T, K> Map<K, T> convertMap(Stream<T> from, Function<T, K> keyFunc) {
		return convertMap(from, keyFunc, Function.identity());
	}

	public static <T, K> Map<K, T> convertMap(Iterable<T> from, Function<T, K> keyFunc) {
		return convertMap(from, keyFunc, Function.identity());
	}

	public static <T, K> Map<K, T> convertMap(T[] from, Function<T, K> keyFunc) {
		return convertMap(from, keyFunc, Function.identity());
	}

	public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc) {
		return convertMap(from, keyFunc, Function.identity());
	}


	public static <T, K, V> Map<K, V> convertMap(Stream<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1);
	}

	public static <T, K, V> Map<K, V> convertMap(Iterable<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1);
	}

	public static <T, K, V> Map<K, V> convertMap(T[] from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1);
	}

	public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1);
	}


	public static <T, K> Map<K, T> convertMap(Stream<T> from, Function<T, K> keyFunc, Supplier<? extends Map<K, T>> supplier) {
		return convertMap(from, keyFunc, Function.identity(), supplier);
	}

	public static <T, K> Map<K, T> convertMap(Iterable<T> from, Function<T, K> keyFunc, Supplier<? extends Map<K, T>> supplier) {
		return convertMap(from, keyFunc, Function.identity(), supplier);
	}

	public static <T, K> Map<K, T> convertMap(T[] from, Function<T, K> keyFunc, Supplier<? extends Map<K, T>> supplier) {
		return convertMap(from, keyFunc, Function.identity(), supplier);
	}

	public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc, Supplier<? extends Map<K, T>> supplier) {
		return convertMap(from, keyFunc, Function.identity(), supplier);
	}


	public static <T, K, V> Map<K, V> convertMap(Stream<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction) {
		return convertMap(from, keyFunc, valueFunc, mergeFunction, HashMap::new);
	}

	public static <T, K, V> Map<K, V> convertMap(Iterable<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction) {
		return convertMap(from, keyFunc, valueFunc, mergeFunction, HashMap::new);
	}

	public static <T, K, V> Map<K, V> convertMap(T[] from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction) {
		return convertMap(from, keyFunc, valueFunc, mergeFunction, HashMap::new);
	}

	public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction) {
		return convertMap(from, keyFunc, valueFunc, mergeFunction, HashMap::new);
	}


	public static <T, K, V> Map<K, V> convertMap(Stream<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, Supplier<? extends Map<K, V>> supplier) {
		return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1, supplier);
	}

	public static <T, K, V> Map<K, V> convertMap(Iterable<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, Supplier<? extends Map<K, V>> supplier) {
		return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1, supplier);
	}

	public static <T, K, V> Map<K, V> convertMap(T[] from, Function<T, K> keyFunc, Function<T, V> valueFunc, Supplier<? extends Map<K, V>> supplier) {
		return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1, supplier);
	}

	public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, Supplier<? extends Map<K, V>> supplier) {
		return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1, supplier);
	}


	public static <T, K, V> Map<K, V> convertMap(Stream<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction, Supplier<? extends Map<K, V>> supplier) {
		if (from == null) {
			return supplier.get();
		}
		return from
			.collect(Collectors.toMap(keyFunc, valueFunc, mergeFunction, supplier));
	}

	public static <T, K, V> Map<K, V> convertMap(Iterable<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction, Supplier<? extends Map<K, V>> supplier) {
		if (Iterables.isEmpty(from)) {
			return supplier.get();
		}
		return convertMap(stream(from), keyFunc, valueFunc, mergeFunction, supplier);
	}

	public static <T, K, V> Map<K, V> convertMap(T[] from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction, Supplier<? extends Map<K, V>> supplier) {
		if (Iterables.isEmpty(from)) {
			return supplier.get();
		}
		return convertMap(stream(from), keyFunc, valueFunc, mergeFunction, supplier);
	}

	public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction, Supplier<? extends Map<K, V>> supplier) {
		if (Iterables.isEmpty(from)) {
			return supplier.get();
		}
		return convertMap(stream(from), keyFunc, valueFunc, mergeFunction, supplier);
	}


	public static <T, K> Map<K, List<T>> convertMultiMap(Stream<T> from, Function<T, K> keyFunc) {
		if (from == null) {
			return new HashMap<>();
		}
		return from
			.collect(
				Collectors.groupingBy(keyFunc, Collectors.mapping(t -> t, Collectors.toList()))
			);
	}

	public static <T, K> Map<K, List<T>> convertMultiMap(Iterable<T> from, Function<T, K> keyFunc) {
		if (Iterables.isEmpty(from)) {
			return new HashMap<>();
		}
		return convertMultiMap(stream(from), keyFunc);
	}

	public static <T, K> Map<K, List<T>> convertMultiMap(T[] from, Function<T, K> keyFunc) {
		if (Iterables.isEmpty(from)) {
			return new HashMap<>();
		}
		return convertMultiMap(stream(from), keyFunc);
	}

	public static <T, K> Map<K, List<T>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc) {
		if (Iterables.isEmpty(from)) {
			return new HashMap<>();
		}
		return convertMultiMap(stream(from), keyFunc);
	}


	public static <T, K, V> Map<K, List<V>> convertMultiMap(Stream<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		if (from == null) {
			return new HashMap<>();
		}
		return from
			.collect(
				Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toList()))
			);
	}

	public static <T, K, V> Map<K, List<V>> convertMultiMap(Iterable<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		if (Iterables.isEmpty(from)) {
			return new HashMap<>();
		}
		return convertMultiMap(stream(from), keyFunc, valueFunc);
	}

	public static <T, K, V> Map<K, List<V>> convertMultiMap(T[] from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		if (Iterables.isEmpty(from)) {
			return new HashMap<>();
		}
		return convertMultiMap(stream(from), keyFunc, valueFunc);
	}

	public static <T, K, V> Map<K, List<V>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		if (Iterables.isEmpty(from)) {
			return new HashMap<>();
		}
		return convertMultiMap(stream(from), keyFunc, valueFunc);
	}


	public static <T, K, V> Map<K, Set<V>> convertMultiSetMap(Stream<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		if (from == null) {
			return new HashMap<>();
		}
		return from.collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toSet())));
	}

	public static <T, K, V> Map<K, Set<V>> convertMultiSetMap(Iterable<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		if (Iterables.isEmpty(from)) {
			return new HashMap<>();
		}
		return convertMultiSetMap(stream(from), keyFunc, valueFunc);
	}

	public static <T, K, V> Map<K, Set<V>> convertMultiSetMap(T[] from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		if (Iterables.isEmpty(from)) {
			return new HashMap<>();
		}
		return convertMultiSetMap(stream(from), keyFunc, valueFunc);
	}

	public static <T, K, V> Map<K, Set<V>> convertMultiSetMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		if (Iterables.isEmpty(from)) {
			return new HashMap<>();
		}
		return convertMultiSetMap(stream(from), keyFunc, valueFunc);
	}


	public static <T, R> List<T> distinct(Stream<T> from, Function<T, R> keyMapper) {
		if (from == null) {
			return new ArrayList<>();
		}
		return distinct(from, keyMapper, (t1, t2) -> t1);
	}

	public static <T, R> List<T> distinct(Iterable<T> from, Function<T, R> keyMapper) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return distinct(from, keyMapper, (t1, t2) -> t1);
	}

	public static <T, R> List<T> distinct(T[] from, Function<T, R> keyMapper) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return distinct(from, keyMapper, (t1, t2) -> t1);
	}

	public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return distinct(from, keyMapper, (t1, t2) -> t1);
	}


	public static <T, R> List<T> distinct(Stream<T> from, Function<T, R> keyMapper, BinaryOperator<T> cover) {
		if (from == null) {
			return new ArrayList<>();
		}
		return new ArrayList<>(convertMap(from, keyMapper, Function.identity(), cover).values());
	}

	public static <T, R> List<T> distinct(Iterable<T> from, Function<T, R> keyMapper, BinaryOperator<T> cover) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return new ArrayList<>(convertMap(from, keyMapper, Function.identity(), cover).values());
	}

	public static <T, R> List<T> distinct(T[] from, Function<T, R> keyMapper, BinaryOperator<T> cover) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return new ArrayList<>(convertMap(from, keyMapper, Function.identity(), cover).values());
	}

	public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper, BinaryOperator<T> cover) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return new ArrayList<>(convertMap(from, keyMapper, Function.identity(), cover).values());
	}


}
