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
public class Tuple1<T1> implements Tuple, Serializable {

	private static final long serialVersionUID = 1L;
	private T1 first;

	public static <T1> Tuple1<T1> of(T1 first) {
		return new Tuple1<>(first);
	}
}
