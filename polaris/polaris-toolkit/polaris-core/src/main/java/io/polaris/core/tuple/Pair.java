package io.polaris.core.tuple;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
@Getter
@Setter
public class Pair<A, B> extends Tuple2<A, B> implements Map.Entry<A, B>, Serializable, Tuple {

	public Pair(final A first, final B second) {
		super(first, second);
	}

	public Pair() {
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
