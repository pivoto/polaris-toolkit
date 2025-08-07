package io.polaris.core.service;

/**
 * 标识为一个有状态的SPI服务类。可通过{@link StatefulServiceLoader}加载
 * <p>
 * 仅作标识，无其他限制
 *
 * @author Qt
 * @since Sep 26, 2024
 */
@Spi
public interface StatefulServiceLoadable extends ServiceLoadable{
}
