package io.polaris.core.classpath.reader.annotation;

import javax.annotation.Nullable;

import io.polaris.core.asm.internal.AsmTypes;
import io.polaris.core.classpath.reader.AnnotationMetadata;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since Jan 27, 2026
 */
@ToString
@EqualsAndHashCode
public class AnnotationEnumValue implements AnnotationMetadata {
	private final String descriptor;
	private final String value;
	private final Type type;
	@Nullable
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	AnnotationValue owner;

	public AnnotationEnumValue(String descriptor, String value) {
		this.descriptor = descriptor;
		this.value = value;
		this.type = Type.getType(descriptor);
	}

	public String getValue() {
		return value;
	}

	public String getClassName() {
		return type.getClassName();
	}

	@Override
	public String dump() {
		return type.getClassName() + "." + value;
	}

	@Override
	public String dump(int level) {
		return dump();
	}

	@Override
	public boolean isEnumType() {
		return true;
	}

	@Override
	public <E extends Enum<E>> E asRuntimeEnumValue() {
		try {
			@SuppressWarnings("unchecked")
			Class<E> enumClass = (Class<E>) AsmTypes.getClass(type);
			return Enum.valueOf(enumClass, value);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

}
