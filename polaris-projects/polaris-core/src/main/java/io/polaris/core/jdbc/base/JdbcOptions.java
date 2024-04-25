package io.polaris.core.jdbc.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import io.polaris.core.jdbc.annotation.Options;
import lombok.Data;

/**
 * @author Qt
 * @since 1.8,  Feb 06, 2024
 */
@Data
public class JdbcOptions {
	private static final Map<Class<? extends ParameterPreparer>, ParameterPreparer> caches = new ConcurrentHashMap<>();
	private boolean useGeneratedKeys = false;
	private int fetchSize = 100;
	private int timeout = -1;
	private int maxRows = -1;
	private String[] keyProperties;
	private String[] keyColumns;
	private ParameterPreparer parameterPreparer = DefaultParameterPreparer.INSTANCE;

	static {
		caches.put(DefaultParameterPreparer.class, DefaultParameterPreparer.INSTANCE);
	}

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
			Class<? extends ParameterPreparer> clazz = options.parameterPreparer();
			if (clazz == DefaultParameterPreparer.class) {
				jdbcOptions.setParameterPreparer(DefaultParameterPreparer.INSTANCE);
			} else {
				ParameterPreparer preparer = caches.computeIfAbsent(clazz, k -> {
					try {
						return k.newInstance();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
				jdbcOptions.setParameterPreparer(preparer);
			}
		}
		return jdbcOptions;
	}

}
