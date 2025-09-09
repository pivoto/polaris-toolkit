package io.polaris.core.tuple;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
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
}
