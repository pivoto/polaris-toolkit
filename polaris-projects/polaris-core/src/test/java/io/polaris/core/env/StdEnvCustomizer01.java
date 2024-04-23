package io.polaris.core.env;

import io.polaris.core.TestConsole;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Qt
 * @since 1.8,  Apr 23, 2024
 */
public class StdEnvCustomizer01 implements StdEnvCustomizer {
	@Override
	public void customize(StdEnv stdEnv) {
		TestConsole.println("StdEnvCustomizer01.customize");
	}
}
