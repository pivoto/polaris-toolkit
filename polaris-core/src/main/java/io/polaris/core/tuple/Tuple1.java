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
public class Tuple1<T1> implements Tuple, Serializable {

	private static final long serialVersionUID = 1L;
	private T1 first;

	public static <T1> Tuple1<T1> of(T1 first) {
		return new Tuple1<>(first);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tuple1)) return false;
		Tuple1<?> tuple1 = (Tuple1<?>) o;
		return Objects.equals(first, tuple1.first);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(first);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Tuple1{");
		sb.append("first=").append(first);
		sb.append('}');
		return sb.toString();
	}
}
