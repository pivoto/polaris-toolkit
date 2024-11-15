package io.polaris.core.env;

import io.polaris.core.io.Consoles;

/**
 * @author Qt
 * @since  Apr 23, 2024
 */
public class StdEnvCustomizer01 implements StdEnvCustomizer {
	@Override
	public void customize(StdEnv stdEnv) {
		Consoles.println("StdEnvCustomizer01.customize");
	}
}
