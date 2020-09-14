package io.polaris.core.function;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author Qt
 * @since 1.8,  Sep 06, 2023
 */
@FunctionalInterface
public interface Visitable<T> {

	void visit(Consumer<? super T> visitor);

	static <E> Visitable<E> of(Iterable<E> iterable) {
		return iterable::forEach;
	}

	static <E> Visitable<E> of(Iterator<E> iterator) {
		return iterator::forEachRemaining;
	}

	static <E> Visitable<E> of(Stream<E> stream) {
		return stream::forEach;
	}

}
