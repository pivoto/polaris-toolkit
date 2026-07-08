package io.polaris.core.tuple;

import io.polaris.core.lang.Copyable;
import io.polaris.core.lang.Objs;
import lombok.ToString;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Qt
 */
public class TupleN implements Serializable, Tuple, Copyable<TupleN> {

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

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("TupleN{");
		sb.append("array=").append(Arrays.toString(array));
		sb.append('}');
		return sb.toString();
	}

	@Override
	public TupleN copy() {
		Object[] array = new Object[this.array.length];
		for (int i = 0; i < this.array.length; i++) {
			array[i] = Objs.cloneIfPossible(this.array[i]);
		}
		return new TupleN(array);
	}
}
