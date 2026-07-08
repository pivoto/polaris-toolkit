package io.polaris.core.tuple;

import io.polaris.core.lang.Copyable;
import io.polaris.core.lang.Objs;
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
public class Tuple5<T1, T2, T3, T4, T5> implements Serializable, Tuple, Copyable<Tuple5<T1, T2, T3, T4, T5>> {
	private static final long serialVersionUID = 1L;
	private T1 first;
	private T2 second;
	private T3 third;
	private T4 fourth;
	private T5 fifth;

	public static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> of(T1 first, T2 second, T3 third, T4 fourth, T5 fifth) {
		return new Tuple5<>(first, second, third, fourth, fifth);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tuple5)) return false;
		Tuple5<?, ?, ?, ?, ?> tuple5 = (Tuple5<?, ?, ?, ?, ?>) o;
		return Objects.equals(first, tuple5.first) && Objects.equals(second, tuple5.second) && Objects.equals(third, tuple5.third) && Objects.equals(fourth, tuple5.fourth) && Objects.equals(fifth, tuple5.fifth);
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second, third, fourth, fifth);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Tuple5{");
		sb.append("first=").append(first);
		sb.append(", second=").append(second);
		sb.append(", third=").append(third);
		sb.append(", fourth=").append(fourth);
		sb.append(", fifth=").append(fifth);
		sb.append('}');
		return sb.toString();
	}

	@Override
	public Tuple5<T1, T2, T3, T4, T5> copy() {
		return new Tuple5<>(Objs.cloneIfPossible(first), Objs.cloneIfPossible(second), Objs.cloneIfPossible(third), Objs.cloneIfPossible(fourth), Objs.cloneIfPossible(fifth));
	}
}
