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
public class Tuple7<T1, T2, T3, T4, T5, T6, T7> implements Serializable, Tuple {
	private static final long serialVersionUID = 1L;
	private T1 first;
	private T2 second;
	private T3 third;
	private T4 fourth;
	private T5 fifth;
	private T6 sixth;
	private T7 seventh;

	public static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> of(T1 first, T2 second, T3 third, T4 fourth, T5 fifth, T6 sixth, T7 seventh) {
		return new Tuple7<>(first, second, third, fourth, fifth, sixth, seventh);
	}
}
