package io.polaris.core.classpath.reader.annotation;

import java.util.List;

import javax.annotation.Nullable;

import io.polaris.core.classpath.reader.AnnotationMetadata;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Qt
 * @since Jan 27, 2026
 */
@ToString
@EqualsAndHashCode
public class AnnotationArrayValue implements AnnotationMetadata {
	private final List<AnnotationMetadata> array;
	@Nullable
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	AnnotationValue owner;

	public AnnotationArrayValue(List<AnnotationMetadata> array) {
		this.array = array;
	}

	public List<AnnotationMetadata> getArray() {
		return array;
	}

	@Override
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (array != null && !array.isEmpty()) {
			for (AnnotationMetadata value : array) {
				sb.append(value.dump()).append(", ");
			}
			sb.deleteCharAt(sb.length() - 2);
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public String dump(int level) {
		return dump();
	}


	@Override
	public boolean isArrayType() {
		return true;
	}

	@Override
	public Object asRuntimeArrayValue() {
		if (array != null && !array.isEmpty()) {
			Object[] values = new Object[array.size()];
			for (int i = 0; i < array.size(); i++) {
				values[i] = array.get(i).asRuntimeValue();
			}
			return values;
		}
		return null;
	}


}
