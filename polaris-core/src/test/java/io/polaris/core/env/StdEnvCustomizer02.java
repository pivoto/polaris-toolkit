package io.polaris.core.env;

import io.polaris.core.TestConsole;

/**
 * @author Qt
 * @since  Apr 23, 2024
 */
public class StdEnvCustomizer02 implements StdEnvCustomizer {
	@Override
	public void customize(StdEnv stdEnv) {
		TestConsole.println("StdEnvCustomizer02.customize");

	}
}
