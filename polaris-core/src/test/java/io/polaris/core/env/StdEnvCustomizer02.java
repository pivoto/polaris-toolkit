package io.polaris.core.env;

import io.polaris.core.io.Consoles;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;

/**
 * @author Qt
 * @since Apr 23, 2024
 */
public class StdEnvCustomizer02 implements StdEnvCustomizer {
	public static final Logger log = Loggers.of(StdEnvCustomizer02.class);

	@Override
	public void customize(StdEnv stdEnv) {
		log.info("StdEnvCustomizer02.customize");

	}
}
