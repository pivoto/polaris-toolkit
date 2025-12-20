package io.polaris.core.jdbc.sql.query;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Qt
 * @since Dec 20, 2025
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class StringValue implements Serializable {
	private static final long serialVersionUID = 1L;
	private String value;
	private char escape = 0;
	private boolean like = false;

	public StringValue() {
	}

	public StringValue(String value) {
		this.value = value;
	}

	public StringValue(String value, char escape, boolean like) {
		this.value = value;
		this.escape = escape;
		this.like = like;
	}
}
