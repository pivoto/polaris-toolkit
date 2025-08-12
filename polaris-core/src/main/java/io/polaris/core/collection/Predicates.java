package io.polaris.core.collection;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nullable;

/**
 * @author Qt
 * @since Aug 12, 2025
 */
public class Predicates {


	public static <T> Predicate<T> alwaysTrue() {
		return e -> true;
	}

	public static <T> Predicate<T> alwaysFalse() {
		return e -> false;
	}

//	@SafeVarargs
//	public static <T> Predicate<T> and(Predicate<T>... predicates) {
//		return Stream.of(predicates).reduce(java.util.function.Predicate::and)
//			.orElseGet(Predicates::alwaysTrue);
//	}
//
//	@SafeVarargs
//	public static <T> Predicate<T> or(Predicate<T>... predicates) {
//		return Stream.of(predicates).reduce(Predicate::or)
//			.orElse(e -> true);
//	}

	@SafeVarargs
	public static <T> Predicate<T> and(Predicate<T>... predicates) {
		return new AndPredicate<>(Arrays.asList(predicates));
	}


	@SafeVarargs
	public static <T> Predicate<T> or(Predicate<T>... predicates) {
		return new OrPredicate<>(Arrays.asList(predicates));
	}

	public static <T> Predicate<T> and(Iterable<? extends Predicate<T>> predicates) {
		return new AndPredicate<>(predicates);
	}


	public static <T> Predicate<T> or(Iterable<? extends Predicate<T>> predicates) {
		return new OrPredicate<>(predicates);
	}

	public static <T> Predicate<T> and(Stream<Predicate<T>> predicates) {
		return predicates.reduce(Predicate::and).orElse(e -> true);
	}

	public static <T> Predicate<T> or(Stream<Predicate<T>> predicates) {
		return predicates.reduce(Predicate::or).orElse(e -> true);
	}

	private static class NotPredicate<T> implements Predicate<T> {

		final Predicate<T> predicate;

		NotPredicate(Predicate<T> predicate) {
			this.predicate = predicate;
		}

		@Override
		public boolean test(@Nullable T t) {
			return !predicate.test(t);
		}
	}

	private static class AndPredicate<T> implements Predicate<T> {
		private final Iterable<? extends Predicate<T>> predicates;

		private AndPredicate(Iterable<? extends Predicate<T>> predicates) {
			this.predicates = predicates;
		}

		@Override
		public boolean test(@Nullable T t) {
			for (Predicate<T> predicate : predicates) {
				if (!predicate.test(t)) {
					return false;
				}
			}
			return true;
		}
	}

	private static class OrPredicate<T> implements Predicate<T> {
		private final Iterable<? extends Predicate<T>> predicates;

		private OrPredicate(Iterable<? extends Predicate<T>> predicates) {
			this.predicates = predicates;
		}

		@Override
		public boolean test(@Nullable T t) {
			for (Predicate<T> predicate : predicates) {
				if (predicate.test(t)) {
					return true;
				}
			}
			return false;
		}
	}
}
