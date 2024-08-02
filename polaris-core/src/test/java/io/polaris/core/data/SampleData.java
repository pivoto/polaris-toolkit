package io.polaris.core.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SampleData {
	private int intValue;
	private String name;

	public SampleData setIntValue(int intValue) {
		this.intValue = intValue;
		return this;
	}

	public SampleData setName(String name) {
		this.name = name;
		return this;
	}
}
