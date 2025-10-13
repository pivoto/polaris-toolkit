package io.polaris.core.reflect;

import java.util.Objects;

/**
 * @author Qt
 * @since Oct 13, 2025
 */
class FieldKey {
	private final String fieldName;
	private final Class<?> fieldType;

	FieldKey(String fieldName, Class<?> fieldType) {
		this.fieldName = fieldName;
		this.fieldType = fieldType;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof FieldKey)) return false;
		FieldKey fieldKey = (FieldKey) o;
		return Objects.equals(fieldName, fieldKey.fieldName) && Objects.equals(fieldType, fieldKey.fieldType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fieldName, fieldType);
	}
}
