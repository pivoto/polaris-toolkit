package io.polaris.core.classpath.reader.annotation;

import javax.annotation.Nullable;

import io.polaris.core.asm.internal.AsmTypes;
import io.polaris.core.classpath.reader.AnnotationMetadata;
import io.polaris.core.collection.ObjectArrays;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since Jan 27, 2026
 */
@ToString
@EqualsAndHashCode
public class AnnotationSimpleValue implements AnnotationMetadata {
	private final Object value;
	@Nullable
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	AnnotationValue owner;

	public AnnotationSimpleValue(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String dump() {
		return ObjectArrays.toString(value);
	}

	@Override
	public String dump(int level) {
		return dump();
	}

	@Override
	public boolean isSimpleType() {
		return true;
	}

	@Override
	public Object asRuntimeSimpleValue() {
		if (value instanceof Type) {
			try {
				return AsmTypes.getClass((Type) value);
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
		return value;
	}

}
