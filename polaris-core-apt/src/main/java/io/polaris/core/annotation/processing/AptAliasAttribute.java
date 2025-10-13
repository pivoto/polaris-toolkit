package io.polaris.core.annotation.processing;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

/**
 * @author Qt
 * @since Oct 13, 2025
 */
public class AptAliasAttribute {
	private final String value;
	private final DeclaredType annotation;

	public AptAliasAttribute(String value, DeclaredType annotation) {
		this.value = value;
		this.annotation = annotation;
	}

	public AptAliasAttribute(String value) {
		this(value, null);
	}

	public String value() {
		return value;
	}

	public DeclaredType annotation() {
		return annotation;
	}
}
