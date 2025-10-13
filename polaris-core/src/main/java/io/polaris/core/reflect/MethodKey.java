package io.polaris.core.reflect;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Qt
 * @since Oct 13, 2025
 */
class MethodKey {
	private final String methodName;
	private final Class<?> returnTypes;
	private final Class<?>[] parameterTypes;

	public MethodKey(String methodName, Class<?> returnTypes, Class<?>[] parameterTypes) {
		this.methodName = methodName;
		this.returnTypes = returnTypes;
		this.parameterTypes = parameterTypes;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MethodKey)) return false;
		MethodKey methodKey = (MethodKey) o;
		return Objects.equals(methodName, methodKey.methodName)
			&& Objects.equals(returnTypes, methodKey.returnTypes)
			&& Objects.deepEquals(parameterTypes, methodKey.parameterTypes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(methodName, returnTypes, Arrays.hashCode(parameterTypes));
	}
}
