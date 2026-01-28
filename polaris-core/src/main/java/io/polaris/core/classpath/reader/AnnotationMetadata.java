package io.polaris.core.classpath.reader;

import java.lang.annotation.Annotation;

import javax.annotation.Nonnull;

import io.polaris.core.classpath.reader.annotation.EnumValue;
import io.polaris.core.lang.annotation.AnnotationAttributes;

/**
 * @author Qt
 * @since Jan 26, 2026
 */
public interface AnnotationMetadata {

	String dump();

	String dump(int level);

	default boolean isAnnotationType() {
		return false;
	}

	default boolean isSimpleType() {
		return false;
	}

	default boolean isEnumType() {
		return false;
	}

	default boolean isArrayType() {
		return false;
	}

	default String annotationType() {
		return null;
	}

	default String getClass(String name) {
		return null;
	}

	default String getString(String name) {
		return null;
	}

	default Boolean getBoolean(String name) {
		return null;
	}

	default Number getNumber(String name) {
		return null;
	}

	default EnumValue getEnum(String name) {
		return null;
	}

	default String[] getClassArray(String name) {
		return null;
	}

	default String[] getStringArray(String name) {
		return null;
	}

	default Boolean[] getBooleanArray(String name) {
		return null;
	}

	default Number[] getNumberArray(String name) {
		return null;
	}

	default EnumValue[] getEnumArray(String name) {
		return null;
	}

	default AnnotationMetadata[] getAnnotationArray(String name) {
		return null;
	}

	default AnnotationMetadata getAnnotation(String name) {
		return null;
	}

	default Class<? extends Annotation> runtimeAnnotationType() {
		return null;
	}

	default AnnotationAttributes asRuntimeAnnotationAttributes() {
		return null;
	}

	default <A extends Annotation> A asRuntimeAnnotation() {
		return null;
	}

	default <E extends Enum<E>> E asRuntimeEnumValue() {
		return null;
	}

	default Object asRuntimeArrayValue() {
		return null;
	}

	default Object asRuntimeSimpleValue() {
		return null;
	}

	default Object asRuntimeValue() {
		return asRuntimeValue(this);
	}

	static Object asRuntimeValue(@Nonnull AnnotationMetadata metadata) {
		if (metadata.isSimpleType()) {
			return metadata.asRuntimeSimpleValue();
		} else if (metadata.isEnumType()) {
			return metadata.asRuntimeEnumValue();
		} else if (metadata.isArrayType()) {
			return metadata.asRuntimeArrayValue();
		} else if (metadata.isAnnotationType()) {
			return metadata.asRuntimeAnnotation();
		}
		return null;
	}
}
