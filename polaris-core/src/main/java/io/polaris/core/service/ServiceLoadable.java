package io.polaris.core.service;

import io.polaris.core.annotation.Alternative;
import io.polaris.core.spi.Spi;
import io.polaris.core.spi.SpiLoadable;
import io.polaris.core.spi.SpiLoader;

/**
 * 标识为一个SPI服务类。可通过{@link SpiLoader}加载
 * <p>
 * 仅作标识，无其他限制
 *
 * @author Qt
 * @since Sep 26, 2024
 */
@Spi("标识为一个SPI服务类")
@Deprecated
@Alternative(SpiLoadable.class)
public interface ServiceLoadable extends SpiLoadable{
}
