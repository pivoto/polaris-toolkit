package io.polaris.core.tuple;

import io.polaris.core.lang.Copyable;
import io.polaris.core.lang.Objs;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Qt
 */
@Getter
@Setter
public class Pair<A, B> extends Tuple2<A, B> implements Map.Entry<A, B>, Serializable, Tuple {
	private static final long serialVersionUID = 1L;

	public Pair(final A first, final B second) {
		super(first, second);
	}

	public Pair() {
	}

	public static <T1, T2> Pair<T1, T2> of(T1 first, T2 second) {
		return new Pair<>(first, second);
	}

	@Override
	public A getKey() {
		return getFirst();
	}

	@Override
	public B getValue() {
		return getSecond();
	}

	@Override
	public B setValue(B value) {
		B old = this.getSecond();
		this.setSecond(value);
		return old;
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public Pair<A, B> copy() {
		return new Pair<>(Objs.cloneIfPossible(getFirst()), Objs.cloneIfPossible(getSecond()));
	}
}
