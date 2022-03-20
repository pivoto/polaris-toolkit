package io.polaris.toolkit.spring.core.tuple;

/**
 * @author Qt
 * @version Mar 04, 2022
 * @since 1.8
 */
public class Tuple<A, B> {
	private final A first;
	private final B second;

	public Tuple(A first, B second) {
		this.first = first;
		this.second = second;
	}

	public static <A, B> Tuple<A, B> of(A first, B second) {
		return new Tuple<A, B>(first, second);
	}

	public A getFirst() {
		return first;
	}

	public B getSecond() {
		return second;
	}
}
