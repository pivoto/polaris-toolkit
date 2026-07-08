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
public class Tuple3<T1, T2, T3> implements Serializable, Tuple, Copyable<Tuple3<T1, T2, T3>> {
	private static final long serialVersionUID = 1L;
	private T1 first;
	private T2 second;
	private T3 third;

	public static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 first, T2 second, T3 third) {
		return new Tuple3<>(first, second, third);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tuple3)) return false;
		Tuple3<?, ?, ?> tuple3 = (Tuple3<?, ?, ?>) o;
		return Objects.equals(first, tuple3.first) && Objects.equals(second, tuple3.second) && Objects.equals(third, tuple3.third);
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second, third);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Tuple3{");
		sb.append("first=").append(first);
		sb.append(", second=").append(second);
		sb.append(", third=").append(third);
		sb.append('}');
		return sb.toString();
	}

	@Override
	public Tuple3<T1, T2, T3> copy() {
		return new Tuple3<>(Objs.cloneIfPossible(first), Objs.cloneIfPossible(second), Objs.cloneIfPossible(third));
	}
}
