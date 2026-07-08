package io.polaris.core.tuple;

import java.io.Serializable;
import java.util.Objects;

import io.polaris.core.lang.Copyable;
import io.polaris.core.lang.Objs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Qt
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tuple6<T1, T2, T3, T4, T5, T6> implements Serializable, Tuple, Copyable<Tuple6<T1, T2, T3, T4, T5, T6>> {
	private static final long serialVersionUID = 1L;
	private T1 first;
	private T2 second;
	private T3 third;
	private T4 fourth;
	private T5 fifth;
	private T6 sixth;

	public static <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> of(T1 first, T2 second, T3 third, T4 fourth, T5 fifth, T6 sixth) {
		return new Tuple6<>(first, second, third, fourth, fifth, sixth);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tuple6)) return false;
		Tuple6<?, ?, ?, ?, ?, ?> tuple6 = (Tuple6<?, ?, ?, ?, ?, ?>) o;
		return Objects.equals(first, tuple6.first) && Objects.equals(second, tuple6.second) && Objects.equals(third, tuple6.third) && Objects.equals(fourth, tuple6.fourth) && Objects.equals(fifth, tuple6.fifth) && Objects.equals(sixth, tuple6.sixth);
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second, third, fourth, fifth, sixth);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Tuple6{");
		sb.append("first=").append(first);
		sb.append(", second=").append(second);
		sb.append(", third=").append(third);
		sb.append(", fourth=").append(fourth);
		sb.append(", fifth=").append(fifth);
		sb.append(", sixth=").append(sixth);
		sb.append('}');
		return sb.toString();
	}

	@Override
	public Tuple6<T1, T2, T3, T4, T5, T6> copy() {
		return new Tuple6<>(Objs.cloneIfPossible(first), Objs.cloneIfPossible(second), Objs.cloneIfPossible(third), Objs.cloneIfPossible(fourth), Objs.cloneIfPossible(fifth), Objs.cloneIfPossible(sixth));
	}
}
