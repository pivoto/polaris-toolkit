package io.polaris.core.tuple;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Qt
 * @since 1.8
 */
public class TupleN implements Serializable, Tuple {

	private static final long serialVersionUID = 1L;
	private final Object[] array;

	public TupleN(Object... array) {
		this.array = array;
	}

	public static TupleN of(Object... array) {
		return new TupleN(array);
	}

	public void set(int idx, Object val) {
		this.array[idx] = val;
	}

	public <E> E get(int idx) {
		return (E) this.array[idx];
	}

	public int size() {
		return this.array.length;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TupleN tupleN = (TupleN) o;
		return Arrays.equals(array, tupleN.array);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(array);
	}
}
