package io.polaris.core.lang;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Qt
 * @since 1.8
 */
public class Other implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Object target;

	public Other(Object target) {
		this.target = target;
	}

	public static Other of(Object target) {
		return new Other(target);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return target != null;
		}
		if (getClass() != o.getClass()) {
			return !Objects.equals(target, o);
		}
		Other other = (Other) o;
		return Objects.equals(target, other.target);
	}

	@Override
	public int hashCode() {
		return Objects.hash(target);
	}


	@Override
	public String toString() {
		return "not " + Objects.toString(target);
	}

}
