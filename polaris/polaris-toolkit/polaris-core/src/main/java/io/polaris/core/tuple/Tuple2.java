package io.polaris.core.tuple;

import lombok.*;

import java.io.Serializable;

/**
 * @author Qt
 * @since 1.8
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Tuple2<T1, T2> implements Serializable, Tuple {
	private static final long serialVersionUID = 1L;
	private T1 first;
	private T2 second;

	public static <T1, T2> Tuple2<T1, T2> of(T1 first, T2 second) {
		return new Tuple2<>(first, second);
	}
}
