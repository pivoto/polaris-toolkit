package io.polaris.core.jdbc.sql.node;

import io.polaris.core.string.Strings;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Qt
 * @since  Aug 29, 2023
 */
public class DefaultVarNameGenerator implements VarNameGenerator {

	private final AtomicInteger index = new AtomicInteger();
	private final String prefix;

	public DefaultVarNameGenerator() {
		this("_");
	}

	public DefaultVarNameGenerator(String prefix) {
		this.prefix = Strings.coalesce(prefix, "_");
	}


	@Override
	public String generate() {
		return prefix + (index.getAndIncrement());
	}
}
