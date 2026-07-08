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
public class Tuple4<T1, T2, T3, T4> implements Serializable, Tuple, Copyable<Tuple4<T1, T2, T3, T4>> {
	private static final long serialVersionUID = 1L;
	private T1 first;
	private T2 second;
	private T3 third;
	private T4 fourth;

	public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> of(T1 first, T2 second, T3 third, T4 fourth) {
		return new Tuple4<>(first, second, third, fourth);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tuple4)) return false;
		Tuple4<?, ?, ?, ?> tuple4 = (Tuple4<?, ?, ?, ?>) o;
		return Objects.equals(first, tuple4.first) && Objects.equals(second, tuple4.second) && Objects.equals(third, tuple4.third) && Objects.equals(fourth, tuple4.fourth);
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second, third, fourth);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Tuple4{");
		sb.append("first=").append(first);
		sb.append(", second=").append(second);
		sb.append(", third=").append(third);
		sb.append(", fourth=").append(fourth);
		sb.append('}');
		return sb.toString();
	}

	@Override
	public Tuple4<T1, T2, T3, T4> copy() {
		return new Tuple4<>(Objs.cloneIfPossible(first), Objs.cloneIfPossible(second), Objs.cloneIfPossible(third), Objs.cloneIfPossible(fourth));
	}
}
