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
public class Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> implements Serializable, Tuple {
	private static final long serialVersionUID = 1L;
	private T1 first;
	private T2 second;
	private T3 third;
	private T4 fourth;
	private T5 fifth;
	private T6 sixth;
	private T7 seventh;
	private T8 eighth;
	private T9 ninth;

	public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> of(T1 first, T2 second, T3 third, T4 fourth, T5 fifth, T6 sixth, T7 seventh, T8 eighth, T9 ninth) {
		return new Tuple9<>(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tuple9)) return false;
		Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?> tuple9 = (Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?>) o;
		return Objects.equals(first, tuple9.first) && Objects.equals(second, tuple9.second) && Objects.equals(third, tuple9.third) && Objects.equals(fourth, tuple9.fourth) && Objects.equals(fifth, tuple9.fifth) && Objects.equals(sixth, tuple9.sixth) && Objects.equals(seventh, tuple9.seventh) && Objects.equals(eighth, tuple9.eighth) && Objects.equals(ninth, tuple9.ninth);
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Tuple9{");
		sb.append("first=").append(first);
		sb.append(", second=").append(second);
		sb.append(", third=").append(third);
		sb.append(", fourth=").append(fourth);
		sb.append(", fifth=").append(fifth);
		sb.append(", sixth=").append(sixth);
		sb.append(", seventh=").append(seventh);
		sb.append(", eighth=").append(eighth);
		sb.append(", ninth=").append(ninth);
		sb.append('}');
		return sb.toString();
	}
}
