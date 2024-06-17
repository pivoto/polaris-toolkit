package io.polaris.core.jdbc.base;

import io.polaris.core.string.Strings;
import lombok.Getter;

/**
 * @author Qt
 * @since  Dec 28, 2023
 */
@Getter
public class BeanPropertyMapping {

	private String property;
	private String column;

	public BeanPropertyMapping() {
	}

	public BeanPropertyMapping(String property, String column) {
		this.property = property;
		this.column = column;
	}

	public boolean isValid() {
		return Strings.isNotBlank(column) && Strings.isNotBlank(property);
	}

	public BeanPropertyMapping property(String property) {
		this.property = property;
		return this;
	}

	public BeanPropertyMapping column(String column) {
		this.column = column;
		return this;
	}
}
