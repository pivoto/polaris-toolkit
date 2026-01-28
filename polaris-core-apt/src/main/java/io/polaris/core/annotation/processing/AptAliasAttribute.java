package io.polaris.core.annotation.processing;

import javax.lang.model.type.DeclaredType;

/**
 * @author Qt
 * @since Oct 13, 2025
 */
public class AptAliasAttribute {
	private final String attribute;
	private final DeclaredType annotationType;

	public AptAliasAttribute(String attribute, DeclaredType annotationType) {
		this.attribute = attribute;
		this.annotationType = annotationType;
	}

	public AptAliasAttribute(String attribute) {
		this(attribute, null);
	}

	public String attribute() {
		return attribute;
	}

	public DeclaredType annotationType() {
		return annotationType;
	}
}
