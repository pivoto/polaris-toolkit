package io.polaris.core.jdbc.annotation.processing;

import java.lang.annotation.Annotation;

import javax.annotation.Nonnull;

import io.polaris.core.annotation.processing.AptAnnotationAttributes;
import io.polaris.core.jdbc.annotation.Id;

/**
 * @author Qt
 * @since Oct 08, 2025
 */
@SuppressWarnings("ClassExplicitlyAnnotation")
class IdAnnotationAttributes implements Id {

	private final AptAnnotationAttributes annotationAttributes;

	IdAnnotationAttributes(@Nonnull AptAnnotationAttributes annotationAttributes) {
		this.annotationAttributes = annotationAttributes;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return Id.class;
	}

	@Override
	public boolean auto() {
		Boolean val = annotationAttributes.getBoolean("auto");
		return val != null && val;
	}

	@Override
	public String seqName() {
		String val = annotationAttributes.getString("seqName");
		return val == null ? "" : val;
	}

	@Override
	public String sql() {
		String val = annotationAttributes.getString("sql");
		return val == null ? "" : val;
	}

}
