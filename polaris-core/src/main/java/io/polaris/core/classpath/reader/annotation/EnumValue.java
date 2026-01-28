package io.polaris.core.classpath.reader.annotation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Qt
 * @since Jan 28, 2026
 */
@Getter
@EqualsAndHashCode
@ToString
public class EnumValue {

	private final String className;
	private final String value;

	public EnumValue(String className, String value) {
		this.className = className;
		this.value = value;
	}
}
