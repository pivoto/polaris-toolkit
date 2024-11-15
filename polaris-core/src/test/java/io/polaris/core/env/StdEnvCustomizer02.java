package io.polaris.core.env;

import io.polaris.core.io.Consoles;

/**
 * @author Qt
 * @since  Apr 23, 2024
 */
public class StdEnvCustomizer02 implements StdEnvCustomizer {
	@Override
	public void customize(StdEnv stdEnv) {
		Consoles.println("StdEnvCustomizer02.customize");

	}
}
