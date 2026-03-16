package io.polaris.json;

import io.polaris.core.spi.Spi;
import io.polaris.core.spi.StatefulSpiLoadable;

/**
 * @author Qt
 */
@Spi("Fastjson自定义配置")
public interface FastjsonCustomizer extends StatefulSpiLoadable {

	void customize();

}
