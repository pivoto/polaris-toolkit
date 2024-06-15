package io.polaris.core.jdbc.sql.node;

/**
 * @author Qt
 * @since  Aug 11, 2023
 */
public interface VarNameGenerator {

	String generate();

	static VarNameGenerator newInstance() {
		return new DefaultVarNameGenerator();
	}

	static VarNameGenerator newInstance(String prefix) {
		return new DefaultVarNameGenerator(prefix);
	}
}
