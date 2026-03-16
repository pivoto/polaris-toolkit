package io.polaris.json;

import io.polaris.core.spi.Spi;
import io.polaris.core.spi.StatefulSpiLoadable;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Qt
 * @since Feb 04, 2024
 */
@Spi("Jackson自定义配置")
public interface JacksonCustomizer extends StatefulSpiLoadable {

	void customize(ObjectMapper mapper);

}
