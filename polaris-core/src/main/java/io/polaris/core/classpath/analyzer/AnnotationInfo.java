package io.polaris.core.classpath.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import io.polaris.core.collection.ObjectArrays;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since Aug 12, 2025
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NotThreadSafe
public class AnnotationInfo {
	private final Type type;
	/** annotationDesc， 如 Ljavax/validation/constraints/NotNull; */
	private final String descriptor;
	private final boolean visible;
	private final Map<String, Value> values = new HashMap<>();


	public AnnotationInfo(String descriptor, boolean visible) {
		this.descriptor = descriptor;
		this.type = Type.getType(descriptor);
		this.visible = visible;
	}

	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append(type.getInternalName()).append("(");
		if (!values.isEmpty()) {
			for (Map.Entry<String, Value> entry : values.entrySet()) {
				sb.append(entry.getKey()).append(":").append(entry.getValue().dump()).append(", ");
			}
			sb.delete(sb.length() - 2, sb.length());
		}
		return sb.append(")").toString();
	}

	public abstract static class Value {
		public abstract String dump();
	}

	@Getter
	@ToString
	@EqualsAndHashCode
	public static class SimpleValue extends Value {
		private final Object value;

		public SimpleValue(Object value) {
			this.value = value;
		}

		@Override
		public String dump() {
			return ObjectArrays.toString(value);
		}
	}

	@Getter
	@ToString
	@EqualsAndHashCode
	public static class EnumValue extends Value {
		private final String descriptor;
		private final String value;
		private final Type type;

		public EnumValue(String descriptor, String value) {
			this.descriptor = descriptor;
			this.value = value;
			this.type = Type.getType(descriptor);
		}

		@Override
		public String dump() {
			return type.getInternalName() + "." + value;
		}
	}

	@Getter
	@ToString
	@EqualsAndHashCode
	public static class AnnotationValue extends Value {
		private final AnnotationInfo annotation;

		public AnnotationValue(AnnotationInfo annotation) {
			this.annotation = annotation;
		}

		@Override
		public String dump() {
			if (annotation != null) {
				return annotation.dump();
			}
			return "";
		}
	}

	@Getter
	@ToString
	@EqualsAndHashCode
	public static class ArrayValue extends Value {
		private final List<Value> array;

		public ArrayValue(List<Value> array) {
			this.array = array;
		}

		@Override
		public String dump() {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			if (array != null && !array.isEmpty()) {
				for (Value value : array) {
					sb.append(value.dump()).append(", ");
				}
				sb.deleteCharAt(sb.length() - 2);
			}
			sb.append("]");
			return sb.toString();
		}
	}


}
