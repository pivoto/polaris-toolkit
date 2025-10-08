package io.polaris.core.annotation.processing;

import java.util.Objects;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.type.TypeMirror;

/**
 * @author Qt
 * @since Oct 08, 2025
 */
class InternalAnnotationValue implements AnnotationValue {
	private final AnnotationValue raw;
	private final Object value;

	InternalAnnotationValue(AnnotationValue raw, Object value) {
		this.raw = raw;
		this.value = value;
	}


	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return Objects.toString(value);
	}

	@Override
	public <R, P> R accept(AnnotationValueVisitor<R, P> v, P p) {
		return v.visit(this, p);
	}
}
