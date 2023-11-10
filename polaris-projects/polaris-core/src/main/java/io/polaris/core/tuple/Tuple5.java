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
public class Tuple5<T1, T2, T3, T4, T5> implements Serializable, Tuple {
	private static final long serialVersionUID = 1L;
	private T1 first;
	private T2 second;
	private T3 third;
	private T4 fourth;
	private T5 fifth;

	public static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> of(T1 first, T2 second, T3 third, T4 fourth, T5 fifth) {
		return new Tuple5<>(first, second, third, fourth, fifth);
	}
}
