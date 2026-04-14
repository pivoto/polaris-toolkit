package io.polaris.core.tuple;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Qt
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tuple7)) return false;
		Tuple7<?, ?, ?, ?, ?, ?, ?> tuple7 = (Tuple7<?, ?, ?, ?, ?, ?, ?>) o;
		return Objects.equals(first, tuple7.first) && Objects.equals(second, tuple7.second) && Objects.equals(third, tuple7.third) && Objects.equals(fourth, tuple7.fourth) && Objects.equals(fifth, tuple7.fifth) && Objects.equals(sixth, tuple7.sixth) && Objects.equals(seventh, tuple7.seventh);
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second, third, fourth, fifth, sixth, seventh);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Tuple7{");
		sb.append("first=").append(first);
		sb.append(", second=").append(second);
		sb.append(", third=").append(third);
		sb.append(", fourth=").append(fourth);
		sb.append(", fifth=").append(fifth);
		sb.append(", sixth=").append(sixth);
		sb.append(", seventh=").append(seventh);
		sb.append('}');
		return sb.toString();
	}
}
