package io.polaris.core.reflect;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Qt
 * @since Oct 13, 2025
 */
class ConstructorKey {
	private final Class<?>[] parameterTypes;

	ConstructorKey(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ConstructorKey)) return false;
		ConstructorKey that = (ConstructorKey) o;
		return Objects.deepEquals(parameterTypes, that.parameterTypes);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(parameterTypes);
	}
}
