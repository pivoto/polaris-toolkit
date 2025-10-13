package io.polaris.core.jdbc.annotation.processing;

import java.lang.annotation.Annotation;

import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;

import io.polaris.core.annotation.processing.AptAnnotationAttributes;
import io.polaris.core.jdbc.annotation.ColumnProperty;

/**
 * @author Qt
 * @since Oct 08, 2025
 */
@SuppressWarnings("ClassExplicitlyAnnotation")
class ColumnPropertyAnnotationAttributes implements ColumnProperty {

	private final AptAnnotationAttributes annotationAttributes;

	ColumnPropertyAnnotationAttributes(@Nonnull AptAnnotationAttributes annotationAttributes) {
		this.annotationAttributes = annotationAttributes;
	}


	@Override
	public Class<? extends Annotation> annotationType() {
		return ColumnProperty.class;
	}

	@Override
	public String key() {
		return annotationAttributes.getString("key");
	}

	@Override
	public Type type() {
		VariableElement type = annotationAttributes.getEnum("type");
		return type == null ? Type.STRING : Type.valueOf(type.getSimpleName().toString());
	}

	public String value() {
		Type type = type();
		switch (type) {
			case STRING: {
				String val = annotationAttributes.getString("stringValue");
				return val == null ? "" : val;
			}
			case CLASS: {
				DeclaredType classValue = annotationAttributes.getClass("classValue");
				if (classValue == null) {
					return "";
				}
				Element classValueElement = classValue.asElement();
				if (!(classValueElement instanceof TypeElement)) {
					return "";
				}
				TypeElement element = (TypeElement) classValueElement;
				return element.getQualifiedName().toString();
			}
			case INT:
				return String.valueOf(intValue());
			case BOOLEAN:
				return String.valueOf(booleanValue());
			case LONG:
				return String.valueOf(longValue());
			case DOUBLE:
				return String.valueOf(doubleValue());
			default:
				return "";
		}
	}

	@Override
	public String stringValue() {
		String val = annotationAttributes.getString("stringValue");
		return val == null ? "" : val;
	}

	@Override
	public Class<?> classValue() {
		DeclaredType classValue = annotationAttributes.getClass("classValue");
		if (classValue == null) {
			return void.class;
		}
		TypeElement element = (TypeElement) classValue.asElement();
		try {
			return Class.forName(element.getQualifiedName().toString());
		} catch (Throwable e) {
			return void.class;
		}
	}

	@Override
	public int intValue() {
		Integer intValue = annotationAttributes.getInteger("intValue");
		return intValue == null ? 0 : intValue;
	}

	@Override
	public boolean booleanValue() {
		Boolean booleanValue = annotationAttributes.getBoolean("booleanValue");
		return booleanValue != null && booleanValue;
	}

	@Override
	public long longValue() {
		Long longValue = annotationAttributes.getLong("longValue");
		return longValue == null ? 0 : longValue;
	}

	@Override
	public double doubleValue() {
		Double doubleValue = annotationAttributes.getDouble("doubleValue");
		return doubleValue == null ? 0 : doubleValue;
	}
}
