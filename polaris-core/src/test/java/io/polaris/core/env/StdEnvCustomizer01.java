package io.polaris.core.env;

import java.io.File;
import java.net.URL;

import io.polaris.core.consts.StdKeys;
import io.polaris.core.io.Consoles;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;

/**
 * @author Qt
 * @since Apr 23, 2024
 */
public class StdEnvCustomizer01 implements StdEnvCustomizer {

	public static final Logger log = Loggers.of(StdEnvCustomizer01.class);

	@Override
	public void customize(StdEnv stdEnv) {
		log.info("StdEnvCustomizer01.customize");
		URL url = StdEnvCustomizer01.class.getProtectionDomain().getCodeSource().getLocation();
		if ("file".equals(url.getProtocol())) {
			String file = url.getFile().replaceFirst("/+$", "").replace("\\", "/");
			if (file.endsWith("target/test-classes")) {
				File dir = new File(file);
				dir.mkdirs();
				if (dir.exists()) {
					log.info("配置：{}={}", StdKeys.BYTECODE_GENERATOR_TMPDIR, dir.getAbsolutePath());
					stdEnv.setDefaults(StdKeys.BYTECODE_GENERATOR_TMPDIR, dir.getAbsolutePath());
				}
			}
		}

	}
}
