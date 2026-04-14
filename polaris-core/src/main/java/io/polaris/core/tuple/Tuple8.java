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
public class Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> implements Serializable, Tuple {
	private static final long serialVersionUID = 1L;
	private T1 first;
	private T2 second;
	private T3 third;
	private T4 fourth;
	private T5 fifth;
	private T6 sixth;
	private T7 seventh;
	private T8 eighth;

	public static <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> of(T1 first, T2 second, T3 third, T4 fourth, T5 fifth, T6 sixth, T7 seventh, T8 eighth) {
		return new Tuple8<>(first, second, third, fourth, fifth, sixth, seventh, eighth);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tuple8)) return false;
		Tuple8<?, ?, ?, ?, ?, ?, ?, ?> tuple8 = (Tuple8<?, ?, ?, ?, ?, ?, ?, ?>) o;
		return Objects.equals(first, tuple8.first) && Objects.equals(second, tuple8.second) && Objects.equals(third, tuple8.third) && Objects.equals(fourth, tuple8.fourth) && Objects.equals(fifth, tuple8.fifth) && Objects.equals(sixth, tuple8.sixth) && Objects.equals(seventh, tuple8.seventh) && Objects.equals(eighth, tuple8.eighth);
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second, third, fourth, fifth, sixth, seventh, eighth);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Tuple8{");
		sb.append("first=").append(first);
		sb.append(", second=").append(second);
		sb.append(", third=").append(third);
		sb.append(", fourth=").append(fourth);
		sb.append(", fifth=").append(fifth);
		sb.append(", sixth=").append(sixth);
		sb.append(", seventh=").append(seventh);
		sb.append(", eighth=").append(eighth);
		sb.append('}');
		return sb.toString();
	}
}
