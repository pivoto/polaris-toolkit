package io.polaris.mybatis.enhance.example;

import javax.annotation.Nonnull;

import io.polaris.mybatis.consts.DbEnum;

/**
 * @author Qt
 * @since Apr 17, 2026
 */
public enum ExampleDbEnum implements DbEnum<String> {

	ONE("1"),
	TWO("2"),
	THREE("3");

	private String code;

	private ExampleDbEnum(String code) {
		this.code = code;
	}

	@Nonnull
	@Override
	public String getCode() {
		return code;
	}
}
