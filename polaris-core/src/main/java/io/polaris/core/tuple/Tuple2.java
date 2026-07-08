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
public class Tuple2<T1, T2> implements Serializable, Tuple, Copyable<Tuple2<T1, T2>> {
	private static final long serialVersionUID = 1L;
	private T1 first;
	private T2 second;

	public static <T1, T2> Tuple2<T1, T2> of(T1 first, T2 second) {
		return new Tuple2<>(first, second);
	}


	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tuple2)) return false;
		Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;
		return Objects.equals(first, tuple2.first) && Objects.equals(second, tuple2.second);
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Tuple2{");
		sb.append("first=").append(first);
		sb.append(", second=").append(second);
		sb.append('}');
		return sb.toString();
	}

	@Override
	public Tuple2<T1, T2> copy() {
		return new Tuple2<>(Objs.cloneIfPossible(first), Objs.cloneIfPossible(second));
	}
}
