package io.polaris.core.jdbc.annotation.processing;

import java.lang.annotation.Annotation;

import javax.annotation.Nonnull;

import io.polaris.core.annotation.processing.AptAnnotationAttributes;
import io.polaris.core.jdbc.annotation.Table;

/**
 * @author Qt
 * @since Oct 08, 2025
 */
@SuppressWarnings("ClassExplicitlyAnnotation")
class TableAnnotationAttributes implements Table {

	private final AptAnnotationAttributes annotationAttributes;

	TableAnnotationAttributes(@Nonnull AptAnnotationAttributes annotationAttributes) {
		this.annotationAttributes = annotationAttributes;
	}
	@Override
	public Class<? extends Annotation> annotationType() {
		return Table.class;
	}

	@Override
	public String value() {
		return annotationAttributes.getString("value");
	}

	@Override
	public String alias() {
		String val = annotationAttributes.getString("alias");
		return val == null ? "" : val;
	}

	@Override
	public String schema() {
		String val = annotationAttributes.getString("schema");
		return val == null ? "" : val;
	}

	@Override
	public String catalog() {
		String val = annotationAttributes.getString("catalog");
		return val == null ? "" : val;
	}

	@Override
	public String metaSuffix() {
		String val = annotationAttributes.getString("metaSuffix");
		return val == null ? "Meta" : val;
	}

	@Override
	public boolean sqlGenerated() {
		Boolean val = annotationAttributes.getBoolean("sqlGenerated");
		return val == null || val;
	}

	@Override
	public String sqlSuffix() {
		String val = annotationAttributes.getString("sqlSuffix");
		return val == null ? "Sql" : val;
	}

}
