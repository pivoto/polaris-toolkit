package io.polaris.core.guid;

import io.polaris.core.spi.Spi;
import io.polaris.core.spi.StatefulSpiLoadable;

/**
 * @author Qt
 * @since Jul 16, 2024
 */
@Spi("全局唯一标识符节点策略")
public interface GuidNodeStrategyProvider extends StatefulSpiLoadable {

	GuidNodeStrategy get();

	GuidNodeStrategy get(String app);

}
