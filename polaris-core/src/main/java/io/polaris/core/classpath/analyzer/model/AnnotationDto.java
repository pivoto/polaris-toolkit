package io.polaris.core.classpath.analyzer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.polaris.core.classpath.analyzer.AnnotationInfo;
import io.polaris.core.collection.ObjectArrays;
import io.polaris.core.map.Maps;
import lombok.Data;


/**
 * @author Qt
 * @since Aug 21, 2025
 */
@Data
public class AnnotationDto {
	/** classInternalName */
	private String name;
	private Map<String, Value> values = new HashMap<>();

	@Data
	public static class Value {
		private Object simpleValue;
		private String enumValue;
		private List<Value> arrayValue;
		private AnnotationDto annotation;

		public static Value from(AnnotationInfo.Value value) {
			Value rs = new Value();
			if (value instanceof AnnotationInfo.SimpleValue) {
				AnnotationInfo.SimpleValue simpleValue = (AnnotationInfo.SimpleValue) value;
				rs.simpleValue = ObjectArrays.toString(simpleValue.getValue());
			} else if (value instanceof AnnotationInfo.EnumValue) {
				AnnotationInfo.EnumValue enumValue = (AnnotationInfo.EnumValue) value;
				rs.enumValue = enumValue.dump();
			} else if (value instanceof AnnotationInfo.ArrayValue) {
				AnnotationInfo.ArrayValue arrayValue = (AnnotationInfo.ArrayValue) value;
				rs.arrayValue = new ArrayList<>();
				for (AnnotationInfo.Value v : arrayValue.getArray()) {
					rs.arrayValue.add(Value.from(v));
				}
			} else if (value instanceof AnnotationInfo.AnnotationValue) {
				AnnotationInfo.AnnotationValue annotationValue = (AnnotationInfo.AnnotationValue) value;
				rs.annotation = AnnotationDto.from(annotationValue.getAnnotation());
			}
			return rs;
		}
	}

	public static AnnotationDto from(AnnotationInfo annotationInfo) {
		AnnotationDto annotationDto = new AnnotationDto();
		annotationDto.setName(annotationInfo.getType().getInternalName());
		if (Maps.isNotEmpty(annotationInfo.getValues())) {
			annotationInfo.getValues().forEach((key, value) -> {
				annotationDto.getValues().put(key, Value.from(value));
			});
		}
		return annotationDto;
	}
}
