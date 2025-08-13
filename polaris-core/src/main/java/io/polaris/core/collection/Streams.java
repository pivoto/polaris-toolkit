package io.polaris.core.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
 * 流处理工具类，提供对集合、数组和Stream的各种转换和过滤操作。
 *
 * @author Qts
 * @since Nov 06, 2024
 */
public class Streams {

	/**
	 * 将Iterable转换为Stream。
	 *
	 * @param <T> Iterable中元素的类型
	 * @param iterable 要转换的Iterable对象，可以为null
	 * @return 转换后的Stream，如果iterable为null则返回空Stream
	 */
	public static <T> Stream<T> stream(Iterable<T> iterable) {
		if (iterable == null) {
			return Stream.empty();
		}
		if (iterable instanceof Collection) {
			return ((Collection<T>) iterable).stream();
		}
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	/**
	 * 将Collection转换为Stream。
	 *
	 * @param <T> Collection中元素的类型
	 * @param collection 要转换的Collection对象，可以为null
	 * @return 转换后的Stream，如果collection为null则返回空Stream
	 */
	public static <T> Stream<T> stream(Collection<T> collection) {
		if (collection == null) {
			return Stream.empty();
		}
		return collection.stream();
	}

	/**
	 * 将数组转换为Stream。
	 *
	 * @param <T> 数组中元素的类型
	 * @param array 要转换的数组，可以为null
	 * @return 转换后的Stream，如果array为null则返回空Stream
	 */
	public static <T> Stream<T> stream(T[] array) {
		if (array == null) {
			return Stream.empty();
		}
		return Arrays.stream(array);
	}


	/**
	 * 从Stream中过滤出满足条件的元素并收集到List中。
	 *
	 * @param <T> 元素类型
	 * @param from 源Stream，可以为null
	 * @param predicate 过滤条件，用于判断元素是否应该被保留
	 * @return 包含满足条件元素的List，如果from为null则返回空List
	 */
	public static <T> List<T> filterList(Stream<T> from, Predicate<T> predicate) {
		if (from == null) {
			return new ArrayList<>();
		}
		return from.filter(predicate).collect(Collectors.toList());
	}

	/**
	 * 从Iterable中过滤出满足条件的元素并收集到List中。
	 *
	 * @param <T> 元素类型
	 * @param from 源Iterable，如果为空则返回空List
	 * @param predicate 过滤条件，用于判断元素是否应该被保留
	 * @return 包含满足条件元素的List，如果from为空则返回空List
	 */
	public static <T> List<T> filterList(Iterable<T> from, Predicate<T> predicate) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return filterList(stream(from), predicate);
	}

	/**
	 * 从数组中过滤出满足条件的元素并收集到List中。
	 *
	 * @param <T> 元素类型
	 * @param from 源数组，如果为空则返回空List
	 * @param predicate 过滤条件，用于判断元素是否应该被保留
	 * @return 包含满足条件元素的List，如果from为空则返回空List
	 */
	public static <T> List<T> filterList(T[] from, Predicate<T> predicate) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return filterList(stream(from), predicate);
	}

	/**
	 * 从Collection中过滤出满足条件的元素并收集到List中。
	 *
	 * @param <T> 元素类型
	 * @param from 源Collection，如果为空则返回空List
	 * @param predicate 过滤条件，用于判断元素是否应该被保留
	 * @return 包含满足条件元素的List，如果from为空则返回空List
	 */
	public static <T> List<T> filterList(Collection<T> from, Predicate<T> predicate) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return filterList(stream(from), predicate);
	}


	/**
	 * 从Stream中过滤出满足条件的元素并收集到LinkedHashSet中。
	 *
	 * @param <T> 元素类型
	 * @param from 源Stream，如果为null则返回空LinkedHashSet
	 * @param predicate 过滤条件，用于判断元素是否应该被保留
	 * @return 包含满足条件元素的LinkedHashSet，如果from为null则返回空LinkedHashSet
	 */
	public static <T> Set<T> filterSet(Stream<T> from, Predicate<T> predicate) {
		if (from == null) {
			return new LinkedHashSet<>();
		}
		return from.filter(predicate).collect(LinkedHashSet::new, Set::add, Set::addAll);
	}

	/**
	 * 从Iterable中过滤出满足条件的元素并收集到LinkedHashSet中。
	 *
	 * @param <T> 元素类型
	 * @param from 源Iterable，如果为空则返回空LinkedHashSet
	 * @param predicate 过滤条件，用于判断元素是否应该被保留
	 * @return 包含满足条件元素的LinkedHashSet，如果from为空则返回空LinkedHashSet
	 */
	public static <T> Set<T> filterSet(Iterable<T> from, Predicate<T> predicate) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashSet<>();
		}
		return filterSet(stream(from), predicate);
	}

	/**
	 * 从数组中过滤出满足条件的元素并收集到LinkedHashSet中。
	 *
	 * @param <T> 元素类型
	 * @param from 源数组，如果为空则返回空LinkedHashSet
	 * @param predicate 过滤条件，用于判断元素是否应该被保留
	 * @return 包含满足条件元素的LinkedHashSet，如果from为空则返回空LinkedHashSet
	 */
	public static <T> Set<T> filterSet(T[] from, Predicate<T> predicate) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashSet<>();
		}
		return filterSet(stream(from), predicate);
	}

	/**
	 * 从Collection中过滤出满足条件的元素并收集到LinkedHashSet中。
	 *
	 * @param <T> 元素类型
	 * @param from 源Collection，如果为空则返回空LinkedHashSet
	 * @param predicate 过滤条件，用于判断元素是否应该被保留
	 * @return 包含满足条件元素的LinkedHashSet，如果from为空则返回空LinkedHashSet
	 */
	public static <T> Set<T> filterSet(Collection<T> from, Predicate<T> predicate) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashSet<>();
		}
		return filterSet(stream(from), predicate);
	}


	/**
	 * 将Stream中的元素进行类型转换并收集到List中，过滤掉转换后为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 目标元素类型
	 * @param from 源Stream，如果为null则返回空List
	 * @param func 转换函数，用于将源类型转换为目标类型
	 * @return 包含转换后元素的List，不包含null元素，如果from为null则返回空List
	 */
	public static <T, U> List<U> convertList(Stream<T> from, Function<T, U> func) {
		if (from == null) {
			return new ArrayList<>();
		}
		return from.map(func)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	/**
	 * 将Iterable中的元素进行类型转换并收集到List中，过滤掉转换后为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 目标元素类型
	 * @param from 源Iterable，如果为空则返回空List
	 * @param func 转换函数，用于将源类型转换为目标类型
	 * @return 包含转换后元素的List，不包含null元素，如果from为空则返回空List
	 */
	public static <T, U> List<U> convertList(Iterable<T> from, Function<T, U> func) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertList(stream(from), func);
	}

	/**
	 * 将数组中的元素进行类型转换并收集到List中，过滤掉转换后为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 目标元素类型
	 * @param from 源数组，如果为空则返回空List
	 * @param func 转换函数，用于将源类型转换为目标类型
	 * @return 包含转换后元素的List，不包含null元素，如果from为空则返回空List
	 */
	public static <T, U> List<U> convertList(T[] from, Function<T, U> func) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertList(stream(from), func);
	}

	/**
	 * 将Collection中的元素进行类型转换并收集到List中，过滤掉转换后为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 目标元素类型
	 * @param from 源Collection，如果为空则返回空List
	 * @param func 转换函数，用于将源类型转换为目标类型
	 * @return 包含转换后元素的List，不包含null元素，如果from为空则返回空List
	 */
	public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertList(stream(from), func);
	}


	/**
	 * 将Stream中的元素先进行过滤，然后进行类型转换并收集到List中，过滤掉转换后为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 目标元素类型
	 * @param from 源Stream，如果为null则返回空List
	 * @param func 转换函数，用于将源类型转换为目标类型
	 * @param filter 过滤条件，用于判断元素是否应该被处理
	 * @return 包含过滤和转换后元素的List，不包含null元素，如果from为null则返回空List
	 */
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

	/**
	 * 将Iterable中的元素先进行过滤，然后进行类型转换并收集到List中，过滤掉转换后为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 目标元素类型
	 * @param from 源Iterable，如果为空则返回空List
	 * @param func 转换函数，用于将源类型转换为目标类型
	 * @param filter 过滤条件，用于判断元素是否应该被处理
	 * @return 包含过滤和转换后元素的List，不包含null元素，如果from为空则返回空List
	 */
	public static <T, U> List<U> convertList(Iterable<T> from, Function<T, U> func, Predicate<T> filter) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertList(stream(from), func, filter);
	}

	/**
	 * 将数组中的元素先进行过滤，然后进行类型转换并收集到List中，过滤掉转换后为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 目标元素类型
	 * @param from 源数组，如果为空则返回空List
	 * @param func 转换函数，用于将源类型转换为目标类型
	 * @param filter 过滤条件，用于判断元素是否应该被处理
	 * @return 包含过滤和转换后元素的List，不包含null元素，如果from为空则返回空List
	 */
	public static <T, U> List<U> convertList(T[] from, Function<T, U> func, Predicate<T> filter) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertList(stream(from), func, filter);
	}

	/**
	 * 将Collection中的元素先进行过滤，然后进行类型转换并收集到List中，过滤掉转换后为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 目标元素类型
	 * @param from 源Collection，如果为空则返回空List
	 * @param func 转换函数，用于将源类型转换为目标类型
	 * @param filter 过滤条件，用于判断元素是否应该被处理
	 * @return 包含过滤和转换后元素的List，不包含null元素，如果from为空则返回空List
	 */
	public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertList(stream(from), func, filter);
	}


	/**
	 * 使用flatMap将Stream中的元素展开并收集到List中，过滤掉为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 展开后元素的类型
	 * @param from 源Stream，如果为null则返回空List
	 * @param func 展开函数，将每个元素转换为Stream
	 * @return 包含展开后元素的List，不包含null元素，如果from为null则返回空List
	 */
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

	/**
	 * 使用flatMap将Iterable中的元素展开并收集到List中，过滤掉为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 展开后元素的类型
	 * @param from 源Iterable，如果为空则返回空List
	 * @param func 展开函数，将每个元素转换为Stream
	 * @return 包含展开后元素的List，不包含null元素，如果from为空则返回空List
	 */
	public static <T, U> List<U> convertListByFlatMap(Iterable<T> from,
		Function<T, ? extends Stream<? extends U>> func) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertListByFlatMap(stream(from), func);
	}

	/**
	 * 使用flatMap将数组中的元素展开并收集到List中，过滤掉为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 展开后元素的类型
	 * @param from 源数组，如果为空则返回空List
	 * @param func 展开函数，将每个元素转换为Stream
	 * @return 包含展开后元素的List，不包含null元素，如果from为空则返回空List
	 */
	public static <T, U> List<U> convertListByFlatMap(T[] from,
		Function<T, ? extends Stream<? extends U>> func) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertListByFlatMap(stream(from), func);
	}

	/**
	 * 使用flatMap将Collection中的元素展开并收集到List中，过滤掉为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 展开后元素的类型
	 * @param from 源Collection，如果为空则返回空List
	 * @param func 展开函数，将每个元素转换为Stream
	 * @return 包含展开后元素的List，不包含null元素，如果from为空则返回空List
	 */
	public static <T, U> List<U> convertListByFlatMap(Collection<T> from,
		Function<T, ? extends Stream<? extends U>> func) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertListByFlatMap(stream(from), func);
	}


	/**
	 * 先将Stream中的元素进行映射，再使用flatMap展开并收集到List中，过滤掉为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 中间映射后的类型
	 * @param <R> 展开后元素的类型
	 * @param from 源Stream，如果为null则返回空List
	 * @param mapper 映射函数，将源类型转换为中间类型
	 * @param func 展开函数，将中间类型转换为Stream
	 * @return 包含映射和展开后元素的List，不包含null元素，如果from为null则返回空List
	 */
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

	/**
	 * 先将Iterable中的元素进行映射，再使用flatMap展开并收集到List中，过滤掉为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 中间映射后的类型
	 * @param <R> 展开后元素的类型
	 * @param from 源Iterable，如果为空则返回空List
	 * @param mapper 映射函数，将源类型转换为中间类型
	 * @param func 展开函数，将中间类型转换为Stream
	 * @return 包含映射和展开后元素的List，不包含null元素，如果from为空则返回空List
	 */
	public static <T, U, R> List<R> convertListByFlatMap(Iterable<T> from,
		Function<? super T, ? extends U> mapper,
		Function<U, ? extends Stream<? extends R>> func) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertListByFlatMap(stream(from), mapper, func);
	}

	/**
	 * 先将数组中的元素进行映射，再使用flatMap展开并收集到List中，过滤掉为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 中间映射后的类型
	 * @param <R> 展开后元素的类型
	 * @param from 源数组，如果为空则返回空List
	 * @param mapper 映射函数，将源类型转换为中间类型
	 * @param func 展开函数，将中间类型转换为Stream
	 * @return 包含映射和展开后元素的List，不包含null元素，如果from为空则返回空List
	 */
	public static <T, U, R> List<R> convertListByFlatMap(T[] from,
		Function<? super T, ? extends U> mapper,
		Function<U, ? extends Stream<? extends R>> func) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertListByFlatMap(stream(from), mapper, func);
	}

	/**
	 * 先将Collection中的元素进行映射，再使用flatMap展开并收集到List中，过滤掉为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 中间映射后的类型
	 * @param <R> 展开后元素的类型
	 * @param from 源Collection，如果为空则返回空List
	 * @param mapper 映射函数，将源类型转换为中间类型
	 * @param func 展开函数，将中间类型转换为Stream
	 * @return 包含映射和展开后元素的List，不包含null元素，如果from为空则返回空List
	 */
	public static <T, U, R> List<R> convertListByFlatMap(Collection<T> from,
		Function<? super T, ? extends U> mapper,
		Function<U, ? extends Stream<? extends R>> func) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return convertListByFlatMap(stream(from), mapper, func);
	}


	/**
	 * 将Map中的所有值(List类型)合并成一个List。
	 *
	 * @param <K> Map的键类型
	 * @param <V> Map的值中List的元素类型
	 * @param map 源Map，其中值为List类型
	 * @return 包含所有List中元素的List
	 */
	public static <K, V> List<V> mergeValuesFromMap(Map<K, List<V>> map) {
		return map.values()
			.stream()
			.flatMap(List::stream)
			.collect(Collectors.toList());
	}


	/**
	 * 将Stream中的元素进行类型转换并收集到LinkedHashSet中，过滤掉转换后为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 目标元素类型
	 * @param from 源Stream，如果为null则返回空LinkedHashSet
	 * @param func 转换函数，用于将源类型转换为目标类型
	 * @return 包含转换后元素的LinkedHashSet，不包含null元素，如果from为null则返回空LinkedHashSet
	 */
	public static <T, U> Set<U> convertSet(Stream<T> from, Function<T, U> func) {
		if (from == null) {
			return new LinkedHashSet<>();
		}
		return from
			.map(func)
			.filter(Objects::nonNull)
			.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	/**
	 * 将Iterable中的元素进行类型转换并收集到LinkedHashSet中，过滤掉转换后为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 目标元素类型
	 * @param from 源Iterable，如果为空则返回空LinkedHashSet
	 * @param func 转换函数，用于将源类型转换为目标类型
	 * @return 包含转换后元素的LinkedHashSet，不包含null元素，如果from为空则返回空LinkedHashSet
	 */
	public static <T, U> Set<U> convertSet(Iterable<T> from, Function<T, U> func) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashSet<>();
		}
		return convertSet(stream(from), func);
	}

	/**
	 * 将数组中的元素进行类型转换并收集到LinkedHashSet中，过滤掉转换后为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 目标元素类型
	 * @param from 源数组，如果为空则返回空LinkedHashSet
	 * @param func 转换函数，用于将源类型转换为目标类型
	 * @return 包含转换后元素的LinkedHashSet，不包含null元素，如果from为空则返回空LinkedHashSet
	 */
	public static <T, U> Set<U> convertSet(T[] from, Function<T, U> func) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashSet<>();
		}
		return convertSet(stream(from), func);
	}

	/**
	 * 将Collection中的元素进行类型转换并收集到LinkedHashSet中，过滤掉转换后为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 目标元素类型
	 * @param from 源Collection，如果为空则返回空LinkedHashSet
	 * @param func 转换函数，用于将源类型转换为目标类型
	 * @return 包含转换后元素的LinkedHashSet，不包含null元素，如果from为空则返回空LinkedHashSet
	 */
	public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashSet<>();
		}
		return convertSet(stream(from), func);
	}

	/**
	 * 将Stream中的元素收集到LinkedHashSet中。
	 *
	 * @param <T> 元素类型
	 * @param from 源Stream，如果为null则返回空LinkedHashSet
	 * @return 包含元素的LinkedHashSet，如果from为null则返回空LinkedHashSet
	 */
	public static <T> Set<T> convertSet(Stream<T> from) {
		return convertSet(from, Function.identity());
	}

	/**
	 * 将Iterable中的元素收集到LinkedHashSet中。
	 *
	 * @param <T> 元素类型
	 * @param from 源Iterable，如果为空则返回空LinkedHashSet
	 * @return 包含元素的LinkedHashSet，如果from为空则返回空LinkedHashSet
	 */
	public static <T> Set<T> convertSet(Iterable<T> from) {
		return convertSet(from, Function.identity());
	}

	/**
	 * 将数组中的元素收集到LinkedHashSet中。
	 *
	 * @param <T> 元素类型
	 * @param from 源数组，如果为空则返回空LinkedHashSet
	 * @return 包含元素的LinkedHashSet，如果from为空则返回空LinkedHashSet
	 */
	public static <T> Set<T> convertSet(T[] from) {
		return convertSet(from, Function.identity());
	}

	/**
	 * 将Collection中的元素收集到LinkedHashSet中。
	 *
	 * @param <T> 元素类型
	 * @param from 源Collection，如果为空则返回空LinkedHashSet
	 * @return 包含元素的LinkedHashSet，如果from为空则返回空LinkedHashSet
	 */
	public static <T> Set<T> convertSet(Collection<T> from) {
		return convertSet(from, Function.identity());
	}


	/**
	 * 将Stream中的元素先进行过滤，然后进行类型转换并收集到LinkedHashSet中，过滤掉转换后为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 目标元素类型
	 * @param from 源Stream，如果为null则返回空LinkedHashSet
	 * @param func 转换函数，用于将源类型转换为目标类型
	 * @param filter 过滤条件，用于判断元素是否应该被处理
	 * @return 包含过滤和转换后元素的LinkedHashSet，不包含null元素，如果from为null则返回空LinkedHashSet
	 */
	public static <T, U> Set<U> convertSet(Stream<T> from, Function<T, U> func, Predicate<T> filter) {
		if (from == null) {
			return new LinkedHashSet<>();
		}
		return from
			.filter(filter)
			.map(func)
			.filter(Objects::nonNull)
			.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	/**
	 * 将Iterable中的元素先进行过滤，然后进行类型转换并收集到LinkedHashSet中，过滤掉转换后为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 目标元素类型
	 * @param from 源Iterable，如果为空则返回空LinkedHashSet
	 * @param func 转换函数，用于将源类型转换为目标类型
	 * @param filter 过滤条件，用于判断元素是否应该被处理
	 * @return 包含过滤和转换后元素的LinkedHashSet，不包含null元素，如果from为空则返回空LinkedHashSet
	 */
	public static <T, U> Set<U> convertSet(Iterable<T> from, Function<T, U> func, Predicate<T> filter) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashSet<>();
		}
		return convertSet(stream(from), func, filter);
	}

	/**
	 * 将数组中的元素先进行过滤，然后进行类型转换并收集到LinkedHashSet中，过滤掉转换后为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 目标元素类型
	 * @param from 源数组，如果为空则返回空LinkedHashSet
	 * @param func 转换函数，用于将源类型转换为目标类型
	 * @param filter 过滤条件，用于判断元素是否应该被处理
	 * @return 包含过滤和转换后元素的LinkedHashSet，不包含null元素，如果from为空则返回空LinkedHashSet
	 */
	public static <T, U> Set<U> convertSet(T[] from, Function<T, U> func, Predicate<T> filter) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashSet<>();
		}
		return convertSet(stream(from), func, filter);
	}

	/**
	 * 将Collection中的元素先进行过滤，然后进行类型转换并收集到LinkedHashSet中，过滤掉转换后为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 目标元素类型
	 * @param from 源Collection，如果为空则返回空LinkedHashSet
	 * @param func 转换函数，用于将源类型转换为目标类型
	 * @param filter 过滤条件，用于判断元素是否应该被处理
	 * @return 包含过滤和转换后元素的LinkedHashSet，不包含null元素，如果from为空则返回空LinkedHashSet
	 */
	public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashSet<>();
		}
		return convertSet(stream(from), func, filter);
	}


	/**
	 * 根据过滤条件将Stream中的元素转换为Map。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param from 源Stream，如果为null则返回空LinkedHashMap
	 * @param filter 过滤条件，用于判断元素是否应该被包含在结果中
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @return 包含过滤后元素的LinkedHashMap，如果from为null则返回空LinkedHashMap
	 */
	public static <T, K> Map<K, T> convertMapByFilter(Stream<T> from, Predicate<T> filter, Function<T, K> keyFunc) {
		if (from == null) {
			return new LinkedHashMap<>();
		}
		return from
			.filter(filter)
			.collect(Collectors.toMap(keyFunc, v -> v, (t1, t2) -> t1, LinkedHashMap::new));
	}

	/**
	 * 根据过滤条件将Iterable中的元素转换为Map。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param from 源Iterable，如果为空则返回空LinkedHashMap
	 * @param filter 过滤条件，用于判断元素是否应该被包含在结果中
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @return 包含过滤后元素的LinkedHashMap，如果from为空则返回空LinkedHashMap
	 */
	public static <T, K> Map<K, T> convertMapByFilter(Iterable<T> from, Predicate<T> filter, Function<T, K> keyFunc) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashMap<>();
		}
		return convertMapByFilter(stream(from), filter, keyFunc);
	}

	/**
	 * 根据过滤条件将数组中的元素转换为Map。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param from 源数组，如果为空则返回空LinkedHashMap
	 * @param filter 过滤条件，用于判断元素是否应该被包含在结果中
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @return 包含过滤后元素的LinkedHashMap，如果from为空则返回空LinkedHashMap
	 */
	public static <T, K> Map<K, T> convertMapByFilter(T[] from, Predicate<T> filter, Function<T, K> keyFunc) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashMap<>();
		}
		return convertMapByFilter(stream(from), filter, keyFunc);
	}

	/**
	 * 根据过滤条件将Collection中的元素转换为Map。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param from 源Collection，如果为空则返回空LinkedHashMap
	 * @param filter 过滤条件，用于判断元素是否应该被包含在结果中
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @return 包含过滤后元素的LinkedHashMap，如果from为空则返回空LinkedHashMap
	 */
	public static <T, K> Map<K, T> convertMapByFilter(Collection<T> from, Predicate<T> filter, Function<T, K> keyFunc) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashMap<>();
		}
		return convertMapByFilter(stream(from), filter, keyFunc);
	}


	/**
	 * 使用flatMap将Stream中的元素展开并收集到LinkedHashSet中，过滤掉为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 展开后元素的类型
	 * @param from 源Stream，如果为null则返回空LinkedHashSet
	 * @param func 展开函数，将每个元素转换为Stream
	 * @return 包含展开后元素的LinkedHashSet，不包含null元素，如果from为null则返回空LinkedHashSet
	 */
	public static <T, U> Set<U> convertSetByFlatMap(Stream<T> from,
		Function<T, ? extends Stream<? extends U>> func) {
		if (from == null) {
			return new LinkedHashSet<>();
		}
		return from
			.filter(Objects::nonNull)
			.flatMap(func)
			.filter(Objects::nonNull)
			.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	/**
	 * 使用flatMap将Iterable中的元素展开并收集到LinkedHashSet中，过滤掉为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 展开后元素的类型
	 * @param from 源Iterable，如果为空则返回空LinkedHashSet
	 * @param func 展开函数，将每个元素转换为Stream
	 * @return 包含展开后元素的LinkedHashSet，不包含null元素，如果from为空则返回空LinkedHashSet
	 */
	public static <T, U> Set<U> convertSetByFlatMap(Iterable<T> from,
		Function<T, ? extends Stream<? extends U>> func) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashSet<>();
		}
		return convertSetByFlatMap(stream(from), func);
	}

	/**
	 * 使用flatMap将数组中的元素展开并收集到LinkedHashSet中，过滤掉为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 展开后元素的类型
	 * @param from 源数组，如果为空则返回空LinkedHashSet
	 * @param func 展开函数，将每个元素转换为Stream
	 * @return 包含展开后元素的LinkedHashSet，不包含null元素，如果from为空则返回空LinkedHashSet
	 */
	public static <T, U> Set<U> convertSetByFlatMap(T[] from,
		Function<T, ? extends Stream<? extends U>> func) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashSet<>();
		}
		return convertSetByFlatMap(stream(from), func);
	}

	/**
	 * 使用flatMap将Collection中的元素展开并收集到LinkedHashSet中，过滤掉为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 展开后元素的类型
	 * @param from 源Collection，如果为空则返回空LinkedHashSet
	 * @param func 展开函数，将每个元素转换为Stream
	 * @return 包含展开后元素的LinkedHashSet，不包含null元素，如果from为空则返回空LinkedHashSet
	 */
	public static <T, U> Set<U> convertSetByFlatMap(Collection<T> from,
		Function<T, ? extends Stream<? extends U>> func) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashSet<>();
		}
		return convertSetByFlatMap(stream(from), func);
	}


	/**
	 * 先将Stream中的元素进行映射，再使用flatMap展开并收集到LinkedHashSet中，过滤掉为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 中间映射后的类型
	 * @param <R> 展开后元素的类型
	 * @param from 源Stream，如果为null则返回空LinkedHashSet
	 * @param mapper 映射函数，将源类型转换为中间类型
	 * @param func 展开函数，将中间类型转换为Stream
	 * @return 包含映射和展开后元素的LinkedHashSet，不包含null元素，如果from为null则返回空LinkedHashSet
	 */
	public static <T, U, R> Set<R> convertSetByFlatMap(Stream<T> from,
		Function<? super T, ? extends U> mapper,
		Function<U, ? extends Stream<? extends R>> func) {
		if (from == null) {
			return new LinkedHashSet<>();
		}
		return from
			.map(mapper)
			.filter(Objects::nonNull)
			.flatMap(func)
			.filter(Objects::nonNull)
			.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	/**
	 * 先将Iterable中的元素进行映射，再使用flatMap展开并收集到LinkedHashSet中，过滤掉为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 中间映射后的类型
	 * @param <R> 展开后元素的类型
	 * @param from 源Iterable，如果为空则返回空LinkedHashSet
	 * @param mapper 映射函数，将源类型转换为中间类型
	 * @param func 展开函数，将中间类型转换为Stream
	 * @return 包含映射和展开后元素的LinkedHashSet，不包含null元素，如果from为空则返回空LinkedHashSet
	 */
	public static <T, U, R> Set<R> convertSetByFlatMap(Iterable<T> from,
		Function<? super T, ? extends U> mapper,
		Function<U, ? extends Stream<? extends R>> func) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashSet<>();
		}
		return convertSetByFlatMap(stream(from), mapper, func);
	}

	/**
	 * 先将数组中的元素进行映射，再使用flatMap展开并收集到LinkedHashSet中，过滤掉为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 中间映射后的类型
	 * @param <R> 展开后元素的类型
	 * @param from 源数组，如果为空则返回空LinkedHashSet
	 * @param mapper 映射函数，将源类型转换为中间类型
	 * @param func 展开函数，将中间类型转换为Stream
	 * @return 包含映射和展开后元素的LinkedHashSet，不包含null元素，如果from为空则返回空LinkedHashSet
	 */
	public static <T, U, R> Set<R> convertSetByFlatMap(T[] from,
		Function<? super T, ? extends U> mapper,
		Function<U, ? extends Stream<? extends R>> func) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashSet<>();
		}
		return convertSetByFlatMap(stream(from), mapper, func);
	}

	/**
	 * 先将Collection中的元素进行映射，再使用flatMap展开并收集到LinkedHashSet中，过滤掉为null的元素。
	 *
	 * @param <T> 源元素类型
	 * @param <U> 中间映射后的类型
	 * @param <R> 展开后元素的类型
	 * @param from 源Collection，如果为空则返回空LinkedHashSet
	 * @param mapper 映射函数，将源类型转换为中间类型
	 * @param func 展开函数，将中间类型转换为Stream
	 * @return 包含映射和展开后元素的LinkedHashSet，不包含null元素，如果from为空则返回空LinkedHashSet
	 */
	public static <T, U, R> Set<R> convertSetByFlatMap(Collection<T> from,
		Function<? super T, ? extends U> mapper,
		Function<U, ? extends Stream<? extends R>> func) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashSet<>();
		}
		return convertSetByFlatMap(stream(from), mapper, func);
	}


	/**
	 * 将Stream中的元素转换为Map，使用指定的键映射函数和默认的值映射函数(Function.identity())。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param from 源Stream，如果为null则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @return 包含转换后元素的LinkedHashMap，如果from为null则返回空LinkedHashMap
	 */
	public static <T, K> Map<K, T> convertMap(Stream<T> from, Function<T, K> keyFunc) {
		return convertMap(from, keyFunc, Function.identity());
	}

	/**
	 * 将Iterable中的元素转换为Map，使用指定的键映射函数和默认的值映射函数(Function.identity())。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param from 源Iterable，如果为空则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @return 包含转换后元素的LinkedHashMap，如果from为空则返回空LinkedHashMap
	 */
	public static <T, K> Map<K, T> convertMap(Iterable<T> from, Function<T, K> keyFunc) {
		return convertMap(from, keyFunc, Function.identity());
	}

	/**
	 * 将数组中的元素转换为Map，使用指定的键映射函数和默认的值映射函数(Function.identity())。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param from 源数组，如果为空则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @return 包含转换后元素的LinkedHashMap，如果from为空则返回空LinkedHashMap
	 */
	public static <T, K> Map<K, T> convertMap(T[] from, Function<T, K> keyFunc) {
		return convertMap(from, keyFunc, Function.identity());
	}

	/**
	 * 将Collection中的元素转换为Map，使用指定的键映射函数和默认的值映射函数(Function.identity())。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param from 源Collection，如果为空则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @return 包含转换后元素的LinkedHashMap，如果from为空则返回空LinkedHashMap
	 */
	public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc) {
		return convertMap(from, keyFunc, Function.identity());
	}


	/**
	 * 将Stream中的元素转换为Map，使用指定的键映射函数和值映射函数。
	 * 当出现键冲突时，使用(v1, v2) -> v1作为合并函数。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源Stream，如果为null则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @return 包含转换后元素的LinkedHashMap，如果from为null则返回空LinkedHashMap
	 */
	public static <T, K, V> Map<K, V> convertMap(Stream<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1);
	}

	/**
	 * 将Iterable中的元素转换为Map，使用指定的键映射函数和值映射函数。
	 * 当出现键冲突时，使用(v1, v2) -> v1作为合并函数。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源Iterable，如果为空则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @return 包含转换后元素的LinkedHashMap，如果from为空则返回空LinkedHashMap
	 */
	public static <T, K, V> Map<K, V> convertMap(Iterable<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1);
	}

	/**
	 * 将数组中的元素转换为Map，使用指定的键映射函数和值映射函数。
	 * 当出现键冲突时，使用(v1, v2) -> v1作为合并函数。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源数组，如果为空则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @return 包含转换后元素的LinkedHashMap，如果from为空则返回空LinkedHashMap
	 */
	public static <T, K, V> Map<K, V> convertMap(T[] from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1);
	}

	/**
	 * 将Collection中的元素转换为Map，使用指定的键映射函数和值映射函数。
	 * 当出现键冲突时，使用(v1, v2) -> v1作为合并函数。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源Collection，如果为空则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @return 包含转换后元素的LinkedHashMap，如果from为空则返回空LinkedHashMap
	 */
	public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1);
	}


	/**
	 * 将Stream中的元素转换为Map，使用指定的键映射函数和默认的值映射函数(Function.identity())，并指定Map的实现类型。
	 * 当出现键冲突时，使用(v1, v2) -> v1作为合并函数。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param from 源Stream，如果为null则返回由supplier创建的空Map
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param supplier Map供应商，用于创建Map实例
	 * @return 包含转换后元素的Map，如果from为null则返回由supplier创建的空Map
	 */
	public static <T, K> Map<K, T> convertMap(Stream<T> from, Function<T, K> keyFunc, Supplier<? extends Map<K, T>> supplier) {
		return convertMap(from, keyFunc, Function.identity(), supplier);
	}

	/**
	 * 将Iterable中的元素转换为Map，使用指定的键映射函数和默认的值映射函数(Function.identity())，并指定Map的实现类型。
	 * 当出现键冲突时，使用(v1, v2) -> v1作为合并函数。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param from 源Iterable，如果为空则返回由supplier创建的空Map
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param supplier Map供应商，用于创建Map实例
	 * @return 包含转换后元素的Map，如果from为空则返回由supplier创建的空Map
	 */
	public static <T, K> Map<K, T> convertMap(Iterable<T> from, Function<T, K> keyFunc, Supplier<? extends Map<K, T>> supplier) {
		return convertMap(from, keyFunc, Function.identity(), supplier);
	}

	/**
	 * 将数组中的元素转换为Map，使用指定的键映射函数和默认的值映射函数(Function.identity())，并指定Map的实现类型。
	 * 当出现键冲突时，使用(v1, v2) -> v1作为合并函数。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param from 源数组，如果为空则返回由supplier创建的空Map
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param supplier Map供应商，用于创建Map实例
	 * @return 包含转换后元素的Map，如果from为空则返回由supplier创建的空Map
	 */
	public static <T, K> Map<K, T> convertMap(T[] from, Function<T, K> keyFunc, Supplier<? extends Map<K, T>> supplier) {
		return convertMap(from, keyFunc, Function.identity(), supplier);
	}

	/**
	 * 将Collection中的元素转换为Map，使用指定的键映射函数和默认的值映射函数(Function.identity())，并指定Map的实现类型。
	 * 当出现键冲突时，使用(v1, v2) -> v1作为合并函数。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param from 源Collection，如果为空则返回由supplier创建的空Map
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param supplier Map供应商，用于创建Map实例
	 * @return 包含转换后元素的Map，如果from为空则返回由supplier创建的空Map
	 */
	public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc, Supplier<? extends Map<K, T>> supplier) {
		return convertMap(from, keyFunc, Function.identity(), supplier);
	}


	/**
	 * 将Stream中的元素转换为Map，使用指定的键映射函数、值映射函数和合并函数。
	 * 默认使用LinkedHashMap作为Map的实现类型。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源Stream，如果为null则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @param mergeFunction 合并函数，用于处理键冲突的情况
	 * @return 包含转换后元素的LinkedHashMap，如果from为null则返回空LinkedHashMap
	 */
	public static <T, K, V> Map<K, V> convertMap(Stream<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction) {
		return convertMap(from, keyFunc, valueFunc, mergeFunction, LinkedHashMap::new);
	}

	/**
	 * 将Iterable中的元素转换为Map，使用指定的键映射函数、值映射函数和合并函数。
	 * 默认使用LinkedHashMap作为Map的实现类型。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源Iterable，如果为空则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @param mergeFunction 合并函数，用于处理键冲突的情况
	 * @return 包含转换后元素的LinkedHashMap，如果from为空则返回空LinkedHashMap
	 */
	public static <T, K, V> Map<K, V> convertMap(Iterable<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction) {
		return convertMap(from, keyFunc, valueFunc, mergeFunction, LinkedHashMap::new);
	}

	/**
	 * 将数组中的元素转换为Map，使用指定的键映射函数、值映射函数和合并函数。
	 * 默认使用LinkedHashMap作为Map的实现类型。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源数组，如果为空则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @param mergeFunction 合并函数，用于处理键冲突的情况
	 * @return 包含转换后元素的LinkedHashMap，如果from为空则返回空LinkedHashMap
	 */
	public static <T, K, V> Map<K, V> convertMap(T[] from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction) {
		return convertMap(from, keyFunc, valueFunc, mergeFunction, LinkedHashMap::new);
	}

	/**
	 * 将Collection中的元素转换为Map，使用指定的键映射函数、值映射函数和合并函数。
	 * 默认使用LinkedHashMap作为Map的实现类型。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源Collection，如果为空则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @param mergeFunction 合并函数，用于处理键冲突的情况
	 * @return 包含转换后元素的LinkedHashMap，如果from为空则返回空LinkedHashMap
	 */
	public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction) {
		return convertMap(from, keyFunc, valueFunc, mergeFunction, LinkedHashMap::new);
	}


	/**
	 * 将Stream中的元素转换为Map，使用指定的键映射函数、值映射函数和Map供应商。
	 * 当出现键冲突时，使用(v1, v2) -> v1作为合并函数。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源Stream，如果为null则返回由supplier创建的空Map
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @param supplier Map供应商，用于创建Map实例
	 * @return 包含转换后元素的Map，如果from为null则返回由supplier创建的空Map
	 */
	public static <T, K, V> Map<K, V> convertMap(Stream<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, Supplier<? extends Map<K, V>> supplier) {
		return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1, supplier);
	}

	/**
	 * 将Iterable中的元素转换为Map，使用指定的键映射函数、值映射函数和Map供应商。
	 * 当出现键冲突时，使用(v1, v2) -> v1作为合并函数。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源Iterable，如果为空则返回由supplier创建的空Map
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @param supplier Map供应商，用于创建Map实例
	 * @return 包含转换后元素的Map，如果from为空则返回由supplier创建的空Map
	 */
	public static <T, K, V> Map<K, V> convertMap(Iterable<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, Supplier<? extends Map<K, V>> supplier) {
		return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1, supplier);
	}

	/**
	 * 将数组中的元素转换为Map，使用指定的键映射函数、值映射函数和Map供应商。
	 * 当出现键冲突时，使用(v1, v2) -> v1作为合并函数。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源数组，如果为空则返回由supplier创建的空Map
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @param supplier Map供应商，用于创建Map实例
	 * @return 包含转换后元素的Map，如果from为空则返回由supplier创建的空Map
	 */
	public static <T, K, V> Map<K, V> convertMap(T[] from, Function<T, K> keyFunc, Function<T, V> valueFunc, Supplier<? extends Map<K, V>> supplier) {
		return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1, supplier);
	}

	/**
	 * 将Collection中的元素转换为Map，使用指定的键映射函数、值映射函数和Map供应商。
	 * 当出现键冲突时，使用(v1, v2) -> v1作为合并函数。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源Collection，如果为空则返回由supplier创建的空Map
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @param supplier Map供应商，用于创建Map实例
	 * @return 包含转换后元素的Map，如果from为空则返回由supplier创建的空Map
	 */
	public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, Supplier<? extends Map<K, V>> supplier) {
		return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1, supplier);
	}


	/**
	 * 将Stream中的元素转换为Map，使用指定的键映射函数、值映射函数、合并函数和Map供应商。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源Stream，如果为null则返回由supplier创建的空Map
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @param mergeFunction 合并函数，用于处理键冲突的情况
	 * @param supplier Map供应商，用于创建Map实例
	 * @return 包含转换后元素的Map，如果from为null则返回由supplier创建的空Map
	 */
	public static <T, K, V> Map<K, V> convertMap(Stream<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction, Supplier<? extends Map<K, V>> supplier) {
		if (from == null) {
			return supplier.get();
		}
		return from
			.collect(Collectors.toMap(keyFunc, valueFunc, mergeFunction, supplier));
	}

	/**
	 * 将Iterable中的元素转换为Map，使用指定的键映射函数、值映射函数、合并函数和Map供应商。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源Iterable，如果为空则返回由supplier创建的空Map
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @param mergeFunction 合并函数，用于处理键冲突的情况
	 * @param supplier Map供应商，用于创建Map实例
	 * @return 包含转换后元素的Map，如果from为空则返回由supplier创建的空Map
	 */
	public static <T, K, V> Map<K, V> convertMap(Iterable<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction, Supplier<? extends Map<K, V>> supplier) {
		if (Iterables.isEmpty(from)) {
			return supplier.get();
		}
		return convertMap(stream(from), keyFunc, valueFunc, mergeFunction, supplier);
	}

	/**
	 * 将数组中的元素转换为Map，使用指定的键映射函数、值映射函数、合并函数和Map供应商。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源数组，如果为空则返回由supplier创建的空Map
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @param mergeFunction 合并函数，用于处理键冲突的情况
	 * @param supplier Map供应商，用于创建Map实例
	 * @return 包含转换后元素的Map，如果from为空则返回由supplier创建的空Map
	 */
	public static <T, K, V> Map<K, V> convertMap(T[] from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction, Supplier<? extends Map<K, V>> supplier) {
		if (Iterables.isEmpty(from)) {
			return supplier.get();
		}
		return convertMap(stream(from), keyFunc, valueFunc, mergeFunction, supplier);
	}

	/**
	 * 将Collection中的元素转换为Map，使用指定的键映射函数、值映射函数、合并函数和Map供应商。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源Collection，如果为空则返回由supplier创建的空Map
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @param mergeFunction 合并函数，用于处理键冲突的情况
	 * @param supplier Map供应商，用于创建Map实例
	 * @return 包含转换后元素的Map，如果from为空则返回由supplier创建的空Map
	 */
	public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction, Supplier<? extends Map<K, V>> supplier) {
		if (Iterables.isEmpty(from)) {
			return supplier.get();
		}
		return convertMap(stream(from), keyFunc, valueFunc, mergeFunction, supplier);
	}


	/**
	 * 将Stream中的元素按指定键函数分组，生成一个MultiMap（一个键对应多个值的Map）。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param from 源Stream，如果为null则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @return 包含分组后元素的LinkedHashMap，键为分组依据，值为该组的所有元素列表
	 */
	public static <T, K> Map<K, List<T>> convertMultiMap(Stream<T> from, Function<T, K> keyFunc) {
		if (from == null) {
			return new LinkedHashMap<>();
		}
		return from
			.collect(
				Collectors.groupingBy(keyFunc, Collectors.mapping(t -> t, Collectors.toList()))
			);
	}

	/**
	 * 将Iterable中的元素按指定键函数分组，生成一个MultiMap（一个键对应多个值的Map）。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param from 源Iterable，如果为空则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @return 包含分组后元素的LinkedHashMap，键为分组依据，值为该组的所有元素列表
	 */
	public static <T, K> Map<K, List<T>> convertMultiMap(Iterable<T> from, Function<T, K> keyFunc) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashMap<>();
		}
		return convertMultiMap(stream(from), keyFunc);
	}

	/**
	 * 将数组中的元素按指定键函数分组，生成一个MultiMap（一个键对应多个值的Map）。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param from 源数组，如果为空则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @return 包含分组后元素的LinkedHashMap，键为分组依据，值为该组的所有元素列表
	 */
	public static <T, K> Map<K, List<T>> convertMultiMap(T[] from, Function<T, K> keyFunc) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashMap<>();
		}
		return convertMultiMap(stream(from), keyFunc);
	}

	/**
	 * 将Collection中的元素按指定键函数分组，生成一个MultiMap（一个键对应多个值的Map）。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param from 源Collection，如果为空则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @return 包含分组后元素的LinkedHashMap，键为分组依据，值为该组的所有元素列表
	 */
	public static <T, K> Map<K, List<T>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashMap<>();
		}
		return convertMultiMap(stream(from), keyFunc);
	}


	/**
	 * 将Stream中的元素按指定键函数分组，并对值进行转换，生成一个MultiMap（一个键对应多个值的Map）。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源Stream，如果为null则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @return 包含分组和转换后元素的LinkedHashMap，键为分组依据，值为该组的所有元素列表
	 */
	public static <T, K, V> Map<K, List<V>> convertMultiMap(Stream<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		if (from == null) {
			return new LinkedHashMap<>();
		}
		return from
			.collect(
				Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toList()))
			);
	}

	/**
	 * 将Iterable中的元素按指定键函数分组，并对值进行转换，生成一个MultiMap（一个键对应多个值的Map）。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源Iterable，如果为空则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @return 包含分组和转换后元素的LinkedHashMap，键为分组依据，值为该组的所有元素列表
	 */
	public static <T, K, V> Map<K, List<V>> convertMultiMap(Iterable<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashMap<>();
		}
		return convertMultiMap(stream(from), keyFunc, valueFunc);
	}

	/**
	 * 将数组中的元素按指定键函数分组，并对值进行转换，生成一个MultiMap（一个键对应多个值的Map）。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源数组，如果为空则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @return 包含分组和转换后元素的LinkedHashMap，键为分组依据，值为该组的所有元素列表
	 */
	public static <T, K, V> Map<K, List<V>> convertMultiMap(T[] from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashMap<>();
		}
		return convertMultiMap(stream(from), keyFunc, valueFunc);
	}

	/**
	 * 将Collection中的元素按指定键函数分组，并对值进行转换，生成一个MultiMap（一个键对应多个值的Map）。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源Collection，如果为空则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @return 包含分组和转换后元素的LinkedHashMap，键为分组依据，值为该组的所有元素列表
	 */
	public static <T, K, V> Map<K, List<V>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashMap<>();
		}
		return convertMultiMap(stream(from), keyFunc, valueFunc);
	}


	/**
	 * 将Stream中的元素按指定键函数分组，并对值进行转换，生成一个MultiMap（一个键对应多个值的Map），值为LinkedHashSet类型。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源Stream，如果为null则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @return 包含分组和转换后元素的LinkedHashMap，键为分组依据，值为该组的所有元素集合（LinkedHashSet）
	 */
	public static <T, K, V> Map<K, Set<V>> convertMultiSetMap(Stream<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		if (from == null) {
			return new LinkedHashMap<>();
		}
		return from.collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toCollection(LinkedHashSet::new))));
	}

	/**
	 * 将Iterable中的元素按指定键函数分组，并对值进行转换，生成一个MultiMap（一个键对应多个值的Map），值为LinkedHashSet类型。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源Iterable，如果为空则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @return 包含分组和转换后元素的LinkedHashMap，键为分组依据，值为该组的所有元素集合（LinkedHashSet）
	 */
	public static <T, K, V> Map<K, Set<V>> convertMultiSetMap(Iterable<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashMap<>();
		}
		return convertMultiSetMap(stream(from), keyFunc, valueFunc);
	}

	/**
	 * 将数组中的元素按指定键函数分组，并对值进行转换，生成一个MultiMap（一个键对应多个值的Map），值为LinkedHashSet类型。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源数组，如果为空则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @return 包含分组和转换后元素的LinkedHashMap，键为分组依据，值为该组的所有元素集合（LinkedHashSet）
	 */
	public static <T, K, V> Map<K, Set<V>> convertMultiSetMap(T[] from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashMap<>();
		}
		return convertMultiSetMap(stream(from), keyFunc, valueFunc);
	}

	/**
	 * 将Collection中的元素按指定键函数分组，并对值进行转换，生成一个MultiMap（一个键对应多个值的Map），值为LinkedHashSet类型。
	 *
	 * @param <T> 元素类型
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param from 源Collection，如果为空则返回空LinkedHashMap
	 * @param keyFunc 键映射函数，用于从元素生成键
	 * @param valueFunc 值映射函数，用于从元素生成值
	 * @return 包含分组和转换后元素的LinkedHashMap，键为分组依据，值为该组的所有元素集合（LinkedHashSet）
	 */
	public static <T, K, V> Map<K, Set<V>> convertMultiSetMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
		if (Iterables.isEmpty(from)) {
			return new LinkedHashMap<>();
		}
		return convertMultiSetMap(stream(from), keyFunc, valueFunc);
	}


	/**
	 * 根据指定的键映射函数对Stream中的元素进行去重操作。
	 * 当出现重复键时，使用(t1, t2) -> t1作为合并函数。
	 *
	 * @param <T> 元素类型
	 * @param <R> 键的类型
	 * @param from 源Stream，如果为null则返回空ArrayList
	 * @param keyMapper 键映射函数，用于生成元素的键
	 * @return 去重后的元素列表
	 */
	public static <T, R> List<T> distinct(Stream<T> from, Function<T, R> keyMapper) {
		if (from == null) {
			return new ArrayList<>();
		}
		return distinct(from, keyMapper, (t1, t2) -> t1);
	}

	/**
	 * 根据指定的键映射函数对Iterable中的元素进行去重操作。
	 * 当出现重复键时，使用(t1, t2) -> t1作为合并函数。
	 *
	 * @param <T> 元素类型
	 * @param <R> 键的类型
	 * @param from 源Iterable，如果为空则返回空ArrayList
	 * @param keyMapper 键映射函数，用于生成元素的键
	 * @return 去重后的元素列表
	 */
	public static <T, R> List<T> distinct(Iterable<T> from, Function<T, R> keyMapper) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return distinct(from, keyMapper, (t1, t2) -> t1);
	}

	/**
	 * 根据指定的键映射函数对数组中的元素进行去重操作。
	 * 当出现重复键时，使用(t1, t2) -> t1作为合并函数。
	 *
	 * @param <T> 元素类型
	 * @param <R> 键的类型
	 * @param from 源数组，如果为空则返回空ArrayList
	 * @param keyMapper 键映射函数，用于生成元素的键
	 * @return 去重后的元素列表
	 */
	public static <T, R> List<T> distinct(T[] from, Function<T, R> keyMapper) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return distinct(from, keyMapper, (t1, t2) -> t1);
	}

	/**
	 * 根据指定的键映射函数对Collection中的元素进行去重操作。
	 * 当出现重复键时，使用(t1, t2) -> t1作为合并函数。
	 *
	 * @param <T> 元素类型
	 * @param <R> 键的类型
	 * @param from 源Collection，如果为空则返回空ArrayList
	 * @param keyMapper 键映射函数，用于生成元素的键
	 * @return 去重后的元素列表
	 */
	public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return distinct(from, keyMapper, (t1, t2) -> t1);
	}


	/**
	 * 根据指定的键映射函数对Stream中的元素进行去重操作，使用指定的合并函数处理重复键。
	 *
	 * @param <T> 元素类型
	 * @param <R> 键的类型
	 * @param from 源Stream，如果为null则返回空ArrayList
	 * @param keyMapper 键映射函数，用于生成元素的键
	 * @param cover 合并函数，用于处理键冲突的情况
	 * @return 去重后的元素列表
	 */
	public static <T, R> List<T> distinct(Stream<T> from, Function<T, R> keyMapper, BinaryOperator<T> cover) {
		if (from == null) {
			return new ArrayList<>();
		}
		return new ArrayList<>(convertMap(from, keyMapper, Function.identity(), cover).values());
	}

	/**
	 * 根据指定的键映射函数对Iterable中的元素进行去重操作，使用指定的合并函数处理重复键。
	 *
	 * @param <T> 元素类型
	 * @param <R> 键的类型
	 * @param from 源Iterable，如果为空则返回空ArrayList
	 * @param keyMapper 键映射函数，用于生成元素的键
	 * @param cover 合并函数，用于处理键冲突的情况
	 * @return 去重后的元素列表
	 */
	public static <T, R> List<T> distinct(Iterable<T> from, Function<T, R> keyMapper, BinaryOperator<T> cover) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return new ArrayList<>(convertMap(from, keyMapper, Function.identity(), cover).values());
	}

	/**
	 * 根据指定的键映射函数对数组中的元素进行去重操作，使用指定的合并函数处理重复键。
	 *
	 * @param <T> 元素类型
	 * @param <R> 键的类型
	 * @param from 源数组，如果为空则返回空ArrayList
	 * @param keyMapper 键映射函数，用于生成元素的键
	 * @param cover 合并函数，用于处理键冲突的情况
	 * @return 去重后的元素列表
	 */
	public static <T, R> List<T> distinct(T[] from, Function<T, R> keyMapper, BinaryOperator<T> cover) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return new ArrayList<>(convertMap(from, keyMapper, Function.identity(), cover).values());
	}

	/**
	 * 根据指定的键映射函数对Collection中的元素进行去重操作，使用指定的合并函数处理重复键。
	 *
	 * @param <T> 元素类型
	 * @param <R> 键的类型
	 * @param from 源Collection，如果为空则返回空ArrayList
	 * @param keyMapper 键映射函数，用于生成元素的键
	 * @param cover 合并函数，用于处理键冲突的情况
	 * @return 去重后的元素列表
	 */
	public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper, BinaryOperator<T> cover) {
		if (Iterables.isEmpty(from)) {
			return new ArrayList<>();
		}
		return new ArrayList<>(convertMap(from, keyMapper, Function.identity(), cover).values());
	}


}
