package io.polaris.core.env;

import io.polaris.core.spi.Spi;
import io.polaris.core.spi.StatefulSpiLoadable;

/**
 * @author Qt
 * @since Apr 23, 2024
 */
@Spi("标准环境属性定制器")
public interface StdEnvCustomizer extends StatefulSpiLoadable {

	void customize(StdEnv stdEnv);

}
