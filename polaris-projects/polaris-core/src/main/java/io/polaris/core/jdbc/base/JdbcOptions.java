package io.polaris.core.jdbc.base;

import javax.annotation.Nullable;

import io.polaris.core.jdbc.base.annotation.Options;
import lombok.Data;

/**
 * @author Qt
 * @since 1.8,  Feb 06, 2024
 */
@Data
public class JdbcOptions {
	private boolean useGeneratedKeys = false;
	private int fetchSize = 100;
	private int timeout = -1;
	private int maxRows = -1;
	private String[] keyProperties;
	private String[] keyColumns;

	public static JdbcOptions ofDefault() {
		return new JdbcOptions();
	}

	public static JdbcOptions of(@Nullable Options options) {
		JdbcOptions jdbcOptions = new JdbcOptions();
		if (options != null) {
			jdbcOptions.setUseGeneratedKeys(options.useGeneratedKeys());
			jdbcOptions.setFetchSize(options.fetchSize());
			jdbcOptions.setTimeout(options.timeout());
			jdbcOptions.setKeyProperties(options.keyProperty());
			jdbcOptions.setKeyColumns(options.keyColumn());
		}
		return jdbcOptions;
	}
}
