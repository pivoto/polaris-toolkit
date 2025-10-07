package io.polaris.core.jdbc.annotation.processing;

import java.lang.annotation.Annotation;

import javax.annotation.Nonnull;

import io.polaris.core.annotation.processing.AptAnnotationAttributes;
import io.polaris.core.jdbc.annotation.Expression;

/**
 * @author Qt
 * @since Oct 08, 2025
 */
@SuppressWarnings("ClassExplicitlyAnnotation")
class ExpressionAnnotationAttributes implements Expression {

	private final AptAnnotationAttributes annotationAttributes;

	ExpressionAnnotationAttributes(@Nonnull AptAnnotationAttributes annotationAttributes) {
		this.annotationAttributes = annotationAttributes;
	}


	@Override
	public Class<? extends Annotation> annotationType() {
		return Expression.class;
	}

	@Override
	public String value() {
		return annotationAttributes.getString("value");
	}

	@Override
	public boolean selectable() {
		Boolean val = annotationAttributes.getBoolean("selectable");
		return val == null || val;
	}

	@Override
	public String tableAliasPlaceholder() {
		String val = annotationAttributes.getString("tableAliasPlaceholder");
		return val == null ? "$T." : val;
	}

	@Override
	public String jdbcType() {
		String val = annotationAttributes.getString("jdbcType");
		return val == null ? "" : val;
	}
}
