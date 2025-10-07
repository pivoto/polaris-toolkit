package io.polaris.core.lang.annotation;

import java.lang.annotation.Annotation;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Qt
 * @since Oct 07, 2025
 */
@EqualsAndHashCode
@ToString
public class AliasAttribute {
	private final String value;
	private final Class<? extends Annotation> annotation;

	public AliasAttribute(String value, Class<? extends Annotation> annotation) {
		this.value = value == null ? "" : value.trim();
		this.annotation = annotation == null ? Annotation.class : annotation;
	}

	public AliasAttribute(String value) {
		this(value, null);
	}

	public String value() {
		return value;
	}

	public Class<? extends Annotation> annotation() {
		return annotation;
	}
}
