package io.polaris.core.tuple;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Qt
 * @since 1.8
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Triple<A, B, C> extends Tuple3<A, B, C> implements Serializable {
	private static final long serialVersionUID = 1L;

	public Triple(final A first, final B second, final C third) {
		super(first, second, third);
	}

	public static <T1, T2, T3> Triple<T1, T2, T3> of(T1 first, T2 second, T3 third) {
		return new Triple<>(first, second, third);
	}
}
