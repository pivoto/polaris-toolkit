package io.polaris.core.jdbc.annotation.processing;

import java.lang.annotation.Annotation;

import javax.annotation.Nonnull;

import io.polaris.core.annotation.processing.AptAnnotationAttributes;
import io.polaris.core.jdbc.annotation.Column;

/**
 * @author Qt
 * @since Oct 08, 2025
 */
@SuppressWarnings("ClassExplicitlyAnnotation")
class ColumnAnnotationAttributes implements Column {

	private final AptAnnotationAttributes annotationAttributes;

	ColumnAnnotationAttributes(@Nonnull AptAnnotationAttributes annotationAttributes) {
		this.annotationAttributes = annotationAttributes;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return Column.class;
	}


	@Override
	public String value() {
		String val = annotationAttributes.getString("value");
		return val == null ? "" : val;
	}

	@Override
	public boolean ignored() {
		Boolean val = annotationAttributes.getBoolean("ignored");
		return val != null && val;
	}

	@Override
	public String jdbcType() {
		String val = annotationAttributes.getString("jdbcType");
		return val == null ? "" : val;
	}

	@Override
	public boolean nullable() {
		Boolean val = annotationAttributes.getBoolean("nullable");
		return val == null || val;
	}

	@Override
	public boolean insertable() {
		Boolean val = annotationAttributes.getBoolean("insertable");
		return val == null || val;
	}

	@Override
	public boolean updatable() {
		Boolean val = annotationAttributes.getBoolean("updatable");
		return val == null || val;
	}

	@Override
	public String updateDefault() {
		String val = annotationAttributes.getString("updateDefault");
		return val == null ? "" : val;
	}

	@Override
	public String insertDefault() {
		String val = annotationAttributes.getString("insertDefault");
		return val == null ? "" : val;
	}

	@Override
	public String updateDefaultSql() {
		String val = annotationAttributes.getString("updateDefaultSql");
		return val == null ? "" : val;
	}

	@Override
	public String insertDefaultSql() {
		String val = annotationAttributes.getString("insertDefaultSql");
		return val == null ? "" : val;
	}

	@Override
	public boolean version() {
		Boolean val = annotationAttributes.getBoolean("version");
		return val != null && val;
	}

	@Override
	public boolean logicDeleted() {
		Boolean val = annotationAttributes.getBoolean("logicDeleted");
		return val != null && val;
	}

	@Override
	public boolean createTime() {
		Boolean val = annotationAttributes.getBoolean("createTime");
		return val != null && val;
	}

	@Override
	public boolean updateTime() {
		Boolean val = annotationAttributes.getBoolean("updateTime");
		return val != null && val;
	}
}
