package io.polaris.core.tuple;

import java.io.Serializable;

import io.polaris.core.lang.Objs;
import lombok.NoArgsConstructor;

/**
 * @author Qt
 */
@NoArgsConstructor
public class Triple<A, B, C> extends Tuple3<A, B, C> implements Serializable {
	private static final long serialVersionUID = 1L;

	public Triple(final A first, final B second, final C third) {
		super(first, second, third);
	}


	public static <T1, T2, T3> Triple<T1, T2, T3> of(T1 first, T2 second, T3 third) {
		return new Triple<>(first, second, third);
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public Tuple3<A, B, C> copy() {
		return new Triple<>(Objs.cloneIfPossible(getFirst()), Objs.cloneIfPossible(getSecond()), Objs.cloneIfPossible(getThird()));
	}

}
