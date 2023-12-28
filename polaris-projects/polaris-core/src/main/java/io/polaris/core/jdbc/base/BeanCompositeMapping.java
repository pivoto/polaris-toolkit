package io.polaris.core.jdbc.base;

import io.polaris.core.string.Strings;
import lombok.Getter;

/**
 * @author Qt
 * @since 1.8,  Dec 28, 2023
 */
@Getter
public class BeanCompositeMapping<T> {

	private String property;
	private BeanMapping<T> mapping;

	public BeanCompositeMapping() {
	}

	public BeanCompositeMapping(String property, BeanMapping<T> mapping) {
		this.property = property;
		this.mapping = mapping;
	}

	public boolean isValid() {
		return Strings.isNotBlank(property) && mapping != null;
	}

	public BeanCompositeMapping<T> property(String property) {
		this.property = property;
		return this;
	}

	public BeanCompositeMapping<T> mapping(BeanMapping<T> mapping) {
		this.mapping = mapping;
		return this;
	}
}
