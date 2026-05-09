package io.polaris.core.tuple;

import lombok.Getter;

/**
 * @author Qt
 * @since May 09, 2026
 */
public class InitializedValue<V> {
	@Getter
	private final boolean initialized;
	@Getter
	private final V value;

	public InitializedValue(boolean initialized, V value) {
		this.initialized = initialized;
		this.value = value;
	}

	public static <V> InitializedValue<V> of(boolean initialized, V value) {
		return new InitializedValue<>(initialized, value);
	}

	public static <V> InitializedValue<V> initialized(V value) {
		return new InitializedValue<>(true, value);
	}

	public static <V> InitializedValue<V> uninitialized() {
		return new InitializedValue<>(false, null);
	}

}
